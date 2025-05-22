package com.vfit.servlet;

import com.vfit.util.DatabaseConnection;
import com.vfit.util.HtmlGenerator;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/dashboard/view")
public class DashboardViewServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect("login");
            return;
        }
        
        int userId = (int) session.getAttribute("userId");
        String username = (String) session.getAttribute("username");
        int points = (int) session.getAttribute("points");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get daily stats
            DailyStats stats = getDailyStats(conn, userId);
            
            // Get achievements
            List<String> achievements = getAchievements(conn, userId);
            
            // Generate HTML response
            StringBuilder html = new StringBuilder();
            
            // Add header
            html.append(HtmlGenerator.generateHeader("VfiT Dashboard"));
            
            // Add navbar
            html.append(HtmlGenerator.generateNavbar(username, points));
            
            // Add main container
            html.append("<div class='container mt-4'>");
            
            // Add stat cards
            html.append("<div class='row'>");
            html.append(HtmlGenerator.generateStatCard("Steps", "walking", stats.steps, 10000));
            html.append(HtmlGenerator.generateStatCard("Sleep", "moon", stats.sleepHours, 8));
            html.append(HtmlGenerator.generateStatCard("Exercise", "dumbbell", stats.exerciseMinutes, 60));
            html.append(HtmlGenerator.generateStatCard("Calories", "fire", stats.caloriesBurned, 2000));
            html.append("</div>");
            
            // Add health tools section
            html.append("<div class='row mt-4'>");
            html.append("<div class='col-md-6'>");
            html.append("<div class='card'>");
            html.append("<div class='card-header'><h5 class='mb-0'>Health Tools</h5></div>");
            html.append("<div class='card-body'>");
            
            // BMI Calculator
            html.append("<ul class='nav nav-tabs' id='healthToolsTab' role='tablist'>");
            html.append("<li class='nav-item'><a class='nav-link active' data-bs-toggle='tab' href='#bmi'>BMI Calculator</a></li>");
            html.append("<li class='nav-item'><a class='nav-link' data-bs-toggle='tab' href='#diet'>Diet Recommendations</a></li>");
            html.append("</ul>");
            
            html.append("<div class='tab-content mt-3'>");
            html.append("<div class='tab-pane fade show active' id='bmi'>");
            html.append(HtmlGenerator.generateForm("bmiForm", "dashboard/calculateBMI", "POST",
                HtmlGenerator.generateInput("number", "height", "Height (cm)", true) +
                HtmlGenerator.generateInput("number", "weight", "Weight (kg)", true) +
                HtmlGenerator.generateButton("submit", "Calculate BMI", "btn-primary")
            ));
            html.append("<div id='bmiResult' class='mt-3'></div>");
            html.append("</div>");
            
            html.append("<div class='tab-pane fade' id='diet'>");
            html.append("<h6>Recommended Daily Intake</h6>");
            html.append("<ul class='list-group'>");
            html.append("<li class='list-group-item'>Calories: 2000-2500 kcal</li>");
            html.append("<li class='list-group-item'>Protein: 50-60g</li>");
            html.append("<li class='list-group-item'>Water: 2-3 liters</li>");
            html.append("</ul>");
            html.append("</div>");
            html.append("</div>");
            html.append("</div></div></div>");
            
            // Add trackers
            html.append("<div class='col-md-6'>");
            html.append("<div class='card'>");
            html.append("<div class='card-header'><h5 class='mb-0'>Daily Trackers</h5></div>");
            html.append("<div class='card-body'>");
            
            // Sleep Tracker
            html.append(HtmlGenerator.generateForm("sleepForm", "dashboard/updateStats", "POST",
                HtmlGenerator.generateInput("number", "sleepHours", "Sleep Hours", true) +
                HtmlGenerator.generateButton("submit", "Log Sleep", "btn-primary")
            ));
            
            // Steps Tracker
            html.append(HtmlGenerator.generateForm("stepsForm", "dashboard/updateStats", "POST",
                HtmlGenerator.generateInput("number", "steps", "Steps", true) +
                HtmlGenerator.generateButton("submit", "Log Steps", "btn-primary")
            ));
            
            // Exercise Tracker
            html.append(HtmlGenerator.generateForm("exerciseForm", "dashboard/logExercise", "POST",
                HtmlGenerator.generateSelect("exerciseType", "Exercise Type", 
                    new String[]{"Walking", "Running", "Cycling", "Swimming", "Weight Training"}, true) +
                HtmlGenerator.generateSelect("intensity", "Intensity", 
                    new String[]{"Low", "Medium", "High"}, true) +
                HtmlGenerator.generateInput("number", "duration", "Duration (minutes)", true) +
                HtmlGenerator.generateButton("submit", "Log Exercise", "btn-primary")
            ));
            
            html.append("</div></div></div>");
            html.append("</div>");
            
            // Add intake monitoring
            html.append("<div class='row mt-4'>");
            html.append("<div class='col-md-6'>");
            html.append("<div class='card'>");
            html.append("<div class='card-header'><h5 class='mb-0'>Water Intake</h5></div>");
            html.append("<div class='card-body'>");
            html.append(HtmlGenerator.generateForm("waterForm", "dashboard/logWater", "POST",
                HtmlGenerator.generateInput("number", "waterIntake", "Water Intake (ml)", true) +
                HtmlGenerator.generateButton("submit", "Log Water", "btn-primary")
            ));
            html.append(HtmlGenerator.generateProgressBar(stats.waterIntake, 2000, "ml"));
            html.append("</div></div></div>");
            
            html.append("<div class='col-md-6'>");
            html.append("<div class='card'>");
            html.append("<div class='card-header'><h5 class='mb-0'>Calorie Intake</h5></div>");
            html.append("<div class='card-body'>");
            html.append(HtmlGenerator.generateForm("calorieForm", "dashboard/logCalories", "POST",
                HtmlGenerator.generateInput("number", "calories", "Calories", true) +
                HtmlGenerator.generateButton("submit", "Log Calories", "btn-primary")
            ));
            html.append(HtmlGenerator.generateProgressBar(stats.caloriesBurned, 2000, "kcal"));
            html.append("</div></div></div>");
            html.append("</div>");
            
            // Add achievements
            html.append("<div class='row mt-4'>");
            html.append("<div class='col-12'>");
            html.append(HtmlGenerator.generateAchievementsList(achievements));
            html.append("</div></div>");
            
            html.append("</div>"); // Close container
            
            // Add footer
            html.append(HtmlGenerator.generateFooter());
            
            // Add scripts
            html.append(HtmlGenerator.generateScripts());
            
            // Add custom JavaScript
            html.append("<script>")
                .append("$(document).ready(function() {")
                .append("$('#bmiForm').on('submit', function(e) {")
                .append("e.preventDefault();")
                .append("$.post('dashboard/calculateBMI', $(this).serialize(), function(data) {")
                .append("$('#bmiResult').html(data);")
                .append("});")
                .append("});")
                .append("$('#sleepForm, #stepsForm').on('submit', function(e) {")
                .append("e.preventDefault();")
                .append("$.post('dashboard/updateStats', $(this).serialize(), function() {")
                .append("location.reload();")
                .append("});")
                .append("});")
                .append("$('#exerciseForm').on('submit', function(e) {")
                .append("e.preventDefault();")
                .append("$.post('dashboard/logExercise', $(this).serialize(), function() {")
                .append("location.reload();")
                .append("});")
                .append("});")
                .append("$('#waterForm').on('submit', function(e) {")
                .append("e.preventDefault();")
                .append("$.post('dashboard/logWater', $(this).serialize(), function() {")
                .append("location.reload();")
                .append("});")
                .append("});")
                .append("$('#calorieForm').on('submit', function(e) {")
                .append("e.preventDefault();")
                .append("$.post('dashboard/logCalories', $(this).serialize(), function() {")
                .append("location.reload();")
                .append("});")
                .append("});")
                .append("});")
                .append("</script>");
            
            html.append("</body></html>");
            
            response.setContentType("text/html");
            response.getWriter().write(html.toString());
            
        } catch (SQLException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Database error: " + e.getMessage());
        }
    }
    
    private DailyStats getDailyStats(Connection conn, int userId) throws SQLException {
        String sql = "SELECT * FROM daily_stats WHERE user_id = ? AND DATE(created_at) = CURDATE()";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new DailyStats(
                        rs.getInt("steps"),
                        rs.getDouble("sleep_hours"),
                        rs.getInt("exercise_minutes"),
                        rs.getInt("calories_burned"),
                        rs.getInt("water_intake")
                    );
                }
            }
        }
        
        // Create new daily stats if none exist
        return createNewDailyStats(conn, userId);
    }
    
    private DailyStats createNewDailyStats(Connection conn, int userId) throws SQLException {
        String sql = "INSERT INTO daily_stats (user_id, steps, sleep_hours, exercise_minutes, calories_burned, water_intake) VALUES (?, 0, 0, 0, 0, 0)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
        return new DailyStats(0, 0, 0, 0, 0);
    }
    
    private List<String> getAchievements(Connection conn, int userId) throws SQLException {
        List<String> achievements = new ArrayList<>();
        String sql = "SELECT achievement_name FROM achievements WHERE user_id = ? ORDER BY created_at DESC";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    achievements.add(rs.getString("achievement_name"));
                }
            }
        }
        return achievements;
    }
    
    private static class DailyStats {
        final int steps;
        final double sleepHours;
        final int exerciseMinutes;
        final int caloriesBurned;
        final int waterIntake;
        
        DailyStats(int steps, double sleepHours, int exerciseMinutes, int caloriesBurned, int waterIntake) {
            this.steps = steps;
            this.sleepHours = sleepHours;
            this.exerciseMinutes = exerciseMinutes;
            this.caloriesBurned = caloriesBurned;
            this.waterIntake = waterIntake;
        }
    }
}
