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
import android.widget.ListView;
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

        FloatingActionButton addPetFab = (FloatingActionButton) findViewById(R.id.add_pet);
        addPetFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitialiseAddPetPopUpWindow();
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


    private void InitialiseAddPetPopUpWindow() {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View customView = inflater.inflate(R.layout.add_pet_layout, null);

        final PopupWindow mPopUpWindow = new PopupWindow(customView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        mPopUpWindow.setFocusable(true);
        mPopUpWindow.update();

        mPopUpWindow.showAtLocation(customView, Gravity.CENTER, 0, 0);

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
                    PetList.add(newPet);
                    PopulatePetList();
                    SavePreferences();

                }
            }
        });

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
