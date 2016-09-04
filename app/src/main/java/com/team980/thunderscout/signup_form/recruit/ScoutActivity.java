package com.team980.thunderscout.signup_form.recruit;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.team980.thunderscout.signup_form.R;
import com.team980.thunderscout.signup_form.ThunderScout;
import com.team980.thunderscout.signup_form.data.StudentData;
import com.team980.thunderscout.signup_form.data.task.DatabaseWriteTask;

public class ScoutActivity extends AppCompatActivity implements View.OnClickListener {

    private StudentData studentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            studentData = (StudentData) savedInstanceState.getSerializable("StudentData");
        } else {
            studentData = new StudentData(); //TODO cache this if the user wishes to
        }

        setContentView(R.layout.activity_scout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Sign up");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_scout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putSerializable("StudentData", studentData);
    }


    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this) //TODO specify newer icon
                .setIcon(R.drawable.ic_warning_white_24dp)
                .setTitle("Are you sure you want to exit?")
                .setMessage("The data currently in the scouting form will be lost!")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        ScoutActivity.super.onBackPressed();
                    }
                }).create().show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.submitButton) {

            TextInputLayout tilStudentName = (TextInputLayout) findViewById(R.id.signup_tilStudentName);
            TextInputLayout tilStudentEmail = (TextInputLayout) findViewById(R.id.signup_tilStudentEmail);
            TextInputLayout tilStudentPhoneNumber = (TextInputLayout) findViewById(R.id.signup_tilStudentPhoneNumber);
            TextInputLayout tilStudentGrade = (TextInputLayout) findViewById(R.id.signup_tilStudentGrade);

            if (tilStudentName.getEditText().getText().toString().isEmpty()) {
                tilStudentName.setError("This field is required");
                return;
            }

            tilStudentName.setErrorEnabled(false);

            if (tilStudentGrade.getEditText().getText().toString().isEmpty()) {
                tilStudentGrade.setError("This field is required");
                return;
            }

            if (!ThunderScout.isInteger(tilStudentGrade.getEditText().getText().toString())) {
                tilStudentGrade.setError("This must be an integer!");
                return;
            }

            tilStudentGrade.setErrorEnabled(false);

            studentData.setName(tilStudentName.getEditText().getText().toString());
            studentData.setEmail(tilStudentEmail.getEditText().getText().toString());
            studentData.setPhoneNumber(tilStudentPhoneNumber.getEditText().getText().toString());
            studentData.setGrade(Integer.valueOf(tilStudentGrade.getEditText().getText().toString()));

            studentData.setDataSource(StudentData.SOURCE_LOCAL_DEVICE);

            DatabaseWriteTask task = new DatabaseWriteTask(new StudentData(studentData), this);

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            String address = prefs.getString("bt_server_device", null);
            //if (address == null) {
                //return; //TODO notify
            //}

            //TODO Disable BT system for now - reenable
            /*Log.d("TS-BT", "START1");
            for (BluetoothDevice device : BluetoothAdapter.getDefaultAdapter().getBondedDevices()) {
                if (device.getAddress().equals(address)) {
                    studentData.setDataSource(BluetoothAdapter.getDefaultAdapter().getName());

                    Log.d("TS-BT", device.getName());
                    ClientConnectionThread connectThread = new ClientConnectionThread(device, studentData, this); //Copy constructor ;)
                    connectThread.start();

                    task.execute();

                    finish();
                }
            }*/
        }
    }

}
