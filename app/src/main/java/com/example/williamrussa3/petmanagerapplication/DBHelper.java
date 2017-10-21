package com.example.williamrussa3.petmanagerapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    private static final String DB_NAME = "EXPENSE_LOG";
    private static final int DB_VER = 1;
    public static final String DB_TABLE = "Log";
    public static final String DB_COLUMN_NAME = "ExpenseName";
    public static final String DB_COLUMN_DETAIL = "ExpenseDetail";
    public static final String DB_COLUMN_PETNAME = "PetName";
    public static final String DB_COLUMN_COST = "ExpenseCost";

    /**
     * Called when the DBHelper is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The DBHelper.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL,%s TEXT NOT NULL,%s TEXT NOT NULL, %s TEXT NOT NULL);", DB_TABLE, DB_COLUMN_NAME, DB_COLUMN_DETAIL, DB_COLUMN_COST, DB_COLUMN_PETNAME);
        db.execSQL(query);
    }

    /**
     * Called when the DBHelper needs to be upgraded. The implementation
     * should use this method to drop tables, add tables, or do anything else it
     * needs to upgrade to the new schema version.
     *
     * @param db         The DBHelper.
     * @param oldVersion The old DBHelper version.
     * @param newVersion The new DBHelper version.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String query = String.format("DELETE TABLE IF EXISTS %s", DB_TABLE);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertNewExpense(String title, String detail, double cost, String petName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, title);
        values.put(DB_COLUMN_DETAIL, detail);
        values.put(DB_COLUMN_COST, cost);
        values.put(DB_COLUMN_PETNAME, petName);
        db.insertWithOnConflict(DB_TABLE, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // exp String comes with all the details from the database for a single expense
    // Split the string into individual words and test to see if they are in the database
    // If a match is found it will be deleted from the database and no longer show
    public void deleteExpense(String exp) {
        SQLiteDatabase db = this.getWritableDatabase();

        String expenseEntry[] = exp.split("\n", 4); // [0] = Title | [1] = Detail | [2] = Cost [3] = Pet Name
        Log.e("Mssg", expenseEntry[0] + expenseEntry[1] + expenseEntry[2] + expenseEntry[3]);

        db.delete(DB_TABLE, DB_COLUMN_NAME + "=? AND " + DB_COLUMN_DETAIL + "=? AND " + DB_COLUMN_COST + "=? AND " + DB_COLUMN_PETNAME + "=?",
                new String[]{expenseEntry[0], expenseEntry[1], expenseEntry[2], expenseEntry[3]});
        db.close();
    }

    // Query the DB to get the details and build an expense which is added to an ArrayList
    // Get the detail from the
    public ArrayList<String> getTaskList() {
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursorName = db.query(DB_TABLE, new String[]{DB_COLUMN_NAME}, null, null, null, null, null);
        Cursor cursorDetails = db.query(DB_TABLE, new String[]{DB_COLUMN_DETAIL}, null, null, null, null, null);
        Cursor cursorCost = db.query(DB_TABLE, new String[]{DB_COLUMN_COST}, null, null, null, null, null);
        Cursor cursorPetName = db.query(DB_TABLE, new String[]{DB_COLUMN_PETNAME}, null, null, null, null, null);
        while (cursorName.moveToNext()) {
            cursorDetails.moveToNext();
            cursorCost.moveToNext();
            cursorPetName.moveToNext();
            int indexName = cursorName.getColumnIndex(DB_COLUMN_NAME);
            int indexDetails = cursorDetails.getColumnIndex(DB_COLUMN_DETAIL);
            int indexCost = cursorCost.getColumnIndex(DB_COLUMN_COST);
            int indexPetName = cursorPetName.getColumnIndex(DB_COLUMN_PETNAME);
            String s = cursorName.getString(indexName) + '\n' + cursorDetails.getString(indexDetails) + '\n' +
                    cursorCost.getString(indexCost) + "\n" + cursorPetName.getString(indexPetName);
            taskList.add(s);
        }
        cursorName.close();
        cursorDetails.close();
        cursorCost.close();
        cursorPetName.close();
        db.close();
        return taskList;
    }

    public ArrayList<Double> getExpenseCost() {
        ArrayList<Double> costList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(DB_TABLE, new String[]{DB_COLUMN_COST}, null, null, null, null, null);

        while (cursor.moveToNext()) {
            int index = cursor.getColumnIndex(DB_COLUMN_COST);
            Double cost = cursor.getDouble(index);
            costList.add(cost);
        }
        cursor.close();
        db.close();
        return costList;
    }

}
