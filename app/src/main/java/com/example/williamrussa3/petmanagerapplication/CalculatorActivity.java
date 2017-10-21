package com.example.williamrussa3.petmanagerapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

public class CalculatorActivity extends AppCompatActivity {

    private Pet mPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        mPet = getIntent().getParcelableExtra("pet_object");

        Button calcBtn = (Button) findViewById(R.id.calc_btn);
        final RadioButton adultRb = (RadioButton) findViewById(R.id.adult_rb);
        final RadioButton normalRb = (RadioButton) findViewById(R.id.active_rb);

       calcBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(adultRb.isChecked()) {
                   double restingEnergyReq = 70 * Math.pow(mPet.GetWeight(), 0.75);
                   Log.i("REQ:", String.valueOf(restingEnergyReq));
                   if(normalRb.isChecked()) {
                       double maintainEnergyReq;
                       maintainEnergyReq = 1.7 * restingEnergyReq;
                       Log.i("MAIN:", String.valueOf(maintainEnergyReq));
                   }
               }
           }
       });
    }
}
