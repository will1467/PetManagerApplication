package com.example.williamrussa3.petmanagerapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

public class CalculatorActivityCat extends AppCompatActivity {

    private Pet mPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator_cat);
        mPet = getIntent().getParcelableExtra("pet_object");

        final Button calcBtn = (Button) findViewById(R.id.cat_calc_btn);
        final TextView outputTxt = (TextView) findViewById(R.id.cat_output_txt);
        final RadioButton ballLess = (RadioButton) findViewById(R.id.noballs_rb);
        final RadioButton intact = (RadioButton) findViewById(R.id.intact_rb);
        final RadioButton obeseProne = (RadioButton) findViewById(R.id.obese_rb);
        final RadioButton weightLoss = (RadioButton) findViewById(R.id.fat_rb);

        calcBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ballLess.isChecked()) {
                    outputTxt.setText(String.format("%.2f Calories per day", calcReq("neutered")));
                } else if(intact.isChecked()) {
                    outputTxt.setText(String.format("%.2f Calories per day", calcReq("intact")));
                } else if(obeseProne.isChecked()) {
                    outputTxt.setText(String.format("%.2f Calories per day", calcReq("obese")));
                } else if(weightLoss.isChecked()) {
                    outputTxt.setText(String.format("%.2f Calories per day", calcReq("weight-loss")));
                } else {
                    outputTxt.setText("Select a dietary requirement.");
                }
            }
        });
    }

    public double calcReq(String req) {
        double rer; // Resting energy requirements
        double der; // Daily enery requirements
        rer = 70 * Math.pow(mPet.GetWeight(), 0.75);
        if(req == "neutered") {
            der = 1.2 * rer;
            return der;
        } else if(req == "intact") {
            der = 1.4 * rer;
            return der;
        } else if(req == "obese") {
            der = 1 * rer;
            return der;
        } else if(req == "weight-loss") {
            der = 0.8 * rer;
            return der;
        } else {
            return 0;
        }
    }
}
