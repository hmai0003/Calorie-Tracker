package com.example.myapplication.Model;

public class Food {
    private String name;
    private String calories;
    private String fat;

    public Food(String name, String calories, String fat) {
        this.name = name;
        this.calories = calories;
        this.fat = fat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCalories() {
        return calories;
    }

    public void setCalories(String calories) {
        this.calories = calories;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }
}
