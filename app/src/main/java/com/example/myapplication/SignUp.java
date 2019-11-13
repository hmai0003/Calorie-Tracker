package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.Model.Credentialtable;
import com.example.myapplication.Model.ReportDetails;
import com.example.myapplication.Model.User;
import com.example.myapplication.Model.Usertable;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class SignUp extends Activity {
    private TextView mBirthDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Button submitButton;
    private TextView signFirstName;
    private TextView signLastName;
    private TextView signEmail;
    private TextView signDob;
    private TextView signHeight;
    private TextView signWeight;
    private RadioButton signMale;
    private RadioButton signFemale;
    private TextView signAddress;
    private TextView signPostCode;
    private Spinner signLvlOfA;
    private TextView signSteps;
    private TextView signUser;
    private TextView signPass;
    private RadioGroup submitGender;
    private Context context;
    private boolean isSecondCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        context = this;
        isSecondCall = false;


        signFirstName = findViewById(R.id.signup_firstname);
        signLastName = findViewById(R.id.signup_surname);
        signAddress = findViewById(R.id.signnup_address);
        signDob = findViewById(R.id.signup_date);
        signEmail = findViewById(R.id.signup_email);
        signHeight = findViewById(R.id.signup_height);
        signWeight = findViewById(R.id.signup_weight);
        signMale = findViewById(R.id.radioMale);
        signFemale = findViewById(R.id.radioFemale);
        signLvlOfA = findViewById(R.id.signupActLevel);
        signPostCode = findViewById(R.id.signup_postcode);
        signUser = findViewById(R.id.signupUser);
        signPass = findViewById(R.id.signupPass);
        signSteps = findViewById(R.id.signupSteps);
        submitButton = findViewById(R.id.signUpButton);
        submitGender = findViewById(R.id.radioGender);


        List<String> list = new ArrayList<String>();
        list.add("Select Activity Level");
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
        final Spinner sActivityLevel = (Spinner) findViewById(R.id.signupActLevel);

        final ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this
                , android.R.layout.simple_spinner_item, list);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sActivityLevel.setAdapter(spinnerAdapter);

        mBirthDate = (TextView) findViewById(R.id.signup_date);

        mBirthDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(SignUp.this, android.R.style.Theme_Material_Light_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow();
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String Date = month + "/" + dayOfMonth + "/" + year;
                mBirthDate.setText(Date);
            }
        };

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkDataEntered()) {

                    CheckEmailId checkEmailId = new CheckEmailId();
                    checkEmailId.execute(signEmail.getText().toString());
                }
            }
        });
    }

    public boolean isEmail(TextView textView) {
        CharSequence email = textView.getText().toString();
        return (!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
    }

    public boolean isEmpty(TextView text) {
        CharSequence str = text.getText().toString();
        return TextUtils.isEmpty(str);
    }

    public boolean checkDataEntered() {
        boolean isCorrect = false;
        if (isEmpty(signFirstName)) {
            signFirstName.setError("First Name is Required");
        } else if (isEmpty(signLastName)) {
            signLastName.setError("Last Name is Required");
        } else if (isEmpty(signDob)) {
            signDob.setError("Date of Birth is Required");
        } else if (isEmpty(signHeight)) {
            signHeight.setError("Height is Required");
        } else if (isEmpty(signWeight)) {
            signWeight.setError("Weight is Required");
        } else if (isEmpty(signUser)) {
            signUser.setError("User Name is Required");
        } else if (isEmpty(signPass)) {
            signPass.setError("Password is Required");
        } else if (isEmpty(signPostCode)) {
            signPostCode.setError("Post code is Required");
        } else if (isEmpty(signAddress)) {
            signAddress.setError("Address is Required");
        } else if (isEmpty(signSteps)) {
            signSteps.setError("Steps data Required");
        } else if (isEmpty(signEmail)) {
            signEmail.setError("Email Id Required");
        } else if (signLvlOfA.getSelectedItemId() == 0) {
            Toast.makeText(getApplicationContext(), "Please select level ", Toast.LENGTH_LONG).show();
        } else
            isCorrect = true;

        return isCorrect;


    }

    private class CheckEmailId extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {


            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {

                url = new URL(BaseUrl + "calorietracker.usertable/findByEmailid/" + strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            System.out.print(textResult);
            return textResult;

        }

        protected void onPostExecute(String s) {
            if (s.length() > 2) {
                if (isSecondCall) {
                    if (isSecondCall) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("User", s);
                        editor.commit();

                        try {
                            //Parse User Json Object
                            int userid = 0;
                            JSONArray array = new JSONArray(s);
                            for(int i = 0; i<array.length(); i++)
                            {
                                JSONObject jObj = array.getJSONObject(i);
                                userid = jObj.getInt("userid");
                            }
                            //Credential Object - User ID, Username, Password Hash
                            Credentialtable credentialtable = new Credentialtable(userid, signUser.getText().toString(), SHA1(signPass.getText().toString()));

                            SendCredentials sendCredentials = new SendCredentials();
                            sendCredentials.execute(credentialtable);
                        }
                        catch (Exception ex) {

                        }
                    }
                }
                else
                    signEmail.setError("Email already exists");
            } else {
                    CheckUserName checkUserName = new CheckUserName();
                    checkUserName.execute(signUser.getText().toString());
            }
        }
    }

    private class SendCredentials extends AsyncTask<Credentialtable, Void, Void> {

        @Override
        protected Void doInBackground(Credentialtable... credentialtables) {
            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
                String response = "";
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();

                String postJSON = gson.toJson(credentialtables[0]);
                url = new URL(BaseUrl + "calorietracker.credentialtable/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(postJSON.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(postJSON);
                out.close();

                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response += scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Intent intent = new Intent(context, HomeScreen.class);
            startActivity(intent);
        }
    }

    private class CheckUserName extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... strings) {
            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";
            try {

                url = new URL(BaseUrl + "calorietracker.credentialtable/findByUsername/" + strings[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                conn.disconnect();
            }
            System.out.print(textResult);
            return textResult;

        }


        @Override
        protected void onPostExecute(String s) {
            if (s.length() > 2) {
                signUser.setError("Username must be unique");
            } else {
                try {
                    char gen;
                    int level = 0;
                    if (signFemale.isChecked()) {
                        gen = 'F';
                    } else {
                        gen = 'M';
                    }
                    if (signLvlOfA.getSelectedItemId() == 0) {
                        Toast.makeText(getApplicationContext(), "Please select level ", Toast.LENGTH_LONG).show();
                    } else {

                        level = Integer.parseInt(signLvlOfA.getSelectedItem().toString());
                    }


                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    Date dob = simpleDateFormat.parse(signDob.getText().toString());
                    Usertable userTable = new Usertable(signFirstName.getText().toString(), signLastName.getText().toString(), signEmail.getText().toString(), dob, Double.parseDouble(signHeight.getText().toString()), Double.parseDouble(signWeight.getText().toString()), gen, signAddress.getText().toString(), Integer.parseInt(signPostCode.getText().toString()), level, Integer.parseInt(signSteps.getText().toString()));

                    SendPost sendPost = new SendPost();
                    sendPost.execute(userTable);
                }
                catch (Exception ex) {

                }
            }
        }
    }

    private class SendPost extends AsyncTask<Usertable, Void, Void> {

        @Override
        protected Void doInBackground(Usertable... params) {
            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
                String response = "";
                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX").create();


                String postJSON = gson.toJson(params[0]);
                url = new URL(BaseUrl + "calorietracker.usertable/");
                connection = (HttpURLConnection) url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(postJSON.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                PrintWriter out = new PrintWriter(connection.getOutputStream());
                out.print(postJSON);
                out.close();

                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response += scanner.nextLine();
                }
            } catch (Exception e) {
                System.out.println("Exception: " + e.getMessage());
            } finally {
                connection.disconnect();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            isSecondCall = true;
            CheckEmailId checkEmailId = new CheckEmailId();
            checkEmailId.execute(signEmail.getText().toString());
        }
    }

    private static String SHA1(String words) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest diges = MessageDigest.getInstance("SHA-1");
        byte[] wordBytes = words.getBytes("iso-8859-1");
        diges.update(wordBytes, 0, wordBytes.length);
        byte[] sha1hash = diges.digest();
        return hexConverter(sha1hash);
    }

    private static String hexConverter(byte[] value) {
        StringBuilder builder = new StringBuilder();
        for (byte by : value) {
            int half = (by >>> 4) & 0x0F;
            int second_half = 0;

            do {
                builder.append((0 <= half) && (half <= 9) ? (char) ('0' + half) : (char) ('a' + (half - 10)));
                half = by & 0x0F;
            } while (second_half++ < 1);
        }
        return builder.toString();
    }
}



