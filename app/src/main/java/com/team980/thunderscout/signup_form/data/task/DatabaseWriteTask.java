package com.team980.thunderscout.signup_form.data.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.team980.thunderscout.signup_form.MainActivity;
import com.team980.thunderscout.signup_form.data.ServerDataContract;
import com.team980.thunderscout.signup_form.data.ServerDataDbHelper;
import com.team980.thunderscout.signup_form.data.StudentData;

public class DatabaseWriteTask extends AsyncTask<Void, Integer, Void> {

    private final StudentData data;
    private Context context;

    private LocalBroadcastManager localBroadcastManager;

    public DatabaseWriteTask(StudentData data, Context context) {
        this.data = data;

        this.context = context;

        localBroadcastManager = LocalBroadcastManager.getInstance(context);

    }

    @Override
    protected void onPreExecute() {
        //Runs on UI thread before execution
        super.onPreExecute();
    }

    @Override
    protected Void doInBackground(Void[] params) {

        //Put data into Database! :)
        ServerDataDbHelper mDbHelper = new ServerDataDbHelper(context);

        // Gets the data repository in write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(ServerDataContract.StudentDataTable.COLUMN_NAME_STUDENT_NAME, data.getName());
        values.put(ServerDataContract.StudentDataTable.COLUMN_NAME_STUDENT_EMAIL, data.getEmail());
        values.put(ServerDataContract.StudentDataTable.COLUMN_NAME_STUDENT_PHONE_NUMBER, data.getPhoneNumber());
        values.put(ServerDataContract.StudentDataTable.COLUMN_NAME_STUDENT_GRADE, data.getGrade());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                ServerDataContract.StudentDataTable.TABLE_NAME,
                null,
                values);

        publishProgress((int) newRowId);

        return null;
    }

    @Override
    protected void onProgressUpdate(Integer[] values) {
        //Runs on UI thread when publishProgress() is called
        super.onProgressUpdate(values);

        Toast.makeText(context, "Inserted into DB: Row " + values[0], Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onPostExecute(Void o) {
        //Runs on UI thread after execution
        super.onPostExecute(o);

        Intent intent = new Intent(MainActivity.ACTION_REFRESH_VIEW_PAGER);
        localBroadcastManager.sendBroadcast(intent); //notify the UI thread so we can refresh the ViewPager automatically :D
    }
}
