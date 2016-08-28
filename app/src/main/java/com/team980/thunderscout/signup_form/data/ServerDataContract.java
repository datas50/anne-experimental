package com.team980.thunderscout.signup_form.data;

import android.provider.BaseColumns;

public final class ServerDataContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private ServerDataContract() {
    }

    /* Inner class that defines the table contents */
    public static abstract class StudentDataTable implements BaseColumns {
        public static final String TABLE_NAME = "student_data";
        public static final String COLUMN_NAME_STUDENT_NAME = "name";
        public static final String COLUMN_NAME_STUDENT_EMAIL = "email";
        public static final String COLUMN_NAME_STUDENT_PHONE_NUMBER = "phone_number";
        public static final String COLUMN_NAME_STUDENT_GRADE = "grade";
    }
}

