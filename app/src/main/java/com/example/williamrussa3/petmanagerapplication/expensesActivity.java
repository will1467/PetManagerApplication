package com.example.williamrussa3.petmanagerapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class expensesActivity extends AppCompatActivity {

    private Pet mPet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);
        mPet = getIntent().getParcelableExtra("pet_object");
    }
}
