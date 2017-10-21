package com.example.williamrussa3.petmanagerapplication;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends Activity {


    //Static arrayList of all Pets registered in the app. Is static so it can be accessed by other activities
    public static ArrayList<Pet> mPetList;

    //Adapter used when populating ListView
    private ArrayAdapter<String> mItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set event listener for Expenses Log Button
        Button expLogBtn = (Button) findViewById(R.id.exp_log_btn);

        //Don't allow user to go to expenses log if no pets exist. Warn user using Toast
        expLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(mPetList.size() != 0) {
                InitialiseExpenseLog();
            }
            else{
                Toast.makeText(getBaseContext(),"Please add a Pet first", Toast.LENGTH_LONG).show();
            }
            }
        });

        Button addPetButton = (Button) findViewById(R.id.add_pet_Button);

        //Create Dialog upon Add Pet Button select
        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitialiseAddPetDialog();
            }

        });

        //Load Pet List data from shared preferences on app start
        mPetList = getPetListFromPreferences();

        //Populate listview on app start
        PopulatePetList();
    }

    //Load all pets from shared preferences and return ArrayList of all pets in application using GSON

    private ArrayList<Pet> getPetListFromPreferences() {
        SharedPreferences mPrefs = getSharedPreferences("PET_DATA", Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = mPrefs.getString("pet_array", "");

        if (jsonData.isEmpty()) {
            return new ArrayList<Pet>();
        }

        Type type = new TypeToken<ArrayList<Pet>>() {
        }.getType();
        ArrayList<Pet> Pets = gson.fromJson(jsonData, type);
        return Pets;
    }

    //Save shared prefs using GSON to convert PetList to JSON

    private void SavePreferences() {
        SharedPreferences mPrefs = getSharedPreferences("PET_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(mPetList);
        prefsEditor.putString("pet_array", json);
        prefsEditor.commit();
    }

    //Create intent to start Expenses Activity

    private void InitialiseExpenseLog() {
        Intent i = new Intent(this, ExpensesActivity.class);
        startActivity(i);
    }

    //Create AlertDialog allowing user to add a new Pet

    private void InitialiseAddPetDialog() {

        //Use add pet layout xml for this dialog
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.add_pet_layout, null);

        //Get all input Views

        final EditText mPetNameEditText = (EditText) customView.findViewById(R.id.petName);
        final EditText mPetBreedEditText = (EditText) customView.findViewById(R.id.petBreed);
        final EditText mPetWeightEditText = (EditText) customView.findViewById(R.id.petWeight);
        final RadioButton mPetTypeDog = (RadioButton) customView.findViewById(R.id.radioButtonDog);
        final RadioButton mPetTypeCat = (RadioButton) customView.findViewById(R.id.radioButtonCat);

        AlertDialog dialog = new AlertDialog.Builder(this)

                //Set View to xml
                .setView(customView)
                //Set add as "OK" button for this dialog
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String mPetName = mPetNameEditText.getText().toString();
                        String mPetBreed = mPetBreedEditText.getText().toString();
                        double mPetWeight;

                        //Check for NumberFormatException for when TextField is blank
                        try {
                            mPetWeight = Double.parseDouble(mPetWeightEditText.getText().toString());
                        }
                        catch(NumberFormatException e){
                            mPetWeight = 0.0;
                        }

                        //Check what radio button is checked
                        String mPetType = "";
                        if(mPetTypeCat.isChecked()){
                            mPetType = "Cat";
                        }
                        else if(mPetTypeDog.isChecked()){
                            mPetType = "Dog";
                        }

                        //Check no fields are blank before allowing closure of dialog and creation of pet object
                        if(mPetName.equals("") || mPetBreed.equals("") || mPetWeight == 0.0 || mPetType.equals("")){
                            Toast.makeText(getBaseContext(),"Please fill out all fields", Toast.LENGTH_LONG).show();
                        }
                        else{
                            //Create new Pet object and add to PetList
                            Pet newPet = new Pet(mPetName, mPetBreed,mPetWeight,mPetType);
                            mPetList.add(newPet);

                            //Re-populate list view after new Pet is added
                            PopulatePetList();

                            //Save to shared preferences after new Pet has been added
                            SavePreferences();
                        }
                    }
                    })
                //Set negative button for this dialog
                    .setNegativeButton("Cancel",null)
                  .create();
            dialog.show();
    }

    //Populate ListView with data from pet Objects. Called when activity starts and when new pet is added

    private void PopulatePetList() {

        //Create arrayList to be populated
        ArrayList<String> mPetArrayList = new ArrayList<String>();
        ListView petListView = (ListView) findViewById(R.id.petList);

        //Iterate through PetList and add pet names to Array List
        for (int index = 0; index < mPetList.size(); index++) {
            mPetArrayList.add(mPetList.get(index).GetName());
        }

        //If itemsAdapter has not be initialised, create it using layout.rowadapter and arraylist. Otherwise update
        if (mItemsAdapter == null) {
            mItemsAdapter = new ArrayAdapter<String>(this, R.layout.row_main, R.id.pet_button, mPetArrayList);
            //Set adapter for listView
            petListView.setAdapter(mItemsAdapter);
        } else {
            mItemsAdapter.clear(); // Make sure there is no old things left over
            mItemsAdapter.addAll(mPetArrayList);
            mItemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
        }
    }

    // Start PetActivity. Called on click of the pet name in the listView

    public void StartPetActivity(View view) {
        View parent = (View) view.getParent();
        Button petButton = (Button) parent.findViewById(R.id.pet_button);

        String petName = String.valueOf(petButton.getText());

        //Find Pet in the PetList using the Pet name.
        // Pass pet object and pet index to PetActivity. Index will be used for lookup of Pet object in other activities
        for (int index = 0; index < mPetList.size(); index++) {
            if (mPetList.get(index).GetName().equals(petName)) {

                Intent petIntent = new Intent(getApplicationContext(), PetActivity.class);
                petIntent.putExtra("pet_object",mPetList.get(index));
                petIntent.putExtra("pet_index", index);
                startActivity(petIntent);

            }
        }
    }

    //Remove a pet from the PetList. Called upon click of the delete button

    public void DeleteTask(final View view) {

        //Check with user before deleting Pet. Display simple message with two options.
        AlertDialog.Builder dialog = new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to delete this pet?" + "\n\n" + "All data related to this pet will be lost")
                //Set "Yes" as "OK" button for this dialog
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                       View parent = (View) view.getParent();
                        Button petButton = (Button) parent.findViewById(R.id.pet_button);

                        String petName = String.valueOf(petButton.getText());

                        //Find Pet in the PetList using the Pet name.
                        // Remove Pet object from the PetList

                        for (int index = 0; index < mPetList.size(); index++) {
                            if (mPetList.get(index).GetName().equals(petName)) {
                                mPetList.remove(index);
                            }
                        }

                        //Update list view and save to shared preferences upon deletion of Pet object
                        PopulatePetList();
                        SavePreferences();
                    }
                })
                //Set negative button for this dialog
                .setNegativeButton("Cancel",null);
                dialog.show();


    }
}
