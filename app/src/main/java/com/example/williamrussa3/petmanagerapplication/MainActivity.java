package com.example.williamrussa3.petmanagerapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitialisePopUpWindow();
            }

        });
    }

    private void InitialisePopUpWindow() {
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
                }
            }
        });

    }

    private void CreatePetIcon(Pet newPet) {
        LinearLayout linearLayout =  (LinearLayout)findViewById(R.id.linLayout);
        TextView petName = new TextView(this);
        ImageView petIcon= new ImageView(this);
        petIcon.setImageResource(R.drawable.pet_icon);
        petIcon.setAdjustViewBounds(true);
        petIcon.setMaxWidth( (linearLayout.getWidth() / 3));
        petName.setText(newPet.GetName());
        petName.setTextSize(15.0F);
        linearLayout.addView(petName);
        linearLayout.addView(petIcon);

        petIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent petIntent = new Intent(getApplicationContext(), PetActivity.class);
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
}
