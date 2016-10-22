package com.team980.thunderscout.signup_form.recruit;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.team980.thunderscout.signup_form.R;
import com.team980.thunderscout.signup_form.ThunderScout;
import com.team980.thunderscout.signup_form.bluetooth.ClientConnectionThread;
import com.team980.thunderscout.signup_form.data.StudentData;
import com.team980.thunderscout.signup_form.data.task.DatabaseWriteTask;

public class ScoutActivity extends AppCompatActivity implements View.OnClickListener {

    private StudentData studentData;

    // IDs for callback
    public static final String OPERATION_SAVE_THIS_DEVICE = "SAVE_THIS_DEVICE";
    public static final String OPERATION_SEND_BLUETOOTH = "SEND_BLUETOOTH";

    private Bundle operationStates; //used for task loop

    private ProgressDialog operationStateDialog;

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

            Log.d("SCOUTLOOP", "here we go again");

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            boolean saveToThisDevice = prefs.getBoolean("ms_send_to_local_storage", true);
            boolean sendToBluetoothServer = prefs.getBoolean("ms_send_to_bt_server", false);

            operationStates = new Bundle();
            operationStates.putBoolean(OPERATION_SAVE_THIS_DEVICE, saveToThisDevice);
            operationStates.putBoolean(OPERATION_SEND_BLUETOOTH, sendToBluetoothServer);

            operationStateDialog = new ProgressDialog(this);
            operationStateDialog.setIndeterminate(true); //TODO can we use values too?
            operationStateDialog.setCancelable(false);
            operationStateDialog.setTitle("Storing data...");

            dataOutputLoop();
        }
    }

    private void dataOutputLoop() {
        Log.d("SCOUTLOOP", "ever get that feeling of deja vu?");
        if (!operationStateDialog.isShowing()) {
            operationStateDialog.show(); //Show it if it isn't already visible
        }

        if (operationStates.getBoolean(OPERATION_SAVE_THIS_DEVICE)) {
            studentData.setDataSource(StudentData.SOURCE_LOCAL_DEVICE);

            operationStateDialog.setMessage("Saving signup data to this device");

            DatabaseWriteTask task = new DatabaseWriteTask(new StudentData(studentData), getApplicationContext(), this); //MEMORY LEAK PREVENTION
            task.execute();

        } else if (operationStates.getBoolean(OPERATION_SEND_BLUETOOTH)) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

            String address = prefs.getString("ms_bt_server_device", null);

            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(address); //TODO THIS IS A NEW, BETTER SEND METHOD. NEEDS TESTING ;)
            studentData.setDataSource(BluetoothAdapter.getDefaultAdapter().getName());

            operationStateDialog.setMessage("Sending signup data to " + device.getName());

            ClientConnectionThread connectThread = new ClientConnectionThread(device, studentData, getApplicationContext(), this);
            connectThread.start();

        } else {
            operationStateDialog.dismiss();
            operationStateDialog = null;

            finish();
        }
    }

    //TODO broadcast reciever
    public void dataOutputCallback(final String operationId, boolean successful) {
        Log.d("SCOUTLOOP", "back into the fray");
        if (successful) {
            operationStates.putBoolean(operationId, false); //we're done with that!

            operationStateDialog.setMessage("");

            dataOutputLoop();
        } else {
            operationStateDialog.hide();

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("An error has occurred!")
                    .setIcon(R.drawable.ic_warning_white_24dp)
                    .setMessage("Would you like to reattempt the operation?")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            operationStates.putBoolean(operationId, true); //retry

                            dataOutputLoop();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            operationStates.putBoolean(operationId, false); //do not retry

                            dataOutputLoop();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
}
