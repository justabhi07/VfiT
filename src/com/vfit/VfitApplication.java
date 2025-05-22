package com.vfit;

import com.vfit.util.DataStorage;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.List;

public class VfitApplication {
    private static User currentUser;
    private static Scanner scanner = new Scanner(System.in);
    private static int nextUserId = 1;

    public static void main(String[] args) {
        System.out.println("Welcome to VfiT - Your Health Promoter!");
        
        // Load existing users and set nextUserId
        List<User> users = DataStorage.loadUsers();
        for (User user : users) {
            if (user.getUserId() >= nextUserId) {
                nextUserId = user.getUserId() + 1;
            }
        }
        
        while (true) {
            try {
                if (currentUser == null) {
                    showLoginMenu();
                } else {
                    showMainMenu();
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                scanner.nextLine(); // Clear any remaining input
            }
        }
    }

    private static void showLoginMenu() {
        System.out.println("\n=== Login Menu ===");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");

        int choice = getValidIntInput(1, 3);
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Thank you for using VfiT!");
                System.exit(0);
                break;
        }
    }

    private static void showMainMenu() {
        System.out.println("\n=== VfiT Dashboard ===");
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("Points: " + currentUser.getPoints());
        System.out.println("\n1. View Daily Stats");
        System.out.println("2. Log Exercise");
        System.out.println("3. Log Water Intake");
        System.out.println("4. Log Calories");
        System.out.println("5. Calculate BMI");
        System.out.println("6. View Achievements");
        System.out.println("7. Log Steps");
        System.out.println("8. Log Sleep");
        System.out.println("9. Logout");
        System.out.print("Choose an option: ");

        int choice = getValidIntInput(1, 9);
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                viewDailyStats();
                break;
            case 2:
                logExercise();
                break;
            case 3:
                logWaterIntake();
                break;
            case 4:
                logCalories();
                break;
            case 5:
                calculateBMI();
                break;
            case 6:
                viewAchievements();
                break;
            case 7:
                logSteps();
                break;
            case 8:
                logSleep();
                break;
            case 9:
                // Save user data before logout
                DataStorage.saveUser(currentUser);
                currentUser = null;
                break;
        }
    }

    private static void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty()) {
            System.out.println("Username cannot be empty.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty()) {
            System.out.println("Password cannot be empty.");
            return;
        }

        // Try to find existing user
        User user = DataStorage.findUser(username);
        if (user != null && user.getPassword().equals(password)) {
            currentUser = user;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid username or password.");
        }
    }

    private static void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        if (username.isEmpty() || username.length() < 3) {
            System.out.println("Username must be at least 3 characters long.");
            return;
        }

        // Check if username already exists
        if (DataStorage.findUser(username) != null) {
            System.out.println("Username already exists.");
            return;
        }

        System.out.print("Enter email: ");
        String email = scanner.nextLine().trim();
        if (!isValidEmail(email)) {
            System.out.println("Invalid email format.");
            return;
        }

        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        if (password.isEmpty() || password.length() < 6) {
            System.out.println("Password must be at least 6 characters long.");
            return;
        }

        System.out.print("Enter height (cm): ");
        double height = getValidDoubleInput(50, 250);
        if (height <= 0) {
            System.out.println("Height must be positive.");
            return;
        }

        System.out.print("Enter weight (kg): ");
        double weight = getValidDoubleInput(20, 300);
        if (weight <= 0) {
            System.out.println("Weight must be positive.");
            return;
        }

        currentUser = new User(nextUserId++, username, email, height, weight, 0, "Beginner");
        currentUser.setPassword(password);
        DataStorage.saveUser(currentUser);
        System.out.println("Registration successful!");
    }

    private static void viewDailyStats() {
        System.out.println("\n=== Daily Stats ===");
        System.out.println("Steps: " + currentUser.getDailyStats().getSteps() + " / 10000");
        System.out.println("Sleep: " + currentUser.getDailyStats().getSleepHours() + " / 8 hours");
        System.out.println("Exercise: " + currentUser.getDailyStats().getExerciseMinutes() + " / 60 minutes");
        System.out.println("Calories Burned: " + currentUser.getDailyStats().getCaloriesBurned() + " / 2000 kcal");
        System.out.println("Water Intake: " + currentUser.getDailyStats().getWaterIntake() + " / 2000 ml");
    }

    private static void logExercise() {
        System.out.println("\n=== Log Exercise ===");
        System.out.println("Exercise Types:");
        System.out.println("1. Walking");
        System.out.println("2. Running");
        System.out.println("3. Cycling");
        System.out.println("4. Swimming");
        System.out.println("5. Weight Training");
        System.out.print("Choose exercise type: ");
        int type = getValidIntInput(1, 5);
        
        System.out.print("Enter duration (minutes): ");
        int duration = getValidIntInput(1, 480); // Max 8 hours
        
        System.out.println("Exercise logged successfully!");
        currentUser.getDailyStats().addExerciseMinutes(duration);
    }

    private static void logWaterIntake() {
        System.out.print("\nEnter water intake (ml): ");
        int amount = getValidIntInput(0, 5000); // Max 5 liters
        currentUser.getDailyStats().addWaterIntake(amount);
        System.out.println("Water intake logged successfully!");
    }

    private static void logCalories() {
        System.out.print("\nEnter calories consumed: ");
        int calories = getValidIntInput(0, 10000); // Max 10000 calories
        currentUser.getDailyStats().addCaloriesBurned(calories);
        System.out.println("Calories logged successfully!");
    }

    private static void calculateBMI() {
        double heightInMeters = currentUser.getHeight() / 100;
        double bmi = currentUser.getWeight() / (heightInMeters * heightInMeters);
        
        System.out.println("\n=== BMI Result ===");
        System.out.printf("Your BMI: %.1f\n", bmi);
        
        String category;
        if (bmi < 18.5) category = "Underweight";
        else if (bmi < 25) category = "Normal weight";
        else if (bmi < 30) category = "Overweight";
        else category = "Obese";
        
        System.out.println("Category: " + category);
    }

    private static void viewAchievements() {
        System.out.println("\n=== Achievements ===");
        if (currentUser.getAchievements().isEmpty()) {
            System.out.println("No achievements yet. Keep working towards your goals!");
        } else {
            for (String achievement : currentUser.getAchievements()) {
                System.out.println("🏆 " + achievement);
            }
        }
    }

    private static void logSteps() {
        System.out.print("\nEnter number of steps: ");
        int steps = getValidIntInput(0, 50000); // Max 50,000 steps
        currentUser.getDailyStats().addSteps(steps);
        
        // Award points for reaching step goals
        if (steps >= 10000) {
            currentUser.addPoints(10);
            System.out.println("Congratulations! You've reached your daily step goal!");
            currentUser.addAchievement("Step Master - Reached 10,000 steps");
        }
        
        System.out.println("Steps logged successfully!");
    }

    private static void logSleep() {
        System.out.print("\nEnter sleep duration (hours): ");
        double sleepHours = getValidDoubleInput(0, 24);
        currentUser.getDailyStats().setSleepHours(sleepHours);
        
        // Award points for healthy sleep
        if (sleepHours >= 7 && sleepHours <= 9) {
            currentUser.addPoints(5);
            System.out.println("Great job maintaining a healthy sleep schedule!");
            currentUser.addAchievement("Sleep Champion - Maintained healthy sleep schedule");
        }
        
        System.out.println("Sleep duration logged successfully!");
    }

    // Helper methods for input validation
    private static int getValidIntInput(int min, int max) {
        while (true) {
            try {
                int input = scanner.nextInt();
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %d and %d: ", min, max);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static double getValidDoubleInput(double min, double max) {
        while (true) {
            try {
                double input = scanner.nextDouble();
                if (input >= min && input <= max) {
                    return input;
                }
                System.out.printf("Please enter a number between %.1f and %.1f: ", min, max);
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
    }

    private static boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
} 