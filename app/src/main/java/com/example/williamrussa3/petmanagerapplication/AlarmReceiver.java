package com.example.williamrussa3.petmanagerapplication;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            Toast.makeText(context, "OnReceive alarm test", Toast.LENGTH_SHORT).show();
        }
    }