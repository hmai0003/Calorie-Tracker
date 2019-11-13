package com.example.myapplication;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class CalorieTrackerScreen extends Activity {

    private String goalCalorie;
    private int totalSteps;
    private String calorieConsumed;
    private double calorieBurned;
    private String restCalorie;
    private double totalCaloriesFromSteps;
    private String totalCaloriesBurned;

    private SharedPreferences sharedPreferences;
    private Context context;

    DailyStepsDatabase db = null;
    TextView goalTextview;
    TextView consumed;
    TextView burned;
    TextView dailySteps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calorie_tracker_screen);

        context = this;

        goalTextview = findViewById(R.id.calorieGoalTv);
        consumed = findViewById(R.id.totalCalConsumedTv);
        burned = findViewById(R.id.totalCalBurnedTv);
        dailySteps = findViewById(R.id.totalStepsTakenTv);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        goalCalorie = sharedPreferences.getString("CalorieGoal", "0");
        goalTextview.setText(goalCalorie);

        ReadDB readDB = new ReadDB();
        readDB.execute();
    }

    private class ReadDB extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            int dailySteps = 0;
            db = Room.databaseBuilder(context,
                    DailyStepsDatabase.class, "dailysteps_database")
                    .fallbackToDestructiveMigration()
                    .build();
            List<DailySteps> stepsList = db.dailyStepsDao().getAll();
            for(DailySteps each: stepsList )
            {
                dailySteps += each.getStepsTaken();
            }
            totalSteps = dailySteps;
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            GetConsumption getConsumption =new GetConsumption();
            getConsumption.execute();
        }
    }

    private class GetConsumption extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {


            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try{

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userid = "";

                JSONArray array = new JSONArray(userInfo);
                for(int i = 0; i<array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("usertable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("usertable");
                        System.out.print(jsonObject);
                        userid = jsonObject.getString("userid");
                    }
                }

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String x =(simpleDateFormat.format(new Date()));

                url = new URL( BaseUrl + "calorietracker.consumptiontable/totalCaloriesConsumed/" + userid + "/" + x );
                conn = (HttpURLConnection)url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            calorieConsumed = textResult;
            return textResult;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            Burned burn = new Burned();
            burn.execute();
        }

    }

    private class Burned extends AsyncTask<Void, Void, String>{


        @Override
        protected String doInBackground(Void... voids) {

            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userid = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("usertable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("usertable");
                        System.out.print(jsonObject);
                        userid = jsonObject.getString("userid");
                    }
                }

                url = new URL(BaseUrl + "calorietracker.usertable/findCalorieperstep/" + userid );
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                    System.out.println(textResult);
                }
                try {
                    totalCaloriesFromSteps = Double.parseDouble(textResult)* totalSteps;

                }
                catch (Exception e){
                    e.getMessage();
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            return textResult;
        }

        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            RestCalories restCalories = new RestCalories();
            restCalories.execute();
        }

    }

    private class RestCalories extends AsyncTask<Void, Void, String>{

        @Override
        protected String doInBackground(Void... voids) {
            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {

                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                String userInfo = sharedPreferences.getString("User", "");
                String userid = "";

                JSONArray array = new JSONArray(userInfo);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject jObj = array.getJSONObject(i);
                    if (jObj.has("usertable")) ;
                    {
                        JSONObject jsonObject = jObj.getJSONObject("usertable");
                        System.out.print(jsonObject);
                        userid = jsonObject.getString("userid");
                    }
                }

                url = new URL(BaseUrl + "calorietracker.usertable/findLevelOfActivity/" + userid );
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner inStream = new Scanner(conn.getInputStream());
                while (inStream.hasNextLine()) {
                    textResult += inStream.nextLine();
                    System.out.println(textResult);
                }
                try {
                    double totalCaloriesFromRest = Double.parseDouble(textResult);
                    totalCaloriesBurned = Double.toString(totalCaloriesFromRest + totalCaloriesFromSteps);

                }
                catch (Exception e){
                    e.getMessage();
                }
            }

            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                conn.disconnect();
            }
            return totalCaloriesBurned;
        }
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            consumed.setText(calorieConsumed);
            burned.setText(totalCaloriesBurned);
            dailySteps.setText(Integer.toString(totalSteps));
        }
    }

}
