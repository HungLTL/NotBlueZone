package com.example.notbluezone.sqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.notbluezone.MainActivity;
import com.example.notbluezone.adapters.AddressListAdapter;
import com.example.notbluezone.adapters.IdWithNameListItem;
import com.example.notbluezone.adapters.SlideItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PHPHelper {
    public static void getUsernameForAccountScreen(int id, TextView text, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserById.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    text.setText(result.getString("username"));
                    text.setVisibility(View.VISIBLE);
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

    public static void fetchUsernameAndEmail(int id, TextView text1, TextView text2, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserById.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    text1.setText(result.getString("username"));
                    text2.setText(result.getString("email"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("USERNAME_FRAGMENT", error.toString())) {
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

    public static void fetchNationality(List<IdWithNameListItem> list, ArrayAdapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getNationality.php";
        StringRequest request = new StringRequest(Request.Method.GET, sendUrl, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String strJson = jsonArray.getString(i);
                    JSONObject obj = new JSONObject(strJson);

                    String id = obj.getString("id");
                    String nationality = obj.getString("nationality");

                    IdWithNameListItem item = new IdWithNameListItem(id, nationality);
                    list.add(item);
                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_PERSONAL_INFO", error.toString()));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void fetchCities(List<IdWithNameListItem> list, ArrayAdapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getCities.php";
        StringRequest request = new StringRequest(Request.Method.GET, sendUrl, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                IdWithNameListItem foo = new IdWithNameListItem("", "Select city");
                list.add(foo);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < jsonArray.length(); i++) {
                    String strJson = jsonArray.getString(i);
                    JSONObject obj = new JSONObject(strJson);

                    String id = Integer.toString(obj.getInt("id"));
                    String city = obj.getString("city");

                    IdWithNameListItem item = new IdWithNameListItem(id, city);
                    list.add(item);
                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_ADDRESS", error.toString()));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void fetchDistricts(int city_id, List<IdWithNameListItem> list, ArrayAdapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getDistricts.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                IdWithNameListItem foo = new IdWithNameListItem("", "Select district");
                list.add(foo);
                adapter.notifyDataSetChanged();

                for (int i = 0; i < jsonArray.length(); i++) {
                    String strJson = jsonArray.getString(i);
                    JSONObject obj = new JSONObject(strJson);

                    String id = Integer.toString(obj.getInt("id"));
                    String city = obj.getString("district");

                    IdWithNameListItem item = new IdWithNameListItem(id, city);
                    list.add(item);
                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_ADDRESS", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("city_id", Integer.toString(city_id));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void fetchVaxPriority(List<IdWithNameListItem> list, ArrayAdapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getVaxPriority.php";
        StringRequest request = new StringRequest(Request.Method.GET, sendUrl, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String strJson = jsonArray.getString(i);
                    JSONObject obj = new JSONObject(strJson);

                    int id = obj.getInt("id");
                    String priority = obj.getString("priority");

                    IdWithNameListItem item = new IdWithNameListItem(Integer.toString(id), priority);
                    list.add(item);
                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_VAX", error.toString()));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void fetchVaxList(List<IdWithNameListItem> list, ArrayAdapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getVaxList.php";
        StringRequest request = new StringRequest(Request.Method.GET, sendUrl, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);

                for (int i = 0; i < jsonArray.length(); i++) {
                    String strJson = jsonArray.getString(i);
                    JSONObject obj = new JSONObject(strJson);

                    String id = obj.getString("id");
                    String vaccine = obj.getString("vaccine");

                    IdWithNameListItem item = new IdWithNameListItem(id, vaccine);
                    list.add(item);
                    adapter.notifyDataSetChanged();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ACCOUNT_VAX", error.toString()));

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void updateUserName(int id, String name, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserName.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update name.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.updateName(id, name);
                    Toast.makeText(context, "Name updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("name", name);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void updateUserDoB(int id, String DoB, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserDoB.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update date of birth.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.updateDoB(id, DoB);
                    Toast.makeText(context, "Date of birth updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("DoB", DoB);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void updateUserGender(int id, int gender, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserGender.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update gender.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.updateGender(id, gender);
                    Toast.makeText(context, "Gender updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("gender", Integer.toString(gender));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void updateUserNationality(int id, String nat, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserNationality.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update nationality.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.updateNationality(id, nat);
                    Toast.makeText(context, "Nationality updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("nat_id", nat);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void updateUserCard(int id, String card, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserCard.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update card number.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.updateCard(id, card);
                    Toast.makeText(context, "Card numbers updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("card_id", card);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void updateUserPhone(int id, String phone, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserPhone.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update phone number.", Toast.LENGTH_SHORT).show();
                }
                else {
                    DatabaseHelper dbHelper = new DatabaseHelper(context);
                    dbHelper.updatePhone(id, phone);
                    Toast.makeText(context, "Phone number updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("phone", phone);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void addOrUpdateAddress(int id, int city_id, int district_id, String address, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserById.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    if (!result.isNull("address_id"))
                        updateExistingUserAddress(id, city_id, district_id, address, context);
                    else
                        AddUserAddress(id, city_id, district_id, address, context);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("ADDRESS_FRAGMENT", error.toString())) {
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

    public static void updateExistingUserAddress(int id, int city_id, int district_id, String address, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserAddress.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success)
                    Toast.makeText(context, "Failed to update address.", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Address updated!", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("city_id", Integer.toString(city_id));
                params.put("district_id", Integer.toString(district_id));
                params.put("address", address);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void AddUserAddress(int id, int city_id, int district_id, String address, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/addAddress.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success)
                    Toast.makeText(context, "Address added!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(context, "Failed to add user address.", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("city_id", Integer.toString(city_id));
                params.put("district_id", Integer.toString(district_id));
                params.put("address", address);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void setUpAddressFragment(int id, TextView text, Spinner spin1, Spinner spin2, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserAddress.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);

                text.setText(result.getString("address"));

                final Handler handler1 = new Handler();
                handler1.postDelayed(() -> {
                    for (int j = 0; j < spin1.getAdapter().getCount(); j++) {
                        try {
                            if (spin1.getAdapter().getItem(j).toString().contains(result.getString("city_id"))) {
                                spin1.setSelection(j);

                                final Handler handler = new Handler();
                                handler.postDelayed(() -> {
                                    try {
                                        if (result.getString("city_id").contains("1") || result.getString("city_id").contains("5")
                                                || result.getString("city_id").contains("2") || result.getString("city_id").contains("4")
                                                || result.getString("city_id").contains("6")) {
                                            for (int k = 0; k < spin2.getAdapter().getCount(); k++) {
                                                if (spin2.getAdapter().getItem(k).toString().contains(result.getString("district_id"))) {
                                                    spin2.setSelection(k);
                                                    break;
                                                }
                                            }
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, 1000);
                                break;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, 1000);

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

    public static void updateUsernameAndEmail(int id, String username, String email, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserUsernameEmail.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update login data.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Login data updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("USERNAMEEMAIL", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("username", username);
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void prepareUpdatePassword(int id, String currentPassword, String newPassword, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getUserById.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    if (result.getString("password").equals(currentPassword)) {
                        if (newPassword.equals(currentPassword))
                            Toast.makeText(context, "Your new password cannot be the same as your old one!", Toast.LENGTH_SHORT).show();
                        else
                            updatePassword(id, newPassword, context);
                    }
                    else
                        Toast.makeText(context, "Input does not match current password!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PASSWORD_FRAGMENT", error.toString())) {
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

    public static void updatePassword(int id, String newPassword, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/updateUserPassword.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (!success) {
                    Toast.makeText(context, "Failed to update password", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "Password updated!", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("PERSONALINFO", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("password", newPassword);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void getLastVaxForm(int id, Spinner spin, EditText text, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getLatestVaxForm.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                Log.i("tagconvertstr", "["+response+"]");
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    final Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        try {
                            if (result.getString("vax_id").length() > 0) {
                                for (int k = 0; k < spin.getAdapter().getCount(); k++) {
                                    if (spin.getAdapter().getItem(k).toString().contains(result.getString("vax_id"))) {
                                        spin.setSelection(k);
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }, 1000);

                    if (!result.isNull("shot_number")) {
                        int dose = result.getInt("shot_number") + 1;
                        text.setText(Integer.toString(dose));
                    }
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

    public static void addHealthForm(int user_id, int onbehalf, String date, String name, String DoB,
                                     String card_id, String nat_id, int gender, String phone, int city_id,
                                     int district_id, String address, int sus, int nat, int sym,
                                     int ChestPain, int Cough, int Diarhhea, int Fatigue, int Fever,
                                     int Headache, int NoBreath, int Pain, int SenseLoss, int RedEye,
                                     int SoreThroat, int NoTalk, int Rash, RecyclerView rvAddress, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/addHealthForm.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                int base_form_id = result.getInt("base_form_id");
                if (base_form_id > -1)
                    addTravelHistory(base_form_id, rvAddress, context);
                else
                    Toast.makeText(context, "Failed to add health form.", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HEALTHFORM", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Integer.toString(user_id));
                params.put("onbehalf", Integer.toString(onbehalf));
                params.put("date", date);
                params.put("name", name);
                params.put("DoB", DoB);
                params.put("card_id", card_id);
                params.put("nat_id", nat_id);
                params.put("gender", Integer.toString(gender));
                params.put("phone", phone);
                params.put("city_id", Integer.toString(city_id));
                params.put("district_id", Integer.toString(district_id));
                params.put("address", address);
                params.put("contact_with_suspect", Integer.toString(sus));
                params.put("contact_with_national", Integer.toString(nat));
                params.put("contact_with_symptoms", Integer.toString(sym));
                params.put("chest_pain", Integer.toString(ChestPain));
                params.put("cough", Integer.toString(Cough));
                params.put("diarrhea", Integer.toString(Diarhhea));
                params.put("fatigue", Integer.toString(Fatigue));
                params.put("fever", Integer.toString(Fever));
                params.put("headache", Integer.toString(Headache));
                params.put("no_breath", Integer.toString(NoBreath));
                params.put("pain", Integer.toString(Pain));
                params.put("sense_loss", Integer.toString(SenseLoss));
                params.put("red_eye", Integer.toString(RedEye));
                params.put("sore_throat", Integer.toString(SoreThroat));
                params.put("no_talk", Integer.toString(NoTalk));
                params.put("rash", Integer.toString(Rash));
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void addTravelHistory(int base_form_id, RecyclerView rvAddress, Context context) {
        if (rvAddress.getChildCount() > 0) {
            for (int i = 0; i < rvAddress.getChildCount(); i++) {
                AddressListAdapter.ViewHolder viewHolder = (AddressListAdapter.ViewHolder) rvAddress.getChildViewHolder(rvAddress.getChildAt(i));

                int city_id = 0;
                IdWithNameListItem city = (IdWithNameListItem) viewHolder.spinAdapterCity.getSelectedItem();
                if (!city.toString().contains("Select city"))
                    city_id = Integer.parseInt(city.getId());

                int district_id = 0;
                IdWithNameListItem district = (IdWithNameListItem) viewHolder.spinAdapterDistrict.getSelectedItem();
                if (!district.toString().contains("Select district"))
                    district_id = Integer.parseInt(district.getId());

                String address = "";
                if (!viewHolder.txtAdapterAddress.getText().toString().isEmpty())
                    address = viewHolder.txtAdapterAddress.getText().toString();

                String dateOfVisit = "";
                if (!viewHolder.txtAdapterDate.getText().toString().isEmpty())
                    dateOfVisit = viewHolder.txtAdapterDate.getText().toString();

                if (!((city_id == 0) && (district_id == 0) && (address.isEmpty()) && (dateOfVisit.isEmpty())))
                    addSingleTravelHistory(base_form_id, city_id, district_id, address, dateOfVisit, context);

                if (i == rvAddress.getChildCount() - 1)
                    Toast.makeText(context, "Health form successfully added!", Toast.LENGTH_SHORT).show();
            }
        } else
            Toast.makeText(context, "Health form successfully added!", Toast.LENGTH_SHORT).show();
    }

    public static void addSingleTravelHistory(int base_form_id, int city_id, int district_id, String address,
                                              String dateOfVisit, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/addHealthFormTravelHistory.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HEALTHFORM", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("base_form_id", Integer.toString(base_form_id));
                params.put("city_id", Integer.toString(city_id));
                params.put("district_id", Integer.toString(district_id));
                params.put("address", address);
                params.put("date_of_visit", dateOfVisit);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void addVaxForm(int user_id, int onbehalf, String date, String name, String DoB,
                                     String card_id, String nat_id, int gender, String phone, int city_id,
                                     int district_id, String address, String occupation, String vax_date,
                                    int priority_id, String vax_id, int dose, int c1, String allergens,
                                    int c2, int c3, String vax_list, int c4, int c5, int c6, String acute,
                                    int c7, String chronic, int c8, int c9, int c11, int c12, String other_allergens,
                                  Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/addVaxForm.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                int base_form_id = result.getInt("base_form_id");
                if (base_form_id <= -1)
                    Toast.makeText(context, "Failed to add vax form.", Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HEALTHFORM", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_id", Integer.toString(user_id));
                params.put("onbehalf", Integer.toString(onbehalf));
                params.put("date", date);
                params.put("name", name);
                params.put("DoB", DoB);
                params.put("card_id", card_id);
                params.put("nat_id", nat_id);
                params.put("gender", Integer.toString(gender));
                params.put("phone", phone);
                params.put("city_id", Integer.toString(city_id));
                params.put("district_id", Integer.toString(district_id));
                params.put("address", address);
                params.put("occupation", occupation);
                params.put("vax_date", vax_date);
                params.put("priority_id", Integer.toString(priority_id));
                params.put("vax_id", vax_id);
                params.put("dose", Integer.toString(dose));
                params.put("c1", Integer.toString(c1));
                params.put("allergens", allergens);
                params.put("c2", Integer.toString(c2));
                params.put("c3", Integer.toString(c3));
                params.put("vax_list", vax_list);
                params.put("c4", Integer.toString(c4));
                params.put("c5", Integer.toString(c5));
                params.put("c6", Integer.toString(c6));
                params.put("acute", acute);
                params.put("c7", Integer.toString(c7));
                params.put("chronic", chronic);
                params.put("c8", Integer.toString(c8));
                params.put("c9", Integer.toString(c9));
                params.put("c11", Integer.toString(c11));
                params.put("c12", Integer.toString(c12));
                params.put("other_allergens", other_allergens);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void setUpHomeFragmentHealth(int id, TextView text, TextView text2, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getLastHealthFormGen.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    String date = result.getString("date");
                    text.setText("The last time you or your family declared a health form was on " + date + ".");

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        Date dateValue = format.parse(date);

                        Calendar calendar = Calendar.getInstance();
                        Date currentDate = calendar.getTime();

                        long diff = currentDate.getTime() - dateValue.getTime();
                        if (TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS) > 14)
                            text2.setText("You have not declared a health form for more than 2 weeks!");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
                else
                    text.setText("You have not declared a health form.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HOME_FRAGMENT", error.toString())) {
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

    public static void setUpHomeFragmentVax(int id, TextView text, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getLastVaxFormGen.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    String date = result.getString("vax_date");
                    text.setText("You or your family have upcoming vaccinations on " + date);
                }
                else
                    text.setText("You have not registered for vaccination.");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HOME_FRAGMENT", error.toString())) {
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

    public static void buildHealthQRCode(int id, String name, List<SlideItem> qrCodes, RecyclerView.Adapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getLastHealthFormFamilyMember.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    if (result.isNull("base_form_id"))
                        return;

                    String date = result.getString("date");
                    String card_id = result.getString("card_id");
                    String phone = result.getString("phone");
                    int sus = result.getInt("sus");
                    int nat = result.getInt("nat");
                    int sym = result.getInt("sym");

                    JSONArray symptoms = result.getJSONArray("symptoms_list");
                    ArrayList<String>symptoms_list = new ArrayList<>();
                    for(int i = 0;i < symptoms.length();i++) {
                        String json = symptoms.getString(i);
                        symptoms_list.add(json);
                    }

                    String output = "NotBlueZone Health Form: " + name + ", declared on " + date + ", ID number " + card_id
                            + ", phone number " + phone + ".";
                    if (sus > 0)
                        output = output.concat(" Has contacted with suspected patient(s).");
                    if (nat > 0)
                        output = output.concat(" Has contacted with nationals from COVID-infected countries.");
                    if (sym > 0)
                        output = output.concat(" Has contacted with those with COVID symptoms.");

                    if (symptoms_list.size() > 0) {
                        output = output.concat(" Has the following symptoms in the past 14 days: ");
                        for(String s : symptoms_list) {
                            output = output.concat(s + ",");
                        }
                    }

                    qrCodes.add(new SlideItem(output));
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HOME_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("name", name);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }

    public static void buildVaxQRCode(int id, String name, List<SlideItem> qrCodes, RecyclerView.Adapter adapter, Context context) {
        String sendUrl = "https://notbluezone.000webhostapp.com/php/getLastVaxFormFamilyMember.php";
        StringRequest request = new StringRequest(Request.Method.POST, sendUrl, response -> {
            try {
                Log.i("tagconvertstr", "["+response+"]");
                JSONObject result = new JSONObject(response);
                boolean success = result.getBoolean("success");
                if (success) {
                    if (result.isNull("base_form_id"))
                        return;

                    String vax_date = result.getString("vax_date");
                    String card_id = result.getString("card_id");
                    String phone = result.getString("phone");
                    int priority_id = result.getInt("priority_id");
                    String vax_id = result.getString("vax_id");
                    int shot = result.getInt("shot_number");

                    JSONArray complications = result.getJSONArray("complications_list");
                    ArrayList<HashMap<Integer, String>> complicationsList = new ArrayList<>();
                    for(int i = 0;i < complications.length();i++) {
                        String json = complications.getString(i);
                        JSONObject obj = new JSONObject(json);

                        HashMap<Integer, String> complication = new HashMap<>();
                        complication.put(obj.getInt("complication_id"), obj.getString("details"));
                        complicationsList.add(complication);
                    }

                    String output = "NotBlueZone Vaccination Form: " + name + ", ID number " + card_id
                            + ", phone number " + phone + ".";
                    output = output.concat("Vaccination date: " + vax_date + ", " + vax_id + " shot number " + shot + ", priority level " + priority_id + ".");
                    if (complicationsList.size() > 0) {
                        output = output.concat(" Has the following health complications:");
                        for (HashMap h : complicationsList) {
                            for (Object i : h.keySet()) {
                                output = output.concat(" " + i + "(" + h.get(i) + "),");
                            }
                        }
                    }
                    qrCodes.add(new SlideItem(output));
                    adapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Log.e("HOME_FRAGMENT", error.toString())) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", Integer.toString(id));
                params.put("name", name);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(request);
    }
}
