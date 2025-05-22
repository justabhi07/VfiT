package com.vfit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private String username;
    private String email;
    private String password;
    private double height;
    private double weight;
    private int points;
    private String fitnessLevel;
    private DailyStats dailyStats;
    private List<String> achievements;

    public User(int userId, String username, String email, double height, double weight, int points, String fitnessLevel) {
        this.userId = userId;
        this.username = username;
        this.email = email;
        this.height = height;
        this.weight = weight;
        this.points = points;
        this.fitnessLevel = fitnessLevel;
        this.dailyStats = new DailyStats();
        this.achievements = new ArrayList<>();
    }

    // Getters
    public int getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public double getHeight() { return height; }
    public double getWeight() { return weight; }
    public int getPoints() { return points; }
    public String getFitnessLevel() { return fitnessLevel; }
    public DailyStats getDailyStats() { return dailyStats; }
    public List<String> getAchievements() { return achievements; }

    // Setters
    public void setPassword(String password) { this.password = password; }
    public void setHeight(double height) { this.height = height; }
    public void setWeight(double weight) { this.weight = weight; }
    public void setFitnessLevel(String fitnessLevel) { this.fitnessLevel = fitnessLevel; }

    public void addPoints(int points) {
        this.points += points;
    }

    public void addAchievement(String achievement) {
        achievements.add(achievement);
    }
} 