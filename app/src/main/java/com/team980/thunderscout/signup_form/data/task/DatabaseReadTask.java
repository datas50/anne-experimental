package com.team980.thunderscout.signup_form.data.task;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;

import com.team980.thunderscout.signup_form.data.ServerDataContract.StudentDataTable;
import com.team980.thunderscout.signup_form.data.ServerDataDbHelper;
import com.team980.thunderscout.signup_form.data.StudentData;
import com.team980.thunderscout.signup_form.info.DataViewAdapter;

/**
 * TODO Rewrite this class to remove redundancy, add sorting/filtering parameters
 */
@Deprecated()
public class DatabaseReadTask extends AsyncTask<Void, StudentData, Void> {

    private DataViewAdapter viewAdapter;
    private Context context;

    private SwipeRefreshLayout swipeLayout;

    public DatabaseReadTask(DataViewAdapter adapter, Context context) {
        viewAdapter = adapter;
        this.context = context;
    }

    public DatabaseReadTask(DataViewAdapter adapter, Context context, SwipeRefreshLayout refresh) {
        viewAdapter = adapter;
        this.context = context;

        swipeLayout = refresh;
    }

    @Override
    protected void onPreExecute() {
        //viewAdapter.clearData();

        if (swipeLayout != null) {

            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(true);
                }
            });
        }

        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... params) {

        SQLiteDatabase db = new ServerDataDbHelper(context).getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                StudentDataTable._ID,
                StudentDataTable.COLUMN_NAME_STUDENT_NAME,
                StudentDataTable.COLUMN_NAME_STUDENT_EMAIL,
                StudentDataTable.COLUMN_NAME_STUDENT_PHONE_NUMBER
        };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                StudentDataTable._ID + " DESC";

        Cursor cursor;

        try {
            cursor = db.query(
                    StudentDataTable.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    null,                                // The columns for the WHERE clause
                    null,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );
        } catch (SQLiteException e) {
            e.printStackTrace();
            return null;
        }

        if (cursor.moveToFirst()) {
            initScoutData(cursor);
        }

        while (cursor.moveToNext()) {
            initScoutData(cursor);
        }

        cursor.close();
        return null;
    }

    private void initScoutData(Cursor cursor) {
        StudentData data = new StudentData();

        String studentName = cursor.getString(
                cursor.getColumnIndexOrThrow(StudentDataTable.COLUMN_NAME_STUDENT_NAME));

        data.setName(studentName);

        String studentEmail = cursor.getString(
                cursor.getColumnIndexOrThrow(StudentDataTable.COLUMN_NAME_STUDENT_EMAIL));

        data.setEmail(studentEmail);

        String studentPhoneNumber = cursor.getString(
                cursor.getColumnIndexOrThrow(StudentDataTable.COLUMN_NAME_STUDENT_PHONE_NUMBER));

        data.setPhoneNumber(studentPhoneNumber);

        publishProgress(data);
    }

    @Override
    protected void onProgressUpdate(StudentData[] values) {
        //Runs on UI thread when publishProgress() is called
        viewAdapter.addStudentData(values[0]);

        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void o) {
        //Runs on UI thread after execution

        if (swipeLayout != null) {
            swipeLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeLayout.setRefreshing(false);
                }
            });
        }

        super.onPostExecute(o);
    }

}
