package com.example.notbluezone.sqlite;

import android.provider.BaseColumns;

public final class ContractClass {
    public static final String AUTHORITY = "com.example.notbluezone";
    public static final String SCHEME = "content://";
    public static final String SLASH = "/";
    public static final String DATABASE_NAME = "notbluezone.db";

    public static final String[] SQL_CREATE_TABLE_ARRAY = {
            CUser.CREATE_TABLE,
            CFamilyMember.CREATE_TABLE,
            CSymptomHistory.CREATE_TABLE,
            CSymptomDetails.CREATE_TABLE,
            CTravelHistory.CREATE_TABLE,
            CDeclaredForms.CREATE_TABLE
    };

    public static final String[] SQL_DELETE_TABLE_ARRAY = {
            CDeclaredForms.DELETE_TABLE,
            CTravelHistory.DELETE_TABLE,
            CSymptomDetails.DELETE_TABLE,
            CSymptomHistory.DELETE_TABLE,
            CFamilyMember.DELETE_TABLE,
            CUser.DELETE_TABLE
    };

    private ContractClass(){}

    public static final class CUser implements BaseColumns {
        private CUser() {}

        public static final String TABLE_NAME = "USERS",
            KEY_USER = "UserId",
            KEY_PRIVILEGE = "Privilege",
            KEY_STATUS = "Status",
            KEY_FAMILY_MEMBER_ID = "FamilyId";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_USER + " INTEGER,"
                + KEY_PRIVILEGE + " INTEGER,"
                + KEY_STATUS + " INTEGER,"
                + KEY_FAMILY_MEMBER_ID + " INTEGER"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class CFamilyMember implements BaseColumns {
        private CFamilyMember() {}

        public static final String TABLE_NAME = "FAMILY_MEMBERS",
            KEY_USER = "UserId",
            KEY_NAME = "Name",
            KEY_DOB = "DoB",
            KEY_CARD = "CardId",
            KEY_NAT = "NatId",
            KEY_GENDER = "Gender",
            KEY_PHONE = "Phone";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_USER + " INTEGER,"
                + KEY_NAME + " TEXT,"
                + KEY_DOB + " TEXT,"
                + KEY_CARD + " TEXT,"
                + KEY_NAT + " TEXT,"
                + KEY_GENDER + " INTEGER,"
                + KEY_PHONE + " TEXT,"
                + "FOREIGN KEY (" + KEY_USER + ") REFERENCES " + CUser.TABLE_NAME + "(" + CUser.KEY_USER + ")"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class CSymptomHistory implements BaseColumns {
        private CSymptomHistory() {}

        public static final String TABLE_NAME = "SYMPTOM_HISTORY",
            KEY_FAMILY_ID = "FamilyMemberId",
            KEY_DATE = "Date";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_FAMILY_ID + " INTEGER,"
                + KEY_DATE + " TEXT,"
                + "FOREIGN KEY (" + KEY_FAMILY_ID + ") REFERENCES " + CFamilyMember.TABLE_NAME + "(" + CFamilyMember._ID + ")"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class CSymptomDetails implements BaseColumns {
        private CSymptomDetails() {}

        public static final String TABLE_NAME = "SYMPTOM_DETAILS",
            KEY_SYMPTOM = "Symptom",
            KEY_SYMPTOM_HISTORY = "SymptomHistory";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_SYMPTOM_HISTORY + " INTEGER,"
                + KEY_SYMPTOM + " TEXT,"
                + "FOREIGN KEY (" + KEY_SYMPTOM_HISTORY + ") REFERENCES " + CSymptomHistory.TABLE_NAME + "(" + CSymptomHistory._ID + ")"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class CTravelHistory implements BaseColumns {
        private CTravelHistory() {}

        public static final String TABLE_NAME = "TRAVEL_HISTORY",
                KEY_CITY = "City",
                KEY_DISTRICT = "District",
                KEY_ADDRESS = "Address",
                KEY_DATE = "Date";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER PRIMARY KEY,"
                + KEY_ADDRESS + " TEXT,"
                + KEY_CITY + " TEXT,"
                + KEY_DISTRICT + " TEXT,"
                + KEY_DATE + " TEXT"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }

    public static final class CDeclaredForms implements BaseColumns {
        private CDeclaredForms() {}

        public static final String TABLE_NAME = "DECLARED_FORMS",
            KEY_FAMILY_MEMBER = "FamilyMember",
            KEY_FORM_TYPE = "FormType",
            KEY_QR_STRING = "QrString";

        public static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + " ("
                + _ID + " INTEGER_PRIMARY_KEY,"
                + KEY_FAMILY_MEMBER + " INTEGER,"
                + KEY_FORM_TYPE + " INTEGER,"
                + KEY_QR_STRING + " TEXT,"
                + "FOREIGN KEY (" + KEY_FAMILY_MEMBER + ") REFERENCES " + CFamilyMember.TABLE_NAME + "(" + CFamilyMember._ID + ")"
                + ");";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
