package com.example.williamrussa3.petmanagerapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class PetActivity extends AppCompatActivity {

    private Pet mPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        mPet = getIntent().getParcelableExtra("pet_object");

        ImageButton petFeed = (ImageButton)findViewById(R.id.pet_feed);

        petFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent petIntent = new Intent(getApplicationContext(), FeedingActivity.class);
                petIntent.putExtra("pet_object",mPet);
                startActivity(petIntent);
            }
        });

        ImageButton foodCalculator = (ImageButton) findViewById(R.id.food_calculator);

        foodCalculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent petIntent = new Intent(getApplicationContext(), CalculatorActivity.class);
                petIntent.putExtra("pet_object",mPet);
                startActivity(petIntent);
            }
        });

        ImageButton expensesLog = (ImageButton) findViewById(R.id.expenses_log);

        expensesLog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent petIntent = new Intent(getApplicationContext(), expensesActivity.class);
                petIntent.putExtra("pet_object",mPet);
                startActivity(petIntent);
            }
        });

    }
}
