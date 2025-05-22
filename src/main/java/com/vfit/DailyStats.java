package com.vfit;

import java.io.Serializable;

public class DailyStats implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int steps;
    private double sleepHours;
    private int exerciseMinutes;
    private int caloriesBurned;
    private int waterIntake;

    public DailyStats() {
        this.steps = 0;
        this.sleepHours = 0;
        this.exerciseMinutes = 0;
        this.caloriesBurned = 0;
        this.waterIntake = 0;
    }

    // Getters
    public int getSteps() { return steps; }
    public double getSleepHours() { return sleepHours; }
    public int getExerciseMinutes() { return exerciseMinutes; }
    public int getCaloriesBurned() { return caloriesBurned; }
    public int getWaterIntake() { return waterIntake; }

    // Methods to update stats
    public void addSteps(int steps) {
        this.steps += steps;
    }

    public void setSleepHours(double sleepHours) {
        this.sleepHours = sleepHours;
    }

    public void addExerciseMinutes(int minutes) {
        this.exerciseMinutes += minutes;
        // Calculate calories burned based on exercise
        this.caloriesBurned += minutes * 5; // Simple calculation: 5 calories per minute
    }

    public void addCaloriesBurned(int calories) {
        this.caloriesBurned += calories;
    }

    public void addWaterIntake(int ml) {
        this.waterIntake += ml;
    }

    public void reset() {
        this.steps = 0;
        this.sleepHours = 0;
        this.exerciseMinutes = 0;
        this.caloriesBurned = 0;
        this.waterIntake = 0;
    }
} 