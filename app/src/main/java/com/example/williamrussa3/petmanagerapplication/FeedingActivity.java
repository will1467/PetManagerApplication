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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FeedingActivity extends AppCompatActivity implements DialogInterface.OnDismissListener  {

     private static Pet mPet;
     private int mPetIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feeding);
        mPet = getIntent().getParcelableExtra("pet_object");
        mPetIndex = getIntent().getIntExtra("pet_index",0);
        AddFeedingTime();
    }

    @Override
    protected void onPause(){
        super.onPause();
        MainActivity.PetList.remove(mPetIndex);
        MainActivity.PetList.add(mPet);
        SavePreferences();
    }


    private void SavePreferences(){
        SharedPreferences mPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(MainActivity.PetList);
        prefsEditor.putString("pet_array", json);
        prefsEditor.commit();
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
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.feeding_lin_layout);
        linearLayout.removeAllViews();
        for (int i = 0; i < mPet.GetFeedingTimeLength();  i++) {
            ScheduledTime scheduledTime = mPet.GetFeedingTime(i);

            if(scheduledTime != null){
                Date date = scheduledTime.GetDate();
                String name = "Feeding Time  " + Integer.toString(i);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                TextView feedingTime = new TextView(this);
                feedingTime.setMaxHeight((linearLayout.getHeight()/4));
                feedingTime.setMaxWidth((linearLayout.getWidth()/3));
                feedingTime.setText(name + " " + c.get( Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
                feedingTime.setTextSize(20.0F);
                feedingTime.setTextColor(Color.BLACK);
                feedingTime.setPadding(0,20,0,0);
                linearLayout.addView(feedingTime);
            }
        }
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
            ScheduleFeeding(hourOfDay, minute);
            Toast.makeText(getContext(), "Feeding time has been scheduled", Toast.LENGTH_LONG).show();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            Date date = calendar.getTime();
            mPet.SetFeedingTime(date);
        }

        private void ScheduleFeeding(int hourOfDay, int minute) {
            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(getContext(), AlarmReceiver.class);
            intent.putExtra("Message", mPet.GetName() + " needs feeding");
            alarmIntent = PendingIntent.getBroadcast(getContext(), 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            alarmMgr.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_FIFTEEN_MINUTES, alarmIntent);

        }

    }
}
