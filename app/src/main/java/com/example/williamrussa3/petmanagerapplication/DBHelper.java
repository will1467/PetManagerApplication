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
    public static final String DB_TABLE="Log";
    public static final String DB_COLUMN_NAME = "ExpenseName";
    public static final String DB_COLUMN_DETAIL = "ExpenseDetail";

    /**
     * Called when the DBHelper is created for the first time. This is where the
     * creation of tables and the initial population of the tables should happen.
     *
     * @param db The DBHelper.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String query =
                String.format("CREATE TABLE %s (ID INTEGER PRIMARY KEY AUTOINCREMENT,%s TEXT NOT NULL,%s TEXT NOT NULL);",DB_TABLE,DB_COLUMN_NAME, DB_COLUMN_DETAIL);
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
        String query = String.format("DELETE TABLE IF EXISTS %s",DB_TABLE);
        db.execSQL(query);
        onCreate(db);
    }

    public void insertNewExpense(String title, String detail){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DB_COLUMN_NAME, title);
        values.put(DB_COLUMN_DETAIL, detail);
        db.insertWithOnConflict(DB_TABLE,null,values,SQLiteDatabase.CONFLICT_REPLACE);
        db.close();
    }

    // exp String comes with all the details from the database for a single expense
    // Split the string into individual words and test to see if they are in the database
    // If the strings are found in the database within the same row_adapter the row_adapter will be deleted
    public void deleteExpense(String exp){
        SQLiteDatabase db = this.getWritableDatabase();

        String arr[] = exp.split("\n", 2);
        String title = arr[0];
        String detail = arr[1];
        Log.i(TAG, detail);

        db.delete(DB_TABLE, DB_COLUMN_NAME + "=? AND " + DB_COLUMN_DETAIL + "=?",
                new String[]{title, detail});
        db.close();
    }

    // Query the DB to get the details and build an expense which is added to an ArrayList
    // Get the detail from the
    public ArrayList<String> getTaskList(){
        ArrayList<String> taskList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor1 = db.query(DB_TABLE,new String[]{DB_COLUMN_NAME},null,null,null,null,null);
        Cursor cursor2 = db.query(DB_TABLE,new String[]{DB_COLUMN_DETAIL},null,null,null,null,null);
        while(cursor1.moveToNext()){
            cursor2.moveToNext();
            int index1 = cursor1.getColumnIndex(DB_COLUMN_NAME);
            int index2 = cursor2.getColumnIndex(DB_COLUMN_DETAIL);
            String s = cursor1.getString(index1) + '\n' + cursor2.getString(index2);
            //Log.i(TAG, s);
            taskList.add(s);
        }
        cursor1.close();
        cursor2.close();
        db.close();
        return taskList;
    }
}
