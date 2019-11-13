package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.myapplication.Adapters.FoodSearchAdapter;
import com.example.myapplication.Model.Food;
import com.example.myapplication.Model.FoodCategory;
import com.example.myapplication.Model.FoodItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class SearchActivity extends Activity {

    private TextView searchText;
    private Button searchBtn;
    private ListView lvFoods;

    private ArrayList<FoodItem> foodItems;
    private ArrayList<String> data;
    private FoodSearchAdapter adapter;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        context = this;
        foodItems = new ArrayList<>();
        data = new ArrayList<>();
        lvFoods = findViewById(R.id.lvSearch);
        adapter = new FoodSearchAdapter(this, data);
        lvFoods.setAdapter(adapter);

        searchText = findViewById(R.id.etFoodName);
        searchBtn = findViewById(R.id.btnSearch);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (searchText.getText().length() == 0)
                {
                    searchText.setError("Please enter some value");
                }
                else
                {
                    APICallSearch apiCallSearch = new APICallSearch();
                    apiCallSearch.execute(searchText.getText().toString());
                }
            }
        });

    }

    public void getFoodDescription(int index) {
        Intent intent = new Intent(this, FoodDescriptionScreen.class);
        intent.putExtra("FoodName", foodItems.get(index).getFoodName());
        startActivity(intent);
    }

    public void getFoodDetails(int index) {
        FoodItem foodItem = foodItems.get(index);

        APICall apiCall = new APICall();
        apiCall.execute(foodItem.getFoodId());
    }

    private class APICall extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection conn = null;
            //HttpURLConnection connection = null;

            try {
                URL apiUrl = new URL("https://api.nal.usda.gov/ndb/V2/reports?ndbno=" + strings[0] + "&type=f&format=json&api_key=r11vc2Cq2KINFYhOn51aoI4YdG9OPTOejNxUOCJ3");
                conn = (HttpURLConnection)apiUrl.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Accept", "application/json");
                Scanner scanner = new Scanner(conn.getInputStream());

                while (scanner.hasNextLine()) {
                    result += scanner.nextLine();
                }
            }
            catch (Exception e) {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.print(s);

            try {
                JSONObject obj = new JSONObject(s);
                JSONArray array = obj.getJSONArray("foods");
                System.out.print(array);

                for (int index = 0; index < array.length(); index++) {
                    JSONObject aObj = array.getJSONObject(index);
                    System.out.print(aObj);

                    if (aObj.has("food")) {
                        JSONObject foodObj = aObj.getJSONObject("food");
                        System.out.print(foodObj);

                        JSONObject descObj = foodObj.getJSONObject("desc");
                        JSONArray nutArray = foodObj.getJSONArray("nutrients");

                        String foodName = descObj.getString("name");
                        String category = descObj.getString("fg");

                        String calories = "";
                        String fat = "";

                        for (int aIndex = 0; aIndex < nutArray.length(); aIndex++) {
                            JSONObject nutObj = nutArray.getJSONObject(aIndex);
                            if (Integer.parseInt(nutObj.getString("nutrient_id")) == 208) {
                                calories = nutObj.getString("value");

                            }

                            if (Integer.parseInt(nutObj.getString("nutrient_id")) == 205) {
                                fat = nutObj.getString("value");
                            }
                        }
                        Food food = new Food(foodName,calories,fat);
                        System.out.print(calories);
                        System.out.print(fat);

                        FoodCategory foodCategory = new FoodCategory();
                        foodCategory.setFoodname(food.getName());
                        foodCategory.setCategory("Other");
                        foodCategory.setCalorie(Double.parseDouble(food.getCalories()));
                        foodCategory.setServingunit("gram");
                        foodCategory.setServingamount("100");
                        foodCategory.setFat(Double.parseDouble(food.getFat()));

                        PostFoodItem postFoodItem = new PostFoodItem();
                        postFoodItem.execute(foodCategory);

                        break;
                    }
                }
            }
            catch (Exception e) {

            }
        }
    }

    private class PostFoodItem extends AsyncTask<FoodCategory, Void, Void> {

        @Override
        protected Void doInBackground(FoodCategory... foodCategories) {
            String BaseUrl = "http://192.168.1.109:35027/Assignment1/webresources/";
            URL url = null;
            HttpURLConnection connection = null;
            try {
                String response = "";
                Gson gson =new Gson();
                String postJSON=gson.toJson(foodCategories[0]);
                url = new URL(BaseUrl + "calorietracker.foodtable");
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

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class APICallSearch extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            HttpURLConnection connection = null;
            try {
                URL apUrl = new URL("https://api.nal.usda.gov/ndb/search/?format=json&q=" + strings[0] + "&ds=Standard%20Reference&api_key=r11vc2Cq2KINFYhOn51aoI4YdG9OPTOejNxUOCJ3");
                connection = (HttpURLConnection) apUrl.openConnection();
                connection.setReadTimeout(10000);
                connection.setConnectTimeout(15000);
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                Scanner scan = new Scanner(connection.getInputStream());
                while (scan.hasNextLine()) {
                    result += scan.nextLine();
                }
            } catch (Exception e) {

            }

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            System.out.print(s);

            try {
                data.clear();
                foodItems.clear();
                ArrayList<FoodItem> afoodItems = new ArrayList<>();
                JSONObject obj = new JSONObject(s);
                JSONObject listobj = obj.getJSONObject("list");

                JSONArray array = listobj.getJSONArray("item");
                for (int index = 0; index < array.length(); index++) {
                    JSONObject aObj = array.getJSONObject(index);
                    FoodItem foodItem = new FoodItem(aObj.getString("ndbno"), aObj.getString("name"));
                    afoodItems.add(foodItem);

                    data.add(foodItem.getFoodName());
                }
                foodItems.addAll(afoodItems);
                adapter.notifyDataSetChanged();

            } catch (Exception e) {

            }
        }
    }
}
