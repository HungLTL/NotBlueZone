package com.example.notbluezone.sqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.view.View;

import androidx.core.net.ParseException;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notbluezone.MainActivity;

import org.jetbrains.annotations.Contract;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, ContractClass.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String s : ContractClass.SQL_CREATE_TABLE_ARRAY)
            db.execSQL(s);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (String s : ContractClass.SQL_DELETE_TABLE_ARRAY)
            db.execSQL(s);
        onCreate(db);
    }

    public void logOutAllAccounts(){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CUser.KEY_STATUS, 0);
        db.update(ContractClass.CUser.TABLE_NAME, values, null, null);
        MainActivity.user_id = -1;
    }

    public Cursor getLoggedInAccount() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_STATUS + " = ?", new String[] {Integer.toString(1)});
        if (cursor.moveToFirst())
            return cursor;
        else
            return null;
    }

    public void updateLoginStatus(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CUser.KEY_STATUS, 1);

        String selection = ContractClass.CUser.KEY_USER + " = ?";
        String[] selectionArgs = {Integer.toString(id)};
        db.update(ContractClass.CUser.TABLE_NAME, values, selection, selectionArgs);

        MainActivity.user_id = id;
    }

    public void addAccount(int id, int privilege, Context context) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ContractClass.CUser.KEY_USER, id);
        values.put(ContractClass.CUser.KEY_PRIVILEGE, privilege);

        db.insert(ContractClass.CUser.TABLE_NAME, null, values);

        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserById.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    ContentValues values1 = new ContentValues();
                    values1.put(ContractClass.CFamilyMember.KEY_USER, id);
                    values1.put(ContractClass.CFamilyMember.KEY_NAME, result.getString("name").length() > 0 ? result.getString("name") : "");
                    values1.put(ContractClass.CFamilyMember.KEY_DOB, result.getString("DoB").length() > 0 ? result.getString("DoB") : "");
                    values1.put(ContractClass.CFamilyMember.KEY_CARD, result.getString("card_id").length() > 0 ? result.getString("card_id") : "");
                    values1.put(ContractClass.CFamilyMember.KEY_NAT, result.getString("nat_id").length() > 0 ? result.getString("nat_id") : "");
                    values1.put(ContractClass.CFamilyMember.KEY_GENDER, Math.max(result.getInt("gender"), 0));
                    values1.put(ContractClass.CFamilyMember.KEY_PHONE, result.getString("phone").length() > 0 ? result.getString("phone") : "");

                    long famId = db.insert(ContractClass.CFamilyMember.TABLE_NAME, null, values1);

                    ContentValues values2 = new ContentValues();
                    values2.put(ContractClass.CUser.KEY_FAMILY_MEMBER_ID, famId);

                    String selection = ContractClass.CUser.KEY_USER + " = ?";
                    String[] selectionArgs = { Integer.toString(id) };

                    db.update(ContractClass.CUser.TABLE_NAME, values2, selection, selectionArgs);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public int getUserById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});

        if (cursor != null && cursor.getCount()>0) {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(ContractClass.CUser._ID));
        }
        else
            return -1;
    }

    public void updateFamilyMemberName(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_NAME, name);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateName(int id, String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            int fam_id = (int) cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
            updateFamilyMemberName(fam_id, name);
            cursor.close();
        }
    }

    public void updateFamilyMemberDoB(int id, String DoB) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_DOB, DoB);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateDoB(int id, String DoB) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            int fam_id = (int) cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
            updateFamilyMemberDoB(fam_id, DoB);
            cursor.close();
        }
    }

    public void updateFamilyMemberGender(int id, int gender) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_GENDER, gender);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateGender(int id, int gender) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            int fam_id = (int) cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
            updateFamilyMemberGender(fam_id, gender);
            cursor.close();
        }
    }

    public void updateFamilyMemberNationality(int id, String nat) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_NAT, nat);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateNationality(int id, String nat) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            int fam_id = (int) cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
            updateFamilyMemberNationality(fam_id, nat);
            cursor.close();
        }
    }

    public void updateFamilyMemberCard(int id, String card) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_CARD, card);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updateCard(int id, String card) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            int fam_id = (int) cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
            updateFamilyMemberCard(fam_id, card);
            cursor.close();
        }
    }

    public void updateFamilyMemberPhone(int id, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_PHONE, phone);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public void updatePhone(int id, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[] {Integer.toString(id)});
        if ((cursor != null) && (cursor.getCount() > 0)) {
            cursor.moveToFirst();
            int fam_id = (int) cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
            updateFamilyMemberPhone(fam_id, phone);
            cursor.close();
        }
    }

    public Cursor getFamilyMembers(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CFamilyMember.TABLE_NAME + " WHERE " + ContractClass.CFamilyMember.KEY_USER + " = ?", new String[] {Integer.toString(id)});

        if ((cursor != null) && (cursor.getCount() > 0))
            return cursor;
        else
            return null;
    }

    public int getFamilyMemberIdOfUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser.KEY_USER + " = ?", new String[]{Integer.toString(id)});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex(ContractClass.CUser.KEY_FAMILY_MEMBER_ID));
        } else
            return -1;
    }

    public Cursor getFamilyMemberFromId(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CUser.TABLE_NAME + " WHERE " + ContractClass.CUser._ID + " = ?", new String[] {Integer.toString(id)} );
        if (cursor != null && cursor.getCount() > 0)
            return cursor;
        else
            return null;
    }

    public Cursor getFamilyMember(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CFamilyMember.TABLE_NAME + " WHERE " + ContractClass.CFamilyMember._ID + " = ?", new String[] {Integer.toString(id)} );
        if (cursor != null && cursor.getCount() > 0)
            return cursor;
        else
            return null;
    }

    public void addFamilyMember(int id, String name, String DoB, int gender, String nat, String card, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_USER, id);
        values.put(ContractClass.CFamilyMember.KEY_NAME, name);
        values.put(ContractClass.CFamilyMember.KEY_DOB, DoB);
        values.put(ContractClass.CFamilyMember.KEY_GENDER, gender);
        values.put(ContractClass.CFamilyMember.KEY_NAT, nat);
        values.put(ContractClass.CFamilyMember.KEY_CARD, card);
        values.put(ContractClass.CFamilyMember.KEY_PHONE, phone);

        db.insert(ContractClass.CFamilyMember.TABLE_NAME, null, values);
    }

    public void updateFamilyMember(int id, String name, String DoB, int gender, String nat, String card, String phone) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CFamilyMember.KEY_NAME, name);
        values.put(ContractClass.CFamilyMember.KEY_DOB, DoB);
        values.put(ContractClass.CFamilyMember.KEY_GENDER, gender);
        values.put(ContractClass.CFamilyMember.KEY_NAT, nat);
        values.put(ContractClass.CFamilyMember.KEY_CARD, card);
        values.put(ContractClass.CFamilyMember.KEY_PHONE, phone);

        String selection = ContractClass.CFamilyMember._ID + " = ?";
        String[] selectionArgs = { Integer.toString(id) };

        db.update(ContractClass.CFamilyMember.TABLE_NAME, values, selection, selectionArgs);
    }

    public Cursor getSymptomHistoryDetail(int id, String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CSymptomHistory.TABLE_NAME
                + " WHERE " + ContractClass.CSymptomHistory.KEY_FAMILY_ID + " = ? AND "
                + ContractClass.CSymptomHistory.KEY_DATE + " = ?", new String[] {Integer.toString(id), date});

        if (cursor.moveToFirst()) {
            long symp_id = cursor.getLong(cursor.getColumnIndexOrThrow(ContractClass.CSymptomHistory._ID));

            Cursor cursor1 = db.rawQuery("SELECT * FROM " + ContractClass.CSymptomDetails.TABLE_NAME + " WHERE " + ContractClass.CSymptomDetails.KEY_SYMPTOM_HISTORY + " = ?", new String[] {Long.toString(symp_id)});
            if (cursor1 != null && cursor1.getCount() > 0) {
                cursor.close();
                return cursor1;
            }
            else {
                cursor.close();
                cursor1.close();
                return null;
            }
        }
        else {
            cursor.close();
            return null;
        }
    }

    public ArrayList<String> getSymptomHistoryDetailPrev14Days(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DAY_OF_MONTH, -14);
        Date startDate = calendarStart.getTime();

        String endDateString = format.format(endDate);
        String startDateString = format.format(startDate);

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CSymptomHistory.TABLE_NAME
                + " WHERE " + ContractClass.CSymptomHistory.KEY_FAMILY_ID + " = ? AND ("
                + ContractClass.CSymptomHistory.KEY_DATE + " BETWEEN ? AND ?)", new String[] {Integer.toString(id), startDateString, endDateString});

        if (cursor.moveToFirst()) {
            ArrayList<String> symptomsList = new ArrayList<>();
            do {
                Cursor cursor1 = db.rawQuery("SELECT * FROM " + ContractClass.CSymptomDetails.TABLE_NAME + " WHERE " + ContractClass.CSymptomDetails.KEY_SYMPTOM_HISTORY + " = ?", new String[] {Long.toString(cursor.getLong(cursor.getColumnIndex(ContractClass.CSymptomHistory._ID)))});

                if (cursor1.moveToFirst()) {
                    do {
                        String symptom = cursor1.getString(cursor1.getColumnIndex(ContractClass.CSymptomDetails.KEY_SYMPTOM));
                        if (symptomsList.size() > 0) {
                            for (int i = 0;i < symptomsList.size() ; i++) {
                                if (!symptomsList.get(i).equals(symptom)) {
                                    symptomsList.add(symptom);
                                    break;
                                }
                            }
                        }
                        else
                            symptomsList.add(symptom);
                    } while (cursor1.moveToNext());
                }
            } while(cursor.moveToNext());
            cursor.close();
            return symptomsList;
        }
        else {
            cursor.close();
            return null;
        }
    }

    public void addSymptom(int id, String symptom) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CSymptomDetails.KEY_SYMPTOM_HISTORY, id);
        values.put(ContractClass.CSymptomDetails.KEY_SYMPTOM, symptom);

        db.insert(ContractClass.CSymptomDetails.TABLE_NAME, null, values);
    }

    public void deleteSymptom(int id, String symptom) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = ContractClass.CSymptomDetails.KEY_SYMPTOM_HISTORY + " = ? AND " + ContractClass.CSymptomDetails.KEY_SYMPTOM + " = ?";
        String[] selectionArgs = { Integer.toString(id) , symptom};

        db.delete(ContractClass.CSymptomDetails.TABLE_NAME, selection, selectionArgs);
    }

    public long addSymptomHistory(int id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CSymptomHistory.KEY_FAMILY_ID, id);
        values.put(ContractClass.CSymptomHistory.KEY_DATE, date);

        return db.insert(ContractClass.CSymptomHistory.TABLE_NAME, null, values);
    }

    public int getSymptomHistory(int id, String date) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CSymptomHistory.TABLE_NAME
                + " WHERE " + ContractClass.CSymptomHistory.KEY_FAMILY_ID + " = ? AND "
                + ContractClass.CSymptomHistory.KEY_DATE + " = ?", new String[] {Integer.toString(id), date});
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            int return_id = (int) cursor.getLong(cursor.getColumnIndex(ContractClass.CSymptomHistory._ID));
            cursor.close();
            return return_id;
        } else
            return -1;
    }

    public void addTravelHistory(String address, String city, String district, String date) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ContractClass.CTravelHistory.KEY_ADDRESS, address);
        values.put(ContractClass.CTravelHistory.KEY_CITY, city);
        values.put(ContractClass.CTravelHistory.KEY_DISTRICT, district);
        values.put(ContractClass.CTravelHistory.KEY_DATE, date);

        db.insert(ContractClass.CTravelHistory.TABLE_NAME, null, values);
    }

    public Cursor getTravelHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CTravelHistory.TABLE_NAME, null);
        if (cursor!=null && cursor.getCount() > 0)
            return cursor;
        else
            return null;
    }

    public Cursor getTravelHistoryPast14Days() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        Date endDate = calendar.getTime();
        Calendar calendarStart = Calendar.getInstance();
        calendarStart.add(Calendar.DAY_OF_MONTH, -14);
        Date startDate = calendarStart.getTime();

        String endDateString = format.format(endDate);
        String startDateString = format.format(startDate);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + ContractClass.CTravelHistory.TABLE_NAME + " WHERE (" + ContractClass.CTravelHistory.KEY_DATE + " BETWEEN ? AND ?)", new String[] {startDateString, endDateString});
        if (cursor!=null && cursor.getCount() > 0)
            return cursor;
        else
            return null;
    }
}
