package com.career.util;

import com.career.dao.AdminDAO;
import com.career.model.Admin;

import java.util.Scanner;

/**
 * Command-line utility for safely initializing an administrator account.
 * Uses PBKDF2WithHmacSHA256 password hashing with a cryptographically random salt.
 * 
 * Usage:
 *   java -cp "src/main/webapp/WEB-INF/classes;lib/*" com.career.util.AdminSetup
 * Or with arguments:
 *   java -cp "src/main/webapp/WEB-INF/classes;lib/*" com.career.util.AdminSetup <username> <email> <fullName> <password>
 */
public class AdminSetup {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println(" Career Recommendation System - Admin Account Setup");
        System.out.println("=================================================");

        String username;
        String email;
        String fullName;
        String password;

        if (args.length >= 4) {
            username = args[0].trim();
            email = args[1].trim();
            fullName = args[2].trim();
            password = args[3];
        } else {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter Admin Username [e.g. admin]: ");
            username = scanner.nextLine().trim();
            if (username.isEmpty()) username = "admin";

            System.out.print("Enter Admin Email [e.g. admin@careercompass.edu]: ");
            email = scanner.nextLine().trim();
            if (email.isEmpty()) email = "admin@careercompass.edu";

            System.out.print("Enter Admin Full Name [e.g. System Administrator]: ");
            fullName = scanner.nextLine().trim();
            if (fullName.isEmpty()) fullName = "System Administrator";

            System.out.print("Enter Admin Password (min 6 chars): ");
            password = scanner.nextLine();
            while (password == null || password.length() < 6) {
                System.out.print("Password must be at least 6 characters. Enter again: ");
                password = scanner.nextLine();
            }
        }

        AdminDAO adminDAO = new AdminDAO();

        // Check if admin with this username or email already exists
        Admin existingUser = adminDAO.findByUsername(username);
        if (existingUser != null) {
            System.err.println("[ERROR] Admin user '" + username + "' already exists! (ID: " + existingUser.getId() + ")");
            return;
        }

        Admin existingEmail = adminDAO.findByEmail(email);
        if (existingEmail != null) {
            System.err.println("[ERROR] Admin with email '" + email + "' already exists!");
            return;
        }

        try {
            String hash = PasswordUtil.hashPassword(password);
            Admin admin = new Admin();
            admin.setUsername(username);
            admin.setEmail(email);
            admin.setFullName(fullName);
            admin.setPasswordHash(hash);

            boolean success = adminDAO.createAdmin(admin);
            if (success) {
                System.out.println("\n[SUCCESS] Administrator account initialized successfully!");
                System.out.println("-------------------------------------------------");
                System.out.println("ID:       " + admin.getId());
                System.out.println("Username: " + admin.getUsername());
                System.out.println("Email:    " + admin.getEmail());
                System.out.println("Name:     " + admin.getFullName());
                System.out.println("Hash:     " + hash.substring(0, 30) + "... (PBKDF2 SHA-256)");
                System.out.println("-------------------------------------------------");
                System.out.println("You can now log in at: http://localhost:8080/CareerRecommendationSystem/admin/login");
            } else {
                System.err.println("[FAILED] Could not insert administrator account into database.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Setup exception: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
