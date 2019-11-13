package com.example.myapplication;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity
public class DailySteps {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "stepsTaken")
    public int stepsTaken;
    @ColumnInfo(name = "stepsTime")
    public String stepsTime;

    public DailySteps(int stepsTaken, String stepsTime) {
        this.stepsTaken = stepsTaken;
        this.stepsTime = stepsTime;
    }

    public int getId() {
        return id;
    }

    public int getStepsTaken() {
        return stepsTaken;
    }

    public void setStepsTaken(int stepsTaken) {
        this.stepsTaken = stepsTaken;
    }

    public String getStepsTime() {
        return stepsTime;
    }

    public void setStepsTime(String stepsTime) {
        this.stepsTime = stepsTime;
    }
}
