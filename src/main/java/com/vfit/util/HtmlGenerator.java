package com.vfit.util;

import java.util.List;

public class HtmlGenerator {
    
    public static String generateHeader(String title) {
        return new StringBuilder()
            .append("<!DOCTYPE html>")
            .append("<html>")
            .append("<head>")
            .append("<title>").append(title).append("</title>")
            .append("<link href='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css' rel='stylesheet'>")
            .append("<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css'>")
            .append("<style>")
            .append(".stat-card { border-radius: 15px; padding: 20px; margin-bottom: 20px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }")
            .append(".stat-card i { font-size: 2rem; margin-bottom: 10px; }")
            .append(".progress { height: 10px; }")
            .append(".nav-tabs .nav-link { color: #495057; }")
            .append(".nav-tabs .nav-link.active { color: #007bff; font-weight: bold; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .toString();
    }
    
    public static String generateNavbar(String username, int points) {
        return new StringBuilder()
            .append("<nav class='navbar navbar-expand-lg navbar-dark bg-primary'>")
            .append("<div class='container'>")
            .append("<a class='navbar-brand' href='#'>VfiT</a>")
            .append("<div class='d-flex align-items-center'>")
            .append("<span class='text-white me-3'><i class='fas fa-user'></i> ").append(username).append("</span>")
            .append("<span class='text-white me-3'><i class='fas fa-star'></i> ").append(points).append(" Points</span>")
            .append("<a href='logout' class='btn btn-outline-light'>Logout</a>")
            .append("</div></div></nav>")
            .toString();
    }
    
    public static String generateStatCard(String title, String icon, double value, double max) {
        return String.format(
            "<div class='col-md-3'>" +
            "<div class='stat-card bg-primary text-white'>" +
            "<i class='fas fa-%s'></i>" +
            "<h3>%s</h3>" +
            "<p class='mb-0'>%.0f / %.0f</p>" +
            "<div class='progress mt-2 bg-white'>" +
            "<div class='progress-bar' role='progressbar' style='width: %.1f%%'></div>" +
            "</div></div></div>",
            icon, title, value, max, (value/max)*100
        );
    }
    
    public static String generateFooter() {
        return new StringBuilder()
            .append("<footer class='bg-light text-center text-lg-start mt-5'>")
            .append("<div class='text-center p-3'>")
            .append("<p class='mb-0'>Don't just get fit, Get VfiT - Your wellness tracker</p>")
            .append("</div></footer>")
            .toString();
    }
    
    public static String generateScripts() {
        return new StringBuilder()
            .append("<script src='https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js'></script>")
            .append("<script src='https://code.jquery.com/jquery-3.6.0.min.js'></script>")
            .toString();
    }
    
    public static String generateAchievementsList(List<String> achievements) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='card'><div class='card-header'><h5 class='mb-0'>Rewards & Achievements</h5></div>")
            .append("<div class='card-body'><div id='achievementsList'>");
        
        if (achievements.isEmpty()) {
            html.append("<p class='text-muted'>No achievements yet. Keep working towards your goals!</p>");
        } else {
            for (String achievement : achievements) {
                html.append("<div class='alert alert-success mb-2'>")
                    .append("<i class='fas fa-trophy me-2'></i>")
                    .append(achievement)
                    .append("</div>");
            }
        }
        
        html.append("</div></div></div>");
        return html.toString();
    }
    
    public static String generateForm(String id, String action, String method, String content) {
        return String.format(
            "<form id='%s' action='%s' method='%s'>%s</form>",
            id, action, method, content
        );
    }
    
    public static String generateInput(String type, String name, String label, boolean required) {
        return String.format(
            "<div class='mb-3'>" +
            "<label class='form-label'>%s</label>" +
            "<input type='%s' class='form-control' name='%s' %s>" +
            "</div>",
            label, type, name, required ? "required" : ""
        );
    }
    
    public static String generateSelect(String name, String label, String[] options, boolean required) {
        StringBuilder html = new StringBuilder();
        html.append("<div class='mb-3'>")
            .append("<label class='form-label'>").append(label).append("</label>")
            .append("<select class='form-select' name='").append(name).append("' ")
            .append(required ? "required" : "").append(">");
        
        for (String option : options) {
            html.append("<option value='").append(option.toLowerCase()).append("'>")
                .append(option).append("</option>");
        }
        
        html.append("</select></div>");
        return html.toString();
    }
    
    public static String generateButton(String type, String text, String classes) {
        return String.format(
            "<button type='%s' class='btn %s'>%s</button>",
            type, classes, text
        );
    }
    
    public static String generateProgressBar(double value, double max, String label) {
        return String.format(
            "<div class='progress mt-3'>" +
            "<div class='progress-bar' role='progressbar' style='width: %.1f%%'></div>" +
            "</div>" +
            "<p class='text-center mt-2'>%.0f / %.0f %s</p>",
            (value/max)*100, value, max, label
        );
    }
} 