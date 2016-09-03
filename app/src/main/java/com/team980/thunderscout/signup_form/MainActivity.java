package com.team980.thunderscout.signup_form;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.team980.thunderscout.signup_form.data.StudentData;
import com.team980.thunderscout.signup_form.data.task.DatabaseClearTask;
import com.team980.thunderscout.signup_form.data.task.DatabaseReadTask;
import com.team980.thunderscout.signup_form.info.DataViewAdapter;
import com.team980.thunderscout.signup_form.preferences.SettingsActivity;
import com.team980.thunderscout.signup_form.recruit.ScoutActivity;
import com.team980.thunderscout.signup_form.sheets.SheetsSendTask;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, DialogInterface.OnClickListener {

    private RecyclerView dataView;
    private DataViewAdapter adapter;

    private SwipeRefreshLayout swipeContainer;

    private BroadcastReceiver refreshReceiver;

    private GoogleAccountCredential credential;
    private static final String[] SCOPES = {SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE};


    public static final String ACTION_REFRESH_VIEW_PAGER = "com.team980.thunderscout.signup_form.REFRESH_VIEW_PAGER";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Team 980");

        dataView = (RecyclerView) findViewById(R.id.dataView);

        // use a linear layout manager
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        dataView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)
        adapter = new DataViewAdapter(this, new ArrayList<StudentData>());
        dataView.setAdapter(adapter);

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(this);

        swipeContainer.setColorSchemeResources(R.color.primary);
        swipeContainer.setProgressBackgroundColorSchemeResource(R.color.cardview_light_background);

        DatabaseReadTask query = new DatabaseReadTask(adapter, this, swipeContainer);
        query.execute();

        refreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DatabaseReadTask query = new DatabaseReadTask(adapter, MainActivity.this, swipeContainer);
                query.execute();
            }
        };

        credential = GoogleAccountCredential.usingOAuth2(
                getApplicationContext(), Arrays.asList(SCOPES))
                .setBackOff(new ExponentialBackOff());
    }

    @Override
    protected void onPause() {
        super.onPause();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(refreshReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();

        LocalBroadcastManager.getInstance(this).registerReceiver(refreshReceiver, new IntentFilter(ACTION_REFRESH_VIEW_PAGER));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_delete && adapter.getItemCount() > 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Are you sure?")
                    .setMessage("This will delete all scout data in your local database and the data cannot be recovered!")
                    .setIcon(R.drawable.ic_warning_white_24dp)
                    .setPositiveButton(android.R.string.yes, this)
                    .setNegativeButton(android.R.string.no, null).show();
        }

        if (id == R.id.action_sheets) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString("google_account_name", null);
            if (accountName != null) {
                credential.setSelectedAccountName(accountName);
                startSheetsExport();
            } else {
                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        credential.newChooseAccountIntent(),
                        1000);
            }
        }

        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) { //TODO this doesn't do what's advertised
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1000: //REQUEST_ACCOUNT_PICKER
                if (resultCode == RESULT_OK && data != null &&
                        data.getExtras() != null) {
                    String accountName =
                            data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        SharedPreferences settings =
                                getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("google_account_name", accountName);
                        editor.apply();
                        credential.setSelectedAccountName(accountName);
                        startSheetsExport();
                    }
                }
                break;
            case 1001: //REQUEST_AUTH
                if (resultCode == RESULT_OK) {
                    startSheetsExport();
                }
                break;
        }
    }

    public void startSheetsExport() {
        ProgressDialog mProgress = new ProgressDialog(this);
        mProgress.setMessage("Exporting data to Google Sheets...");

        List<StudentData> data = adapter.getDataList();

        SheetsSendTask sendTask = new SheetsSendTask(credential, this, mProgress);
        sendTask.execute(data);
    }

    /**
     * Called when the scout button is pressed.
     */
    public void onButtonPressed(View v) {
        if (v.getId() == R.id.button_scout) {
            Intent scoutIntent = new Intent(this, ScoutActivity.class);
            startActivity(scoutIntent);
        }
    }

    /**
     * SwipeRefreshLayout
     */
    @Override
    public void onRefresh() {
        DatabaseReadTask query = new DatabaseReadTask(adapter, this, swipeContainer);
        query.execute();
    }

    /**
     * Alert dialog shown for deletion prompt
     */
    @Override
    public void onClick(DialogInterface dialog, int whichButton) {
        DatabaseClearTask clearTask = new DatabaseClearTask(adapter, this);
        clearTask.execute();
    }
}
