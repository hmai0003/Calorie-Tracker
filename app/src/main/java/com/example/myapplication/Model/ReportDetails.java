package com.example.myapplication.Model;

public class ReportDetails {

        private double calorieconsumed;
        private double calorieburned;
        private int stepstaken;
        private double caloriegoal;
        private User userid;

        public ReportDetails(double calorieConsumed, double calorieBurned, int stepsTaken, double calorieGoal, User userId) {
            this.calorieconsumed = calorieConsumed;
            this.calorieburned = calorieBurned;
            this.stepstaken = stepsTaken;
            this.caloriegoal = calorieGoal;
            this.userid = userId;
        }

        public double getCalorieConsumed() {
            return calorieconsumed;
        }

        public void setCalorieConsumed(double calorieConsumed) {
            this.calorieconsumed = calorieConsumed;
        }

        public double getCalorieBurned() {
            return calorieburned;
        }

        public void setCalorieBurned(double calorieBurned) {
            this.calorieburned = calorieBurned;
        }

        public int getStepsTaken() {
            return stepstaken;
        }

        public void setStepsTaken(int stepsTaken) {
            this.stepstaken = stepsTaken;
        }

        public double getCalorieGoal() {
            return caloriegoal;
        }

        public void setCalorieGoal(double calorieGoal) {
            this.caloriegoal = calorieGoal;
        }

        public User getUserId() {
            return userid;
        }

        public void setUserId(User userId) {
            this.userid = userId;
        }


}
