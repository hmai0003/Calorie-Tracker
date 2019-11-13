package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class PieReport extends Activity {

    private TextView mPieDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private SharedPreferences sharedPreferences;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_report);
        context = this;

        mPieDate = findViewById(R.id.pieDate);

        mPieDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(PieReport.this,android.R.style.Theme_Material_Light_Dialog_MinWidth, mDateSetListener,year,month,day);
                dialog.getWindow();
                dialog.show();
            }
        });
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String Date = month + "/" + dayOfMonth + "/" + year;
                mPieDate.setText(Date);

                String date = year + "-" + month + "-" + dayOfMonth;
                ApiForPie apiForPie = new ApiForPie();
                apiForPie.execute(date);
            }
        };




    }

    private class ApiForPie extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
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

                url = new URL( BaseUrl + "calorietracker.reporttable/calorieReport/" + userid + "/" +                  strings[0] );
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
            return textResult;


        }

        protected void onPostExecute(String aVoid) {
            try {
                JSONArray array = new JSONArray(aVoid);
                PieChart pieChart = findViewById(R.id.pieChart);
                List<PieEntry> pieEntries = new ArrayList<>();
                pieEntries.add(new PieEntry(Float.parseFloat(array.getString(0)), "Consumed"));
                pieEntries.add(new PieEntry(Float.parseFloat(array.getString(1)), "Burned"));
                pieEntries.add(new PieEntry(Float.parseFloat(array.getString(2)), "Remaining"));
                PieDataSet pieDataSet = new PieDataSet(pieEntries, "");
                pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
                pieDataSet.setValueTextSize(24);
                PieData pieData = new PieData(pieDataSet);

                pieChart.setData(pieData);
                pieChart.animateY(1200);
                pieChart.invalidate();

            }
            catch (Exception e) {

            }
            super.onPostExecute(aVoid);

        }
    }
}
