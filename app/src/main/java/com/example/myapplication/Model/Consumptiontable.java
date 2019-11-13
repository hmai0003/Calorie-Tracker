package com.example.myapplication.Model;

public class Consumptiontable {

    private double quantity;
    private Usertable userid;
    private FoodCategory foodid;

    public Consumptiontable(double quantity, Usertable userid, FoodCategory foodid) {
        this.quantity = quantity;
        this.userid = userid;
        this.foodid = foodid;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public Usertable getUserid() {
        return userid;
    }

    public void setUserid(Usertable userid) {
        this.userid = userid;
    }

    public FoodCategory getFoodid() {
        return foodid;
    }

    public void setFoodid(FoodCategory foodid) {
        this.foodid = foodid;
    }
}
