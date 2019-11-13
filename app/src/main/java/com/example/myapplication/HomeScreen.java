package com.example.myapplication;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView lblWelcome;
    private TextView lblCalorieGoal;
    private TextView lblCurrentDate;
    private TextView lblCurrentTime;
    private EditText txtCalorieGoal;
    private Button btnAddGoal;
    private FloatingActionButton floatingActionButton1;

    DailyStepsDatabase db;

    Thread timeThread;
    SharedPreferences sharedPreferences;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = this;

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        lblWelcome = findViewById(R.id.lblWelcome);
        lblCalorieGoal = findViewById(R.id.lblCalorieGoal);
        lblCurrentDate = findViewById(R.id.lblCurrentDate);
        lblCurrentTime = findViewById(R.id.lblCurrentTime);
        txtCalorieGoal = findViewById(R.id.txtCalorieGoal);
        btnAddGoal = findViewById(R.id.btnAddCalorieGoal);
        floatingActionButton1 = findViewById(R.id.floatingActionButton);
        String goalValue = sharedPreferences.getString("CalorieGoal", "0");
        lblCalorieGoal.setText("Calorie Goal: " + goalValue);

        btnAddGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int goal = Integer.parseInt(txtCalorieGoal.getText().toString());
                    if ((goal > 0) && (goal < 10000)) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("CalorieGoal", txtCalorieGoal.getText().toString());
                        editor.commit();

                        lblCalorieGoal.setText("Calorie Goal: " + goal);
                    }
                    else
                        showAlert("Oops!", "Please, Enter valid Calorie Goal");
                }
                catch (Exception ex) {
                    showAlert("Oops!", "Please, Enter valid Calorie Goal");
                }

                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(txtCalorieGoal.getWindowToken(), 0);
                txtCalorieGoal.setText("");
            }
        });
        try {

            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String userInfo = sharedPreferences.getString("User", "");

            JSONArray array = new JSONArray(userInfo);
            for(int i = 0; i<array.length(); i++)
            {
                JSONObject jObj = array.getJSONObject(i);
                if(jObj.has("usertable"));
                {
                    JSONObject jsonObject = jObj.getJSONObject("usertable");
                    System.out.print(jsonObject);
                    String homeFirstName = jsonObject.getString("firstname");
                    lblWelcome.setText("Welcome " + homeFirstName);
                }
            }
        }
        catch (Exception e)
        {
            try {
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                String userInfo = sharedPreferences.getString("User", "");

                JSONArray array = new JSONArray(userInfo);
                for(int i = 0; i<array.length(); i++)
                {
                    JSONObject jObj = array.getJSONObject(i);
                    String homeFirstName = jObj.getString("firstname");
                    lblWelcome.setText("Welcome " + homeFirstName);
                }
            }
            catch (Exception ex) {
                lblWelcome.setText("Welcome User");
            }
        }

        timeThread = new Thread() {
            @Override
            public void run() {
                while (!isInterrupted()) {
                    try {
                        Thread.sleep(1000);
                        ((HomeScreen)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                lblCurrentDate.setText(simpleDateFormat.format(new Date()));

                                simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
                                lblCurrentTime.setText(simpleDateFormat.format(new Date()));
                            }
                        });
                    }
                    catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                }
            }
        };
        timeThread.start();

    floatingActionButton1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            updateAtMidnight update = new updateAtMidnight(context);
            update.report();
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("User");
            editor.commit();
            db = Room.databaseBuilder(context,
                    DailyStepsDatabase.class, "dailysteps_database")
                    .fallbackToDestructiveMigration()
                    .build();
            DeleteSQ deleteSQ = new DeleteSQ();
            deleteSQ.execute();

            Intent intent = new Intent(context,MainActivity.class);
            startActivity(intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class DeleteSQ extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            db.dailyStepsDao().deleteAll();
            return null;
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(context,DailyDiet.class);
            startActivity(intent);
            //My daily diet screen

        } else if (id == R.id.nav_gallery) {
            Intent intent = new Intent(context,StepsScreen.class);
            startActivity(intent);


        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(context,CalorieTrackerScreen.class);
            startActivity(intent);

        } else if (id == R.id.nav_manage) {
            Intent intent = new Intent(context,PieReport.class);
            startActivity(intent);


        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(context,MapsActivity.class);
            startActivity(intent);

        } else if (id == R.id.bar_chart) {
            Intent intent = new Intent(context,BarReport.class);
            startActivity(intent);
        }



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
