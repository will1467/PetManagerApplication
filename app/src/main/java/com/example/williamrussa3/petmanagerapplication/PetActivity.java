package com.example.williamrussa3.petmanagerapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class PetActivity extends AppCompatActivity {

    //Pet object holding data about the current pet.
    private Pet mPet;

    //Index of pet object. Passed into Walking and Feeding Activities
    private int mPetIndex;

    //Text used for custom adapter
    String[] mImageCaptions = {
            "Pet Feeding",
            "Pet Walking",
            "Pet Calculator",
    };

    //ID's of drawable resources used for custom adapter
    Integer[] mImageID = {
            R.drawable.pet_feeding,
            R.drawable.dog_walking,
            R.drawable.calculator,


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);

        //Get pet data and index from PetActivity intent
        mPet = getIntent().getParcelableExtra("pet_object");
        mPetIndex = getIntent().getIntExtra("pet_index", 0);

        TextView petInfo = (TextView) findViewById(R.id.petInfo);

        //Set text above ArrayAdapter showing Pet information

        petInfo.setText(" Name: " + mPet.GetName() + "\n Breed: " + mPet.GetBreed() + "\n Weight: " + mPet.GetWeight() + "kg");

        //Create custom adapter using array of strings and image ID's

        CustomList adapter = new CustomList(PetActivity.this, mImageCaptions, mImageID);
        final ListView list = (ListView) findViewById(R.id.list);

        //Set adapter for listView
        list.setAdapter(adapter);

        //Called upon click of listView item. Starts new activity depending on what was clicked
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Get text of caption. Use this to check what activity to start
                Object o = list.getItemAtPosition(position);
                String textViewCapton = (String) o;

                Class ActivityClass;

                //Start Activity depending on which item was clicked on
                if (textViewCapton.equals(mImageCaptions[0])) {
                    ActivityClass = FeedingActivity.class;
                } else if (textViewCapton.equals(mImageCaptions[1])) {
                    ActivityClass = WalkingActivity.class;
                } else if (textViewCapton.equals(mImageCaptions[2])) {

                    //Start different calculator activity depending on type of pet

                    if (mPet.GetType().equals("Dog")) {
                        ActivityClass = CalculatorActivityDog.class;
                    } else {
                        ActivityClass = CalculatorActivityCat.class;
                    }

                } else {
                    ActivityClass = ExpensesActivity.class;
                }

                //Start intent, pass pet index and pet data to all intents for use in those activities

                Intent petIntent = new Intent(getApplicationContext(), ActivityClass);
                petIntent.putExtra("pet_object",mPet);
                petIntent.putExtra("pet_index", mPetIndex);
                startActivity(petIntent);

            }
        });

    }

}
