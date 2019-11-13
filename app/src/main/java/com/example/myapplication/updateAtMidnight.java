package com.example.myapplication;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.example.myapplication.Model.ReportDetails;
import com.example.myapplication.Model.User;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class updateAtMidnight {
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

    public updateAtMidnight(Context context){this.context = context;}
    public void report(){

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        goalCalorie = sharedPreferences.getString("CalorieGoal", "0");

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CalorieGoal","0");
        editor.commit();
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
            db.dailyStepsDao().deleteAll();
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
            PushAsync pushAsync = new PushAsync();
            pushAsync.execute();
        }
    }

    private class PushAsync extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
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
                User user = new User(Integer.parseInt(userid));
                ReportDetails reportDetails = new ReportDetails(Double.parseDouble(calorieConsumed),Double.parseDouble(totalCaloriesBurned),totalSteps,Double.parseDouble(goalCalorie),user);
                String response = "";
                Gson gson =new Gson();
                String postJSON=gson.toJson(reportDetails);
                url = new URL(BaseUrl + "calorietracker.reporttable");
                connection = (HttpURLConnection)url.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setFixedLengthStreamingMode(postJSON.getBytes().length);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setRequestProperty("Accept", "application/json");

                PrintWriter out= new PrintWriter(connection.getOutputStream());
                out.print(postJSON);
                out.close();

                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    response += scanner.nextLine();
                }
                calorieConsumed = response;
            } catch(Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            finally {
                connection.disconnect();
            }
            return null;
        }
    }

}




