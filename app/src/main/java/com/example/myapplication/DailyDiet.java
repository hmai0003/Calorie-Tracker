package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.myapplication.Adapters.FoodCategoryAdapter;
import com.example.myapplication.Model.Consumptiontable;
import com.example.myapplication.Model.FoodCategory;
import com.example.myapplication.Model.ReportDetails;
import com.example.myapplication.Model.User;
import com.example.myapplication.Model.Usertable;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DailyDiet extends Activity {

    private Spinner spinnerCategory;
    private ListView lvFoods;

    private ArrayList<FoodCategory> foodCategories;
    private ArrayList<String> data;
    private FoodCategoryAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_diet);

        context = this;
        foodCategories = new ArrayList<>();
        data = new ArrayList<>();
        lvFoods = findViewById(R.id.listViewFood);
        adapter = new FoodCategoryAdapter(this, data);
        lvFoods.setAdapter(adapter);

        FloatingActionButton btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchActivity.class);
                startActivity(intent);
            }
        });

        spinnerCategory = findViewById(R.id.spinner_category);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {

                } else {
                    //API Call
                    String category = spinnerCategory.getSelectedItem().toString();
                    ApiForCategory apiForCategory = new ApiForCategory();
                    apiForCategory.execute(category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void getFoodDescription(int foodCategoryIndex) {
        Intent intent = new Intent(this, FoodDescriptionScreen.class);
        intent.putExtra("FoodName", foodCategories.get(foodCategoryIndex).getFoodname());
        startActivity(intent);
    }

    public void addConsumptionItem(int foodCategoryIndex) {


        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String user = sharedPreferences.getString("User", "");
            int userid = 0;

            JSONArray array = new JSONArray(user);
            for(int i = 0; i<array.length(); i++) {
                JSONObject jObj = array.getJSONObject(i);
                if (jObj.has("usertable")) ;
                {
                    JSONObject jsonObject = jObj.getJSONObject("usertable");
                    System.out.print(jsonObject);
                    userid = Integer.parseInt(jsonObject.getString("userid"));
                }
            }




            Usertable usertable = new Usertable();
            usertable.setUserid(userid);

            FoodCategory foodCategory = foodCategories.get(foodCategoryIndex);
            double quantity = 1;

            Consumptiontable consumptiontable = new Consumptiontable(quantity, usertable, foodCategory);
            //Execute
            PostConsumptionItem postConsumptionItem = new PostConsumptionItem();
            postConsumptionItem.execute(consumptiontable);
        }
        catch (Exception ex) {

        }
    }

    private class PostConsumptionItem extends AsyncTask<Consumptiontable, Void, Void> {

        @Override
        protected Void doInBackground(Consumptiontable... consumptiontables) {

            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
//                User user = new User(Integer.parseInt(userid));
//                ReportDetails reportDetails = new ReportDetails(Double.parseDouble(calorieConsumed),Double.parseDouble(totalCaloriesBurned),totalSteps,Double.parseDouble(goalCalorie),user);
                String response = "";
                Gson gson =new Gson();
                String postJSON=gson.toJson(consumptiontables[0]);
                url = new URL(BaseUrl + "calorietracker.consumptiontable");
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
            } catch(Exception e) {
                System.out.println("Exception: " + e.getMessage());
            }
            finally {
                connection.disconnect();
            }
            return null;

        }
    }

    private class ApiForCategory extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection conn = null;
            String textResult = "";

            try {
                url = new URL(BaseUrl + "calorietracker.foodtable/findByCategory/" + strings[0]);
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
            return textResult;


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                data.clear();
                foodCategories.clear();
                JSONArray array = new JSONArray(s);
                List<FoodCategory> aList = new ArrayList<>();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject aObj = array.getJSONObject(i);
                    FoodCategory foodCategory = new FoodCategory();
                    foodCategory.setCalorie(aObj.getDouble("calorie"));
                    foodCategory.setCategory(aObj.getString("category"));
                    foodCategory.setFat(aObj.getDouble("fat"));
                    foodCategory.setFoodid(aObj.getInt("foodid"));
                    foodCategory.setFoodname(aObj.getString("foodname"));
                    foodCategory.setServingamount(aObj.getString("servingamount"));
                    foodCategory.setServingunit(aObj.getString("servingunit"));

                    aList.add(foodCategory);
                    data.add(foodCategory.getFoodname());
                }
                foodCategories.addAll(aList);
                adapter.notifyDataSetChanged();
            }
            catch (Exception ex) {

            }
        }
    }
}

