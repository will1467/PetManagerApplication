package com.example.williamrussa3.petmanagerapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.lang.reflect.Type;
import java.util.ArrayList;
public class MainActivity extends AppCompatActivity {

    public static ArrayList<Pet> PetList;
    ArrayAdapter<String> itemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button expLogBtn = (Button) findViewById(R.id.exp_log_btn);
        expLogBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitialiseExpenseLog();
            }
        });

        Button addPetButton = (Button) findViewById(R.id.add_pet_Button);
        addPetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitialiseAddPetDialog();
            }

        });


        PetList = getPetListFromPreferences();

        PopulatePetList();
    }

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

    private void SavePreferences() {
        SharedPreferences mPrefs = getSharedPreferences("PET_DATA", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(PetList);
        prefsEditor.putString("pet_array", json);
        prefsEditor.commit();
    }

    private void InitialiseExpenseLog() {
        Intent i = new Intent(this, ExpensesActivity.class);
        startActivity(i);
    }


    private void InitialiseAddPetDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View customView = inflater.inflate(R.layout.add_pet_layout, null);

        final EditText mPetNameEditText = (EditText) customView.findViewById(R.id.petName);
        final EditText mPetBreedEditText = (EditText) customView.findViewById(R.id.petBreed);
        final EditText mPetWeightEditText = (EditText) customView.findViewById(R.id.petWeight);
        final RadioButton mPetTypeDog = (RadioButton) customView.findViewById(R.id.radioButtonDog);
        final RadioButton mPetTypeCat = (RadioButton) customView.findViewById(R.id.radioButtonCat);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(customView)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mPetBreedEditText.getText() != null && mPetNameEditText.getText() != null && mPetWeightEditText.getText() != null) {
                            double mPetWeight = Double.parseDouble(mPetWeightEditText.getText().toString());
                            String petType = "";
                            if(mPetTypeCat.isChecked()){
                                petType = "Cat";
                            }
                            else if(mPetTypeDog.isChecked()){
                                petType = "Dog";
                            }
                            Pet newPet = new Pet(mPetNameEditText.getText().toString(), mPetBreedEditText.getText().toString(),mPetWeight,petType);
                            PetList.add(newPet);
                            PopulatePetList();
                            SavePreferences();
                        }
                    }
                    })
                    .setNegativeButton("Cancel",null)
                  .create();
            dialog.show();
    }

    private void PopulatePetList() {
        ArrayList<String> petArrayList = new ArrayList<String>();
        ListView petListView = (ListView) findViewById(R.id.petList);

        for (int index = 0; index < PetList.size(); index++) {
            petArrayList.add(PetList.get(index).GetName());
        }

        if (itemsAdapter == null) {
            itemsAdapter = new ArrayAdapter<String>(this, R.layout.row_main, R.id.pet_button, petArrayList);
            petListView.setAdapter(itemsAdapter);
        } else {
            itemsAdapter.clear(); // Make sure there is no old things left over
            itemsAdapter.addAll(petArrayList);
            itemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
        }
    }

    public void StartPetActivity(View view) {
        View parent = (View) view.getParent();
        Button petButton = (Button) parent.findViewById(R.id.pet_button);

        String petName = String.valueOf(petButton.getText());

        for (int index = 0; index < PetList.size(); index++) {
            if (PetList.get(index).GetName().equals(petName)) {

                Intent petIntent = new Intent(getApplicationContext(), PetActivity.class);
                petIntent.putExtra("pet_object", PetList.get(index));
                petIntent.putExtra("pet_index", index);
                startActivity(petIntent);

            }
        }
    }

    public void DeleteTask(View view) {

        View parent = (View) view.getParent();
        Button petButton = (Button) parent.findViewById(R.id.pet_button);

        String petName = String.valueOf(petButton.getText());

        for (int index = 0; index < PetList.size(); index++) {
            if (PetList.get(index).GetName().equals(petName)) {
                PetList.remove(index);
            }
        }

        PopulatePetList();
        SavePreferences();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
