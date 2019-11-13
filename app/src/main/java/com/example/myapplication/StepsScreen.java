package com.example.myapplication;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class StepsScreen extends AppCompatActivity {

    DailySteps eachDS;
    List<DailySteps> dataList;
    ArrayList<String> data;
    ArrayAdapter<String> adapter;
    ListView lv;

    DailyStepsDatabase db = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steps_screen);

        db = Room.databaseBuilder(getApplicationContext(),
                DailyStepsDatabase.class, "dailysteps_database")
                .fallbackToDestructiveMigration()
                .build();

        eachDS = null;
        final TextView stepsTv = (TextView) findViewById(R.id.steps_taken) ;
        Button submitButton = (Button) findViewById(R.id.submit_button);

        data = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, data);
        lv = findViewById(R.id.lvSteps);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                eachDS = dataList.get(position);
                stepsTv.setText(Integer.toString(eachDS.stepsTaken));
            }
        });

        ReadDatabase rdb = new ReadDatabase();
        rdb.execute();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try
                {
                    int steps = Integer.parseInt(stepsTv.getText().toString());
                    if(steps < 1 || steps >= 100000)
                        showAlert("Invalid Value", "Please enter a value between 1 and 100000");
                    else
                    {
                        stepsTv.setText("");
                        if (eachDS == null) {
                            InsertDatabase idb = new InsertDatabase();
                            idb.execute(steps);
                        }
                        else {
                            //Update
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                            String timeStamp = simpleDateFormat.format(new Date());

                            eachDS.setStepsTaken(steps);
                            eachDS.setStepsTime(timeStamp);
                            UpdateDatabase udb = new UpdateDatabase();
                            udb.execute(eachDS);

                            eachDS = null;
                        }
                    }

                }
                catch (Exception e)
                {
                    showAlert("Invalid Value", "Invalid Value for steps taken. Please enter a numerical value");
                }


            }
        });

    }

    private void showAlert(String title, String message) {
        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "DISMISS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }


    private class InsertDatabase extends AsyncTask<Integer, Void, Void>{
        @Override
        protected Void doInBackground(Integer... params) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
            String timeStamp = simpleDateFormat.format(new Date());
            DailySteps dailySteps = new DailySteps(params[0],timeStamp);
            long id = db.dailyStepsDao().insert(dailySteps);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ReadDatabase rdb = new ReadDatabase();
            rdb.execute();
        }
    }

    private class ReadDatabase extends AsyncTask<Void, Void, List<DailySteps>>{
        @Override
        protected List<DailySteps> doInBackground(Void... voids) {
            List<DailySteps> stepsList = db.dailyStepsDao().getAll();
            return  stepsList;
        }

        @Override
        protected void onPostExecute(List<DailySteps> dailySteps) {
            dataList = dailySteps;
            data.clear();
            for (DailySteps each : dailySteps) {
                String demo = "Steps: " + each.stepsTaken + "\n" + "Time: "  + each.stepsTime;
                data.add(demo);
            }

            adapter.notifyDataSetChanged();
        }
    }

    private class UpdateDatabase extends AsyncTask<DailySteps, Void, Void> {

        @Override
        protected Void doInBackground(DailySteps... dailySteps) {
            db.dailyStepsDao().updateUsers(dailySteps[0]);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ReadDatabase rdb = new ReadDatabase();
            rdb.execute();
        }
    }

}
