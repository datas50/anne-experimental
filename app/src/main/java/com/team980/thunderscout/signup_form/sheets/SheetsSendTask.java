package com.team980.thunderscout.signup_form.sheets;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.CellData;
import com.google.api.services.sheets.v4.model.ExtendedValue;
import com.google.api.services.sheets.v4.model.GridData;
import com.google.api.services.sheets.v4.model.RowData;
import com.google.api.services.sheets.v4.model.Sheet;
import com.google.api.services.sheets.v4.model.Spreadsheet;
import com.team980.thunderscout.signup_form.MainActivity;
import com.team980.thunderscout.signup_form.data.StudentData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SheetsSendTask extends AsyncTask<List<StudentData>, Void, Void> {

    private Sheets sheetsService = null;

    private MainActivity activity;
    private ProgressDialog progressDialog;

    public SheetsSendTask(GoogleAccountCredential credential, MainActivity activity, ProgressDialog progress) {
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

        sheetsService = new Sheets.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("ThunderScout")
                .build();

        this.activity = activity;

        progressDialog = progress;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.show();
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        progressDialog.hide();
        //TODO display link?
    }

    @SafeVarargs //?
    @Override
    /**
     * Method where it all happens
     */
    protected final Void doInBackground(List<StudentData>... data) {
        List<StudentData> dataList = data[0];

        ArrayList<RowData> rows = new ArrayList<>();
        for (StudentData student : dataList) {
            ArrayList<CellData> cells = new ArrayList<>();

            CellData name = new CellData();
            name.setUserEnteredValue(
                    new ExtendedValue().setStringValue(student.getName()));
            cells.add(name);

            CellData email = new CellData();
            email.setUserEnteredValue(
                    new ExtendedValue().setStringValue(student.getEmail()));
            cells.add(email);

            CellData phoneNumber = new CellData();
            phoneNumber.setUserEnteredValue(
                    new ExtendedValue().setStringValue(student.getPhoneNumber()));
            cells.add(phoneNumber);

            CellData grade = new CellData();
            grade.setUserEnteredValue(
                    new ExtendedValue().setNumberValue((double) student.getGrade()));
            cells.add(grade);

            rows.add(new RowData().setValues(cells));
        }

        Spreadsheet sheet = new Spreadsheet();

        GridData gridData = new GridData();
        gridData.setRowData(rows);

        ArrayList<GridData> gridList = new ArrayList<>();
        gridList.add(gridData);

        Sheet dataSheet = new Sheet();
        dataSheet.setData(gridList);

        ArrayList<Sheet> sheetList = new ArrayList<>();
        sheetList.add(dataSheet);

        sheet.setSheets(sheetList);

        try {
            sheetsService.spreadsheets().create(sheet).setKey("AIzaSyCzlrwVzibLYH3-J1FIpNjvBLeUt1A5ZdU").execute(); //TODO this is most definitely not a insecure BROWSER key, move along...
        } catch (UserRecoverableAuthIOException e) {
            // Requesting an authorization code will always throw
            // UserRecoverableAuthException on the first call to GoogleAuthUtil.getToken
            // because the user must consent to offline access to their data.  After
            // consent is granted control is returned to your activity in onActivityResult
            // and the second call to GoogleAuthUtil.getToken will succeed.
            activity.startActivityForResult(e.getIntent(), 1001);
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
