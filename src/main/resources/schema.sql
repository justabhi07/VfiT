CREATE DATABASE IF NOT EXISTS vfit_db;
USE vfit_db;

CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    height DOUBLE,
    weight DOUBLE,
    points INT DEFAULT 0,
    fitness_level VARCHAR(20),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS daily_stats (
    stat_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    date DATE,
    steps INT DEFAULT 0,
    sleep_hours DOUBLE DEFAULT 0,
    exercise_minutes INT DEFAULT 0,
    calories_burned INT DEFAULT 0,
    water_intake_ml INT DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS achievements (
    achievement_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    achievement_name VARCHAR(100),
    points_earned INT,
    achieved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS exercise_logs (
    log_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    exercise_type VARCHAR(50),
    intensity VARCHAR(20),
    duration_minutes INT,
    calories_burned INT,
    logged_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
); 