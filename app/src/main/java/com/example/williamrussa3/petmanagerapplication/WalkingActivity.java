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


/*Class Walking Activity
    Schedules walking times for Pet and sets alarms for those walking times
 */

public class WalkingActivity extends AppCompatActivity implements DialogInterface.OnDismissListener{

    //Pet object holding data about the current pet. Made static so can be used in the DialogFragment
    private static Pet mPet;

    //Index of pet object. Used in update/deletion of pet data from the Pet list
    private int mPetIndex;

    //ArrayAdapter to hold list View items
    ArrayAdapter<String> mItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_walking);

        //Get pet data from PetActivity intent
        mPetIndex = getIntent().getIntExtra("pet_index",0);

        //Update PetList data from shared preferences. Retrieve pet object list using index
        MainActivity.mPetList = getPetListFromPreferences();
        mPet = MainActivity.mPetList.get(mPetIndex);

        //Populate ListView upon create
        PopulateWalkingTimeList();
    }

    /*Save preferences on stop of activity
        Pet data is deleted and readded to the PetList. This is done so changes to Pet data while activity is running (i.e times scheduled) is saved
        and no duplicate pets exist in the PetList
     */

    @Override
    protected void onStop(){

        super.onStop();
        MainActivity.mPetList.remove(mPetIndex);
        MainActivity.mPetList.add(mPet);
        SavePreferences();
    }

    //Save shared prefs using GSON to convert PetList to JSON

    private void SavePreferences(){

        SharedPreferences mPrefs = getSharedPreferences("PET_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(MainActivity.mPetList);
        prefsEditor.putString("pet_array", json);
        prefsEditor.commit();
    }

    //Load all pets from shared preferences and return ArrayList of all pets in application using GSON

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

    //Create instance of time picker fragment and show in activity

    public void showTimePicker(View v) {

        DialogFragment newFragment = new WalkingActivity.TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    //ListView is updated upon Dismissal of the TimePickerDialog to add new time

    @Override
    public void onDismiss(DialogInterface dialog) {
        PopulateWalkingTimeList();
    }

    //Populate ListView with scheduled feeding times

    private void PopulateWalkingTimeList() {

        //Create arrayList to be populated
        ArrayList<String> mWalkingTimesList = new ArrayList<String>();
        ListView mWalkingTimesListView = (ListView) findViewById(R.id.walkingTimesList);

        //Loop through feeding times in Pet Object and load into ListView
        for (int index = 0; index < mPet.GetWalkingTimeArrayLength(); index++) {

            //Get date object from mPet and create calendar object
            ScheduledTime scheduledTime = mPet.GetWalkingTime(index);
            Date date = scheduledTime.GetDate();
            String name = "Walking Time " + (index + 1);
            Calendar c = Calendar.getInstance();
            c.setTime(date);

            //Add string containing time data to arrayList. Add a leading 0 if minutes are in single digits
            if (c.get(Calendar.MINUTE) < 10) {
                mWalkingTimesList.add(name + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + "0" + c.get(Calendar.MINUTE));
            } else {
                mWalkingTimesList.add(name + " " + c.get(Calendar.HOUR_OF_DAY) + ":" + c.get(Calendar.MINUTE));
            }
        }

        //If itemsAdapter has not be initialised, create it using layout.rowadapter and arraylist. Otherwise update
        if (mItemsAdapter == null) {
            mItemsAdapter = new ArrayAdapter<String>(this, R.layout.row_adapter, R.id.task_title, mWalkingTimesList);

            //Set adapter for listView
            mWalkingTimesListView.setAdapter(mItemsAdapter);
        } else {
            mItemsAdapter.clear(); // Make sure there is no old things left over
            mItemsAdapter.addAll(mWalkingTimesList);
            mItemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
        }
    }

    //Removes a walking time from the list. Called on click of the delete button
    public void DeleteTask(View view) {

        View parent = (View) view.getParent();
        TextView timeTextView = (TextView) parent.findViewById(R.id.task_title);

        //Get index to be deleted from TextView string and convert to Int
        String value = String.valueOf(timeTextView.getText());
        int indexToBeDeleted = (Integer.parseInt(value.substring(13, 14)) - 1);

        /*Remove feeding time from pet object using index,
            update list view and remove alarm using index
         */

        mPet.RemoveWalkingTime(indexToBeDeleted);
        PopulateWalkingTimeList();
        RemoveAlarm(indexToBeDeleted);
    }

    private void RemoveAlarm(int indexToBeDeleted) {

        //Use Pet index to remove alarm. Intent being cancelled will have same code as the alarm intent when it was made

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), indexToBeDeleted, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
    }

//Time picker fragment. Opens dialog allowing user to select time for walking

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {


        //Calls onDismiss in MainActivity
        @Override
        public void onDismiss(final DialogInterface dialog) {

            super.onDismiss(dialog);
            final Activity activity = getActivity();
            if (activity instanceof DialogInterface.OnDismissListener) {
                ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
            }
        }

//Create new Time Picker dialog using current hour and current minute as default values. Time picker is in 24 hour format

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        //Called upon time set in the time Picker. Schedules alarm

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            //Notify user walking time has been scheduled using Toast
            Toast.makeText(getContext(), "Walking time has been scheduled", Toast.LENGTH_LONG).show();

            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            Date date = calendar.getTime();

            //Create new walking time in Pet object. SetFeedingDate returns ID of feeding time in Pet object which is used by the Alarm Intent

            int mWalkingTimeId = mPet.SetWalkingTime(date);
            ScheduleFeeding(hourOfDay, minute, mWalkingTimeId);
        }

        //Schedule walking time using system alarm service

        private void ScheduleFeeding(int hourOfDay, int minute, int mWalkingTimeId) {

            AlarmManager alarmMgr;
            PendingIntent alarmIntent;

            alarmMgr = (AlarmManager)getContext().getSystemService(Context.ALARM_SERVICE);

            //AlarmReceiver.java will receive the alarm broadcast
            Intent intent = new Intent(getContext(), AlarmReceiver.class);

            //Set intent message
            intent.putExtra("Message", mPet.GetName() + " needs walking");

            //Use ID of feeding time in pet object to give intent unique ID. This allows alarm to be canceled by calling intent of same ID
            alarmIntent = PendingIntent.getBroadcast(getContext(), mWalkingTimeId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            //Set alarm to repeat daily at set time

            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY,alarmIntent);



        }

    }
}