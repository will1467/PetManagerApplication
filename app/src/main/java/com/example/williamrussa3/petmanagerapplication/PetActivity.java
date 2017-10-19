package com.example.williamrussa3.petmanagerapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

public class PetActivity extends AppCompatActivity {

    private Pet mPet;
    private int mPetIndex;

    String[] imageCaptions = {
            "Pet Feeding",
            "Pet Walking",
            "Pet Calculator",
    } ;
    Integer[] imageID = {
            R.drawable.pet_feeding,
            R.drawable.dog_walking,
            R.drawable.calculator,


    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet);
        mPet = getIntent().getParcelableExtra("pet_object");
        mPetIndex = getIntent().getIntExtra("pet_index",0);

        CustomList adapter = new CustomList(PetActivity.this, imageCaptions, imageID);
        final ListView list=(ListView)findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               Object o = list.getItemAtPosition(position);
                String textViewCapton = (String) o;

                Class ActivityClass;

                if(textViewCapton.equals(imageCaptions[0])){
                    ActivityClass = FeedingActivity.class;
                }
                else if(textViewCapton.equals(imageCaptions[1])){
                    ActivityClass = WalkingActivity.class;
                }
                else if(textViewCapton.equals(imageCaptions[2])){
                    ActivityClass = CalculatorActivity.class;
                }
                else{
                    ActivityClass = ExpensesActivity.class;
                }

                Intent petIntent = new Intent(getApplicationContext(),ActivityClass);
                petIntent.putExtra("pet_object",mPet);
                petIntent.putExtra("pet_index", mPetIndex);
                startActivity(petIntent);

            }
        });

    }

}
