package com.team980.thunderscout.signup_form.info;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.team980.thunderscout.signup_form.R;
import com.team980.thunderscout.signup_form.data.StudentData;

public class InfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent launchIntent = getIntent();

        StudentData data = (StudentData) launchIntent.getSerializableExtra("com.team980.thunderscout.signup_form.INFO_STUDENT");

        setTitle(data.getName());

        setContentView(R.layout.activity_info);

        TextView studentEmail = (TextView) findViewById(R.id.info_studentEmail);
        studentEmail.setText(data.getEmail());

        TextView studentPhoneNumber = (TextView) findViewById(R.id.info_studentPhoneNumber);
        studentPhoneNumber.setText(data.getPhoneNumber());

        TextView studentGrade = (TextView) findViewById(R.id.info_studentGrade);
        studentGrade.setText("" + data.getGrade());

        TextView dataSource = (TextView) findViewById(R.id.info_dataSource);
        dataSource.setText(data.getDataSource());
    }
}

