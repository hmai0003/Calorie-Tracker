package com.example.myapplication;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;

public class BarReport extends Activity {

    private Context context;
    private SharedPreferences sharedPreferences;
    private TextView startDateTv;
    private TextView endDateTv;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private DatePickerDialog.OnDateSetListener mDateSetListenerEnd;
    private String startDate;
    BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_report);
        context = this;
        startDateTv = findViewById(R.id.barDate);
        endDateTv = findViewById(R.id.barDate2);

        startDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(BarReport.this,android.R.style.Theme_Material_Light_Dialog_MinWidth, mDateSetListener,year,month,day);
                dialog.getWindow();
                dialog.show();
            }
        });

        endDateTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(BarReport.this,android.R.style.Theme_Material_Light_Dialog_MinWidth, mDateSetListenerEnd,year,month,day);
                dialog.getWindow();
                dialog.show();

            }
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String Date = month + "/" + dayOfMonth + "/" + year;
                startDateTv.setText(Date);

                startDate = year + "-" + month + "-" + dayOfMonth;
            }
        };

        mDateSetListenerEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month + 1;

                String Date = month + "/" + dayOfMonth + "/" + year;
                endDateTv.setText(Date);

                String endDate = year + "-" + month + "-" + dayOfMonth;
                BarAsync barAsync = new BarAsync();
                barAsync.execute(endDate);
            }
        };
    }


    private class BarAsync extends AsyncTask<String, Void, String> {


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
                url = new URL( BaseUrl + "calorietracker.reporttable/filterByDateRange/" + userid + "/" + startDate + "/" + strings[0]);
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

        @Override
        protected void onPostExecute(String s) {
            List<Double> calorieBurned = new ArrayList<>();
            List<Double> calorieConsumed = new ArrayList<>();
            List<String> finalDate = new ArrayList<>();
            List<BarEntry> barConsumed = new ArrayList<>();
            List<BarEntry> barBurned = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(s);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    calorieBurned.add( jsonObject.getDouble("calorieBurned"));
                    calorieConsumed.add(jsonObject.getDouble("calorieConsumed"));
                    finalDate.add(jsonObject.getString("reportDate"));
                    barConsumed.add(new BarEntry(i, Float.parseFloat(Double.toString(calorieConsumed.get(i)))));
                    barBurned.add(new BarEntry(i, Float.parseFloat(Double.toString(calorieBurned.get(i)))));
                }

                barChart = findViewById(R.id.barChart);
                BarDataSet barDataSetConsumed = new BarDataSet(barConsumed, "Consumed");
                barDataSetConsumed.setColor(ColorTemplate.MATERIAL_COLORS[0]);
                BarDataSet barDataSetBurned = new BarDataSet(barBurned, "Burned");
                barDataSetBurned.setColor(ColorTemplate.MATERIAL_COLORS[1]);
                BarData barData = new BarData(barDataSetConsumed, barDataSetBurned);
                barData.setBarWidth(0.15f);

                XAxis xAxis = barChart.getXAxis();
                xAxis.setValueFormatter(new IndexAxisValueFormatter(finalDate));
                xAxis.setCenterAxisLabels(true);
                xAxis.setGranularity(1f);
                xAxis.setGranularityEnabled(true);
                barChart.setData(barData);
                barChart.setDragEnabled(true);
                barChart.getXAxis().setAxisMinimum(0f);
                barChart.groupBars(0f, 0.05f, 0f);
                barChart.animateY(1200);
                barChart.invalidate();



            }

            catch (Exception e) {

            }

            super.onPostExecute(s);
        }

    }
}



