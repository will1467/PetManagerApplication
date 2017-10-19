package com.example.williamrussa3.petmanagerapplication;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FeedingActivity extends AppCompatActivity implements DialogInterface.OnDismissListener  {

    private static Pet mPet;
    private int mPetIndex;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);
        mPet = getIntent().getParcelableExtra("pet_object");
        mPetIndex = getIntent().getIntExtra("pet_index",0);
        MainActivity.PetList = getPetListFromPreferences();
        mPet = MainActivity.PetList.get(mPetIndex);
        AddFeedingTime();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e("Mssg", "in onStop");
        MainActivity.PetList.remove(mPetIndex);
        MainActivity.PetList.add(mPet);
        SavePreferences();
    }


    private void SavePreferences(){
        Log.e("Mssg", "saving");
        SharedPreferences mPrefs = getSharedPreferences("PET_DATA",Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MainActivity.PetList);
        prefsEditor.putString("pet_array", json);
        prefsEditor.commit();
    }

    private ArrayList<Pet> getPetListFromPreferences() {
        SharedPreferences mPrefs = getSharedPreferences("PET_DATA",Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = mPrefs.getString("pet_array","");

        if(jsonData.isEmpty()){
            return new ArrayList<Pet>();
        }

        Type type = new TypeToken<ArrayList<Pet>>(){}.getType();
        ArrayList<Pet> Pets = gson.fromJson(jsonData,type);
        return Pets;
    }

    public void showTimePicker(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.e("mssg","onDismiss called");
        AddFeedingTime();
    }

    private void AddFeedingTime() {
        ArrayList<String> expList = new ArrayList<String>();
        ListView feedingTimesListView = (ListView) findViewById(R.id.feedingTimesList);
        Log.e("Mssg", Integer.toString(mPet.GetFeedingTimeLength()));
        for (int index = 0; index < mPet.GetFeedingTimeLength(); index++) {
            ScheduledTime scheduledTime = mPet.GetFeedingTime(index);
            Date date = scheduledTime.GetDate();
            String name = "Feeding Time " + (index + 1);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            if (c.get(Calendar.MINUTE) < 10) {
                expList.add(name + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + "0" + c.get(Calendar.MINUTE));
            } else {
                expList.add(name + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
            }
        }
            if (itemsAdapter == null) {
                itemsAdapter = new ArrayAdapter<String>(this, R.layout.row_feeding, R.id.feeding_title, expList);
                feedingTimesListView.setAdapter(itemsAdapter);
            } else {
                itemsAdapter.clear(); // Make sure there is no old things left over
                itemsAdapter.addAll(expList);
                itemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
            }
    }

    public void deleteTask(View view) {
        View parent = (View) view.getParent();
        TextView timeTextView = (TextView) parent.findViewById(R.id.feeding_title);
        String value = String.valueOf(timeTextView.getText());
        int indexToBeDeleted = (Integer.parseInt(value.substring(13, 14)) - 1);
        mPet.RemoveFeedingTime(indexToBeDeleted);
        AddFeedingTime();
        RemoveAlarm(indexToBeDeleted);
    }

    private void RemoveAlarm(int indexToBeDeleted) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(),
                AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getApplicationContext(), indexToBeDeleted, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager.cancel(pendingIntent);
    }



    public static class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }



    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Log.e("mssg","onTimeSet called");
        Toast.makeText(getContext(), "Feeding time has been scheduled", Toast.LENGTH_LONG).show();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);
        Date date = calendar.getTime();
        int mFeedingTimeId = mPet.SetFeedingTime(date);
        ScheduleFeeding(hourOfDay, minute, mFeedingTimeId);
    }

    private void ScheduleFeeding(int hourOfDay, int minute, int mFeedingTimeId) {
        AlarmManager alarmMgr;
        PendingIntent alarmIntent;

        alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getContext(), AlarmReceiver.class);
        intent.putExtra("Message", mPet.GetName() + " needs feeding");
        alarmIntent = PendingIntent.getBroadcast(getContext(), mFeedingTimeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute);

        alarmMgr.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),alarmIntent);

    }

}
}
