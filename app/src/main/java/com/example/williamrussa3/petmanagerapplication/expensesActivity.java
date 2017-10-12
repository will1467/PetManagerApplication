package com.example.williamrussa3.petmanagerapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class expensesActivity extends AppCompatActivity {

    private Pet mPet;
    DBHelper dbHelper;
    ArrayAdapter<String> itemsAdapter;
    ListView expListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        mPet = getIntent().getParcelableExtra("pet_object");
        dbHelper = new DBHelper(this);
        expListView = (ListView)findViewById(R.id.expenseList);

        loadExpenseList();
    }

    // Populate the screen with expenses previously entered or set the adapter if there is
    // nothing
    private void loadExpenseList() {
        ArrayList<String> expList = dbHelper.getTaskList();
        if(itemsAdapter == null){
            itemsAdapter = new ArrayAdapter<String>(this,R.layout.row,R.id.task_title,expList);
            expListView.setAdapter(itemsAdapter);
        }
        else {
            itemsAdapter.clear(); // Make sure there is no old things left over
            itemsAdapter.addAll(expList);
            itemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
        }
    }

    // Inflate the menu that is used to add expenses to the list
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_expense_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Handles when the '+' button is clicked at the top right of the screen to add a new expense
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) { // Identify which menu item we are working with
            case R.id.action_add_exp: // Only one item to look out for

                LayoutInflater inflater = getLayoutInflater();
                View inflatedView = inflater.inflate(R.layout.add_expense, null);
                final EditText expTitle = (EditText) inflatedView.findViewById(R.id.expense);
                final EditText expDetails = (EditText) inflatedView.findViewById(R.id.detail);
                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add New Expense")
                        .setMessage("What have you bought now?")
                        .setView(inflatedView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = String.valueOf(expTitle.getText());
                                String detail = String.valueOf(expDetails.getText());
                                dbHelper.insertNewExpense(title, detail);
                                loadExpenseList();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .create();
                dialog.show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteTask(View view){
        View parent = (View)view.getParent();
        TextView taskTextView = (TextView)parent.findViewById(R.id.task_title);
        String task = String.valueOf(taskTextView.getText());
        dbHelper.deleteExpense(task);
        loadExpenseList();
    }
}
