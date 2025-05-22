package com.vfit.servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.vfit.util.DatabaseConnection;
import com.vfit.model.User;

@WebServlet("/dashboard/*")
public class DashboardServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        
        if (action == null || action.equals("/")) {
            loadDashboardData(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String action = request.getPathInfo();
        
        switch (action) {
            case "/updateStats":
                updateDailyStats(request, response);
                break;
            case "/calculateBMI":
                calculateBMI(request, response);
                break;
            case "/logExercise":
                logExercise(request, response);
                break;
            case "/logWater":
                logWaterIntake(request, response);
                break;
            case "/logCalories":
                logCalories(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
    
    private void loadDashboardData(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get today's stats
            String sql = "SELECT * FROM daily_stats WHERE user_id = ? AND date = CURDATE()";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getUserId());
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                request.setAttribute("dailyStats", createDailyStatsFromResultSet(rs));
            } else {
                // Create new daily stats entry if none exists
                createNewDailyStats(conn, user.getUserId());
                request.setAttribute("dailyStats", createEmptyDailyStats());
            }
            
            // Get achievements
            sql = "SELECT * FROM achievements WHERE user_id = ? ORDER BY achieved_at DESC LIMIT 5";
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getUserId());
            
            rs = pstmt.executeQuery();
            List<String> achievements = new ArrayList<>();
            while (rs.next()) {
                achievements.add(rs.getString("achievement_name"));
            }
            request.setAttribute("achievements", achievements);
            
            request.getRequestDispatcher("/dashboard.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void updateDailyStats(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE daily_stats SET steps = ?, sleep_hours = ?, exercise_minutes = ?, " +
                        "calories_burned = ?, water_intake_ml = ? WHERE user_id = ? AND date = CURDATE()";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(request.getParameter("steps")));
            pstmt.setDouble(2, Double.parseDouble(request.getParameter("sleepHours")));
            pstmt.setInt(3, Integer.parseInt(request.getParameter("exerciseMinutes")));
            pstmt.setInt(4, Integer.parseInt(request.getParameter("caloriesBurned")));
            pstmt.setInt(5, Integer.parseInt(request.getParameter("waterIntake")));
            pstmt.setInt(6, user.getUserId());
            
            pstmt.executeUpdate();
            
            response.sendRedirect("dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void calculateBMI(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        double height = Double.parseDouble(request.getParameter("height")) / 100; // Convert cm to m
        double weight = Double.parseDouble(request.getParameter("weight"));
        
        double bmi = weight / (height * height);
        String category;
        
        if (bmi < 18.5) {
            category = "Underweight";
        } else if (bmi < 25) {
            category = "Normal weight";
        } else if (bmi < 30) {
            category = "Overweight";
        } else {
            category = "Obese";
        }
        
        response.setContentType("application/json");
        response.getWriter().write(String.format("{\"bmi\": %.1f, \"category\": \"%s\"}", bmi, category));
    }
    
    private void logExercise(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "INSERT INTO exercise_logs (user_id, exercise_type, intensity, duration_minutes, calories_burned) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, request.getParameter("exerciseType"));
            pstmt.setString(3, request.getParameter("intensity"));
            pstmt.setInt(4, Integer.parseInt(request.getParameter("duration")));
            pstmt.setInt(5, calculateCaloriesBurned(
                request.getParameter("exerciseType"),
                request.getParameter("intensity"),
                Integer.parseInt(request.getParameter("duration"))
            ));
            
            pstmt.executeUpdate();
            
            // Update daily stats
            updateDailyExerciseStats(conn, user.getUserId());
            
            response.sendRedirect("dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void logWaterIntake(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE daily_stats SET water_intake_ml = water_intake_ml + ? " +
                        "WHERE user_id = ? AND date = CURDATE()";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(request.getParameter("waterIntake")));
            pstmt.setInt(2, user.getUserId());
            
            pstmt.executeUpdate();
            
            response.sendRedirect("dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void logCalories(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String sql = "UPDATE daily_stats SET calories_burned = calories_burned + ? " +
                        "WHERE user_id = ? AND date = CURDATE()";
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, Integer.parseInt(request.getParameter("calories")));
            pstmt.setInt(2, user.getUserId());
            
            pstmt.executeUpdate();
            
            response.sendRedirect("dashboard");
        } catch (SQLException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    
    private void createNewDailyStats(Connection conn, int userId) throws SQLException {
        String sql = "INSERT INTO daily_stats (user_id, date) VALUES (?, CURDATE())";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.executeUpdate();
    }
    
    private DailyStats createDailyStatsFromResultSet(ResultSet rs) throws SQLException {
        DailyStats stats = new DailyStats();
        stats.setSteps(rs.getInt("steps"));
        stats.setSleepHours(rs.getDouble("sleep_hours"));
        stats.setExerciseMinutes(rs.getInt("exercise_minutes"));
        stats.setCaloriesBurned(rs.getInt("calories_burned"));
        stats.setWaterIntake(rs.getInt("water_intake_ml"));
        return stats;
    }
    
    private DailyStats createEmptyDailyStats() {
        DailyStats stats = new DailyStats();
        stats.setSteps(0);
        stats.setSleepHours(0);
        stats.setExerciseMinutes(0);
        stats.setCaloriesBurned(0);
        stats.setWaterIntake(0);
        return stats;
    }
    
    private int calculateCaloriesBurned(String exerciseType, String intensity, int duration) {
        int baseCalories;
        switch (exerciseType.toLowerCase()) {
            case "walking":
                baseCalories = 4;
                break;
            case "running":
                baseCalories = 10;
                break;
            case "cycling":
                baseCalories = 8;
                break;
            case "swimming":
                baseCalories = 7;
                break;
            default:
                baseCalories = 5;
        }
        
        double intensityMultiplier;
        switch (intensity.toLowerCase()) {
            case "low":
                intensityMultiplier = 0.8;
                break;
            case "medium":
                intensityMultiplier = 1.0;
                break;
            case "high":
                intensityMultiplier = 1.5;
                break;
            default:
                intensityMultiplier = 1.0;
        }
        
        return (int) (baseCalories * intensityMultiplier * duration);
    }
    
    private void updateDailyExerciseStats(Connection conn, int userId) throws SQLException {
        String sql = "UPDATE daily_stats SET exercise_minutes = " +
                    "(SELECT SUM(duration_minutes) FROM exercise_logs " +
                    "WHERE user_id = ? AND DATE(logged_at) = CURDATE()) " +
                    "WHERE user_id = ? AND date = CURDATE()";
        
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, userId);
        pstmt.setInt(2, userId);
        pstmt.executeUpdate();
    }
    
    // Inner class to hold daily statistics
    public static class DailyStats {
        private int steps;
        private double sleepHours;
        private int exerciseMinutes;
        private int caloriesBurned;
        private int waterIntake;
        
        // Getters and Setters
        public int getSteps() { return steps; }
        public void setSteps(int steps) { this.steps = steps; }
        
        public double getSleepHours() { return sleepHours; }
        public void setSleepHours(double sleepHours) { this.sleepHours = sleepHours; }
        
        public int getExerciseMinutes() { return exerciseMinutes; }
        public void setExerciseMinutes(int exerciseMinutes) { this.exerciseMinutes = exerciseMinutes; }
        
        public int getCaloriesBurned() { return caloriesBurned; }
        public void setCaloriesBurned(int caloriesBurned) { this.caloriesBurned = caloriesBurned; }
        
        public int getWaterIntake() { return waterIntake; }
        public void setWaterIntake(int waterIntake) { this.waterIntake = waterIntake; }
    }
} 