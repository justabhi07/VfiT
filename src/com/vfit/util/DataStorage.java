package com.vfit.util;

import com.vfit.User;
import com.vfit.DailyStats;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataStorage {
    private static final String DATA_DIR = "data";
    private static final String USERS_FILE = DATA_DIR + "/users.dat";

    public static void saveUser(User user) {
        try {
            // Create data directory if it doesn't exist
            new File(DATA_DIR).mkdirs();
            
            // Read existing users
            List<User> users = loadUsers();
            
            // Update or add the user
            boolean found = false;
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getUserId() == user.getUserId()) {
                    users.set(i, user);
                    found = true;
                    break;
                }
            }
            if (!found) {
                users.add(user);
            }
            
            // Save all users
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USERS_FILE))) {
                oos.writeObject(users);
            }
        } catch (IOException e) {
            System.err.println("Error saving user data: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE);
        
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                users = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading user data: " + e.getMessage());
            }
        }
        
        return users;
    }

    public static User findUser(String username) {
        List<User> users = loadUsers();
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
} 