package com.example.williamrussa3.petmanagerapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArraySet;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ArrayList<Pet> PetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton addPetFab = (FloatingActionButton) findViewById(R.id.add_pet);
        addPetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitialiseAddPetPopUpWindow();
            }

        });

        FloatingActionButton deletePetFab = (FloatingActionButton) findViewById(R.id.delete_pet);
        deletePetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InitialiseDeletePetPopUpWindow();
            }
        });

        PetList = getPetListFromPreferences();

        for(int i = 0; i < PetList.size(); i++){
            CreatePetIcon(PetList.get(i));
        }
    }

    private void InitialiseDeletePetPopUpWindow() {

        ArrayList<String> PetNames = new ArrayList<String>();

        for(int i =0; i <PetList.size(); i++){
            Pet pet = PetList.get(i);
            PetNames.add(pet.GetName());
        }



        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.delete_pet_layout,null);

        final PopupWindow mPopUpWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopUpWindow.setFocusable(true);
        mPopUpWindow.update();

        mPopUpWindow.showAtLocation(customView, Gravity.CENTER,0,0);

        Spinner spinner = (Spinner) customView.findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,PetNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


    }

    @Override
    protected void onStop(){
        super.onStop();

        SharedPreferences mPrefs = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(PetList);
        prefsEditor.putString("pet_array", json);
        prefsEditor.commit();
    }

    private ArrayList<Pet> getPetListFromPreferences() {
        SharedPreferences mPref = getPreferences(Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String jsonData = mPref.getString("pet_array","");

        if(jsonData.isEmpty()){
            return new ArrayList<Pet>();
        }

        Type type = new TypeToken<ArrayList<Pet>>(){}.getType();
        ArrayList<Pet> Pets = gson.fromJson(jsonData,type);
        return Pets;
    }


    private void InitialiseAddPetPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.add_pet_layout,null);

        final PopupWindow mPopUpWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopUpWindow.setFocusable(true);
        mPopUpWindow.update();

        mPopUpWindow.showAtLocation(customView, Gravity.CENTER,0,0);

        Button mSubmitButton = (Button) customView.findViewById(R.id.submitButton);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPopUpWindow.dismiss();
            }
        });
        final EditText mPetNameEditText = (EditText) customView.findViewById(R.id.petName);
        final EditText mPetBreedEditText = (EditText) customView.findViewById(R.id.petBreed);

        mPopUpWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

                if (mPetBreedEditText.getText() != null && mPetNameEditText.getText() != null) {
                    Pet newPet = new Pet(mPetNameEditText.getText().toString(), mPetBreedEditText.getText().toString());
                    CreatePetIcon(newPet);
                    PetList.add(newPet);

                }
            }
        });

    }

    private void CreatePetIcon(final Pet newPet) {
        LinearLayout linearLayout =  (LinearLayout)findViewById(R.id.linLayout);
        TextView petName = new TextView(this);
        ImageView petIcon= new ImageView(this);
        petIcon.setImageResource(R.drawable.pet_icon);
        petIcon.setAdjustViewBounds(true);
        petIcon.setMaxWidth( (linearLayout.getWidth() / 3));
        petName.setText(newPet.GetName());
        petName.setTextSize(30.0F);
        linearLayout.addView(petName);
        linearLayout.addView(petIcon);

        petIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent petIntent = new Intent(getApplicationContext(), PetActivity.class);
                petIntent.putExtra("pet_object",newPet);
                startActivity(petIntent);
            }
        });

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String selectedItem = (String) parent.getItemAtPosition(position);

        for(int i = 0; i < PetList.size(); i++ ){
            Pet pet = PetList.get(i);
            if(pet.GetName().equals(selectedItem)){
                PetList.remove(i);
            }
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
