package com.utility;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class App {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private static int loggedInUserId = -1;
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new App().createAndShowGUI());
    }

    private void createAndShowGUI() {
        frame = new JFrame("Smart Utility Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 1));

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> openRegistrationForm());

        frame.add(emailLabel);
        frame.add(emailField);
        frame.add(passwordLabel);
        frame.add(passwordField);
        frame.add(loginButton);
        frame.add(registerButton);

        frame.setVisible(true);
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT id, password, salt, role FROM users WHERE email = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
    
            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                String role = rs.getString("role");
    
                if (verifyPassword(password, storedHashedPassword, salt)) {
                    loggedInUserId = rs.getInt("id");
    
                    if ("admin".equalsIgnoreCase(role)) {
                        JOptionPane.showMessageDialog(frame, "Admin Login Successful!");
                        new AdminDashboard();  // Open admin panel
                    } else {
                        JOptionPane.showMessageDialog(frame, "User Login Successful!");
                        new UserDashboard(loggedInUserId);  // Open user panel
                    }
                    frame.dispose();  // Close login screen
                } else {
                    JOptionPane.showMessageDialog(frame, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(frame, "User not registered. Please register first.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void openRegistrationForm() {
        new RegisterForm();
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hash = md.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    public static boolean verifyPassword(String password, String hashedPassword, String salt) {
        return hashPassword(password, salt).equals(hashedPassword);
    }

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }
}
