package com.example.williamrussa3.petmanagerapplication;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ExpensesActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ArrayAdapter<String> itemsAdapter;
    ListView expListView;
    ArrayList<String> PetNames = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses);

        // Get the list of pet names for the spinner
        for (int i = 0; i < MainActivity.mPetList.size(); i++) {
            PetNames.add(MainActivity.mPetList.get(i).GetName());
        }

        dbHelper = new DBHelper(this);
        expListView = (ListView) findViewById(R.id.expenseList);

        loadExpenseList();
    }

    // Populate the screen with expenses previously entered or set the adapter if there is
    // nothing
    private void loadExpenseList() {
        ArrayList<String> expList = dbHelper.getExpenseList();
        if (itemsAdapter == null) {
            itemsAdapter = new ArrayAdapter<String>(this, R.layout.row_adapter, R.id.task_title, expList);
            expListView.setAdapter(itemsAdapter);
        } else {
            itemsAdapter.clear(); // Make sure there is no old things left over
            itemsAdapter.addAll(expList);
            itemsAdapter.notifyDataSetChanged(); // Notify that data has changed and update views
        }

        // Calculate the total cost
        ArrayList<Double> expCost = dbHelper.getExpenseCost();
        Double total = 0.0;
        for (Double cost : expCost) {
            total += cost;
        }

        TextView totalCost = (TextView) findViewById(R.id.total_expenses);
        totalCost.setText("Total" + " $" + String.format("%.2f", total));
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
                final EditText expCost = (EditText) inflatedView.findViewById(R.id.cost);
                final Spinner spinner = (Spinner) inflatedView.findViewById(R.id.spinner);

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) { }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) { }
                });

                ArrayAdapter<String> adapter =
                        new ArrayAdapter<String>(ExpensesActivity.this, android.R.layout.simple_spinner_item, PetNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);

                AlertDialog dialog = new AlertDialog.Builder(this)
                        .setTitle("Add New Expense")
                        .setMessage("What have you bought now?")
                        .setView(inflatedView)
                        .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Get input from the user about the expense
                                String title = String.valueOf(expTitle.getText());
                                String detail = String.valueOf(expDetails.getText());
                                String a = String.valueOf(expCost.getText());
                                Double cost;
                                if (!a.isEmpty()) {
                                    cost = Double.parseDouble(expCost.getText().toString());
                                } else { // If no expense cost is enter it defaults to 0
                                    cost = 0.0;
                                }
                                // Show a menu of the pet names
                                String petName = spinner.getSelectedItem().toString();
                                if (title.isEmpty() || detail.isEmpty()) {
                                    // If the necessary details are not added do not add anything
                                    // to the database and show a Toast telling user to fill in all
                                    showToast();
                                } else { // Have everything necessary insert into database + load list
                                    dbHelper.insertNewExpense(title, detail, cost, petName);
                                    loadExpenseList();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .create();
                dialog.show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Delete an expense, remove the view and delete it from database
    public void DeleteExpense(View view) {
        View parent = (View) view.getParent();
        TextView taskTextView = (TextView) parent.findViewById(R.id.task_title);
        String expense = String.valueOf(taskTextView.getText());
        dbHelper.deleteExpense(expense);
        loadExpenseList();
    }

    // Show a message telling user to fill in everything
    public void showToast() {
        Toast.makeText(this, "Enter all fields", Toast.LENGTH_LONG).show();
    }
}
