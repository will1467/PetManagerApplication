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
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class WalkingActivity extends AppCompatActivity implements DialogInterface.OnDismissListener{

    private static Pet mPet;
    private int mPetIndex;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);
        mPet = getIntent().getParcelableExtra("pet_object");
        mPetIndex = getIntent().getIntExtra("pet_index",0);
        MainActivity.PetList = getPetListFromPreferences();
        mPet = MainActivity.PetList.get(mPetIndex);
        PopulateWalkingTimeList();
    }

    @Override
    protected void onStop(){

        super.onStop();
        MainActivity.PetList.remove(mPetIndex);
        MainActivity.PetList.add(mPet);
        SavePreferences();
    }


    private void SavePreferences(){

        SharedPreferences mPrefs = getSharedPreferences("PET_DATA", Context.MODE_PRIVATE);
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

        DialogFragment newFragment = new WalkingActivity.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        PopulateWalkingTimeList();
    }

    private void PopulateWalkingTimeList() {

        ArrayList<String> walkingTimesList = new ArrayList<String>();
        ListView walkingTimesListView = (ListView) findViewById(R.id.walkingTimesList);

        for (int index = 0; index < mPet.GetWalkingTimeArrayLength(); index++) {
            ScheduledTime scheduledTime = mPet.GetWalkingTime(index);
            Date date = scheduledTime.GetDate();
            String name = "Walking Time " + (index + 1);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            if (c.get(Calendar.MINUTE) < 10) {
                walkingTimesList.add(name + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + "0" + c.get(Calendar.MINUTE));
            } else {
                walkingTimesList.add(name + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
            }
        }
        if (itemsAdapter == null) {
            itemsAdapter = new ArrayAdapter<String>(this, R.layout.row_adapter, R.id.task_title, walkingTimesList);
            walkingTimesListView.setAdapter(itemsAdapter);
        } else {
            itemsAdapter.clear(); // Make sure there is no old things left over
            itemsAdapter.addAll(walkingTimesList);
            itemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
        }
    }

    public void DeleteTask(View view) {

        View parent = (View) view.getParent();
        TextView timeTextView = (TextView) parent.findViewById(R.id.task_title);

        String value = String.valueOf(timeTextView.getText());
        int indexToBeDeleted = (Integer.parseInt(value.substring(13, 14)) - 1);

        mPet.RemoveWalkingTime(indexToBeDeleted);
        PopulateWalkingTimeList();
        RemoveAlarm(indexToBeDeleted);
    }

    private void RemoveAlarm(int indexToBeDeleted) {

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), indexToBeDeleted, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }



    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

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

            Toast.makeText(getContext(), "Walking time has been scheduled", Toast.LENGTH_LONG).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            Date date = calendar.getTime();

            int mWalkingTimeId = mPet.SetWalkingTime(date);
            ScheduleFeeding(hourOfDay, minute, mWalkingTimeId);
        }

        private void ScheduleFeeding(int hourOfDay, int minute, int mWalkingTimeId) {

            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("Message", mPet.GetName() + " needs walking");
            alarmIntent = PendingIntent.getBroadcast(getContext(), mWalkingTimeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,alarmIntent);



        }

    }
}