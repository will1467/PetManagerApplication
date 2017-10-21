package com.example.williamrussa3.petmanagerapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class CalculatorActivityDog extends AppCompatActivity {

    private Pet mPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mPet = getIntent().getParcelableExtra("pet_object");
        setContentView(R.layout.activity_calculator_dog);

        Button calcBtn = (Button) findViewById(R.id.calc_btn);
        final RadioButton puppyRb = (RadioButton) findViewById(R.id.pubby_rb);
        final RadioButton adultRb = (RadioButton) findViewById(R.id.adult_rb);
        final RadioButton seniorRb = (RadioButton) findViewById(R.id.senior_rb);

        final RadioButton activeRB = (RadioButton) findViewById(R.id.active_rb);
        final RadioButton workingRb = (RadioButton) findViewById(R.id.working_rb);
        final RadioButton lazyRb = (RadioButton) findViewById(R.id.lazy_rb);

        final TextView outputTxt = (TextView) findViewById(R.id.output_txt);

       calcBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(puppyRb.isChecked()) {
                   if(activeRB.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("puppy", "active")));
                   } else if(workingRb.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("puppy", "working")));
                   } else if(lazyRb.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("puppy", "lazy")));
                   } else {
                       outputTxt.setText("Select an activity level.");
                   }
               } else if(adultRb.isChecked()) {
                   if(activeRB.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("adult", "active")));
                   } else if(workingRb.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("adult", "working")));
                   } else if(lazyRb.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("adult", "lazy")));
                   } else {
                       outputTxt.setText("Select an activity level.");
                   }
               } else if(seniorRb.isChecked()) {
                   if (activeRB.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("senior", "active")));
                   } else if (workingRb.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("senior", "working")));
                   } else if (lazyRb.isChecked()) {
                       outputTxt.setText(String.format("%.2f Calories per day", calculateReq("senior", "lazy")));
                   } else {
                       outputTxt.setText("Select an activity level.");
                   }
               } else {
                   outputTxt.setText("Select an age.");
               }
           }
       });
    }

    public double calculateReq(String age, String activity) {
        double maintainEnergyReq;
        double restingEnergyReq;

        if(age == "puppy") {
            restingEnergyReq = 55 * Math.pow(mPet.GetWeight(), 0.75);
        } else if(age == "adult") {
            restingEnergyReq = 70 * Math.pow(mPet.GetWeight(), 0.75);
        } else if(age == "senior") {
            restingEnergyReq = 60 * Math.pow(mPet.GetWeight(), 0.75);
        } else {
            return 0;
        }

        if(activity == "active") {
            maintainEnergyReq = 1.7 * restingEnergyReq;
            return maintainEnergyReq;
        } else if(activity == "working") {
            maintainEnergyReq = 2 * restingEnergyReq;
            return maintainEnergyReq;
        } else if(activity == "lazy") {
            maintainEnergyReq = 1 * restingEnergyReq;
            return maintainEnergyReq;
        } else {
            return 0;
        }
    }
}
