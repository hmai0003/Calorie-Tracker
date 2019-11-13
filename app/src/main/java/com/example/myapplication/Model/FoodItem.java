package com.example.myapplication.Model;

public class FoodItem  {
    private String foodName;
    private String foodId;

    public FoodItem(String foodId, String foodName) {
        this.foodId = foodId;
        this.foodName = foodName;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getFoodId() {
        return foodId;
    }

    public void setFoodName(String foodName) {
        this.foodName = foodName;
    }

    public void setFoodId(String foodId) {
        this.foodId = foodId;
    }

}
