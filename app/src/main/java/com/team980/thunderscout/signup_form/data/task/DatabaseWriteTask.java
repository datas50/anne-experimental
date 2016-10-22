package com.team980.thunderscout.signup_form.data.task;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.team980.thunderscout.signup_form.MainActivity;
import com.team980.thunderscout.signup_form.data.ServerDataContract;
import com.team980.thunderscout.signup_form.data.ServerDataDbHelper;
import com.team980.thunderscout.signup_form.data.StudentData;
import com.team980.thunderscout.signup_form.recruit.ScoutActivity;

public class DatabaseWriteTask extends AsyncTask<Void, Integer, Void> {

    private final StudentData data;
    private Context context;

    private LocalBroadcastManager localBroadcastManager;

    private ScoutActivity activity;

    public DatabaseWriteTask(StudentData data, Context context) {
        this.data = data;

        this.context = context;

        localBroadcastManager = LocalBroadcastManager.getInstance(context);
    }

    public DatabaseWriteTask(StudentData data, Context context, ScoutActivity activity) {
        this(data, context);

        this.activity = activity;
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
        values.put(ServerDataContract.StudentDataTable.COLUMN_NAME_DATA_SOURCE, data.getDataSource());

        // Insert the new row, returning the primary key value of the new row
        long newRowId;
        newRowId = db.insert(
                ServerDataContract.StudentDataTable.TABLE_NAME,
                null,
                values);

        if (newRowId == -1) {
            if (activity != null) {
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {

                    @Override
                    public void run() {  //TODO broadcast reciever
                        activity.dataOutputCallback(ScoutActivity.OPERATION_SAVE_THIS_DEVICE, false);
                    }
                });
            }
        } else {
            if (activity != null) {
                Handler handler = new Handler(Looper.getMainLooper());

                handler.post(new Runnable() {

                    @Override
                    public void run() {  //TODO broadcast reciever
                        activity.dataOutputCallback(ScoutActivity.OPERATION_SAVE_THIS_DEVICE, true);
                    }
                });
            }

            publishProgress((int) newRowId);
        }

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
