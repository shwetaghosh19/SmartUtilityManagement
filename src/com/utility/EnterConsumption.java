package com.utility;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class EnterConsumption {
    public EnterConsumption(int userId) {
        JFrame frame = new JFrame("Enter Consumption Data");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(5, 2));

        JLabel lblUtilityType = new JLabel("Utility Type:");
        String[] utilities = {"Electricity", "Water", "Gas"};
        JComboBox<String> cbUtilityType = new JComboBox<>(utilities);

        JLabel lblBillingMonth = new JLabel("Billing Month (YYYY-MM):");
        JTextField txtBillingMonth = new JTextField();

        JLabel lblCurrentReading = new JLabel("Current Meter Reading:");
        JTextField txtCurrentReading = new JTextField();

        JButton btnSubmit = new JButton("Submit");

        frame.add(lblUtilityType);
        frame.add(cbUtilityType);
        frame.add(lblBillingMonth);
        frame.add(txtBillingMonth);
        frame.add(lblCurrentReading);
        frame.add(txtCurrentReading);
        frame.add(new JLabel()); // Empty space
        frame.add(btnSubmit);

        frame.setVisible(true);

        // Button Click Event to Insert Data
        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String utilityType = cbUtilityType.getSelectedItem().toString();
                String billingMonthInput = txtBillingMonth.getText().trim();
                double currentReading;

                // Validate and format billing month
                String billingMonth = formatBillingMonth(billingMonthInput);
                if (billingMonth == null) {
                    JOptionPane.showMessageDialog(frame, "Invalid billing month format. Use YYYY-MM.");
                    return;
                }

                try {
                    currentReading = Double.parseDouble(txtCurrentReading.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Please enter a valid number for meter reading.");
                    return;
                }

                // Fetch previous reading and calculate consumption
                double previousReading = getPreviousReading(userId, utilityType);
                double usageAmount = currentReading - previousReading;

                if (usageAmount < 0) {
                    JOptionPane.showMessageDialog(frame, "Error: Current reading is lower than previous reading!");
                    return;
                }

                insertConsumptionData(userId, utilityType, billingMonth, currentReading, usageAmount);
                JOptionPane.showMessageDialog(frame, "Consumption data saved successfully!");
                frame.dispose(); // Close form after submission
            }
        });
    }

    // Validate and format billing month
    private String formatBillingMonth(String input) {
        try {
            LocalDate date = LocalDate.parse(input + "-01");
            return date.toString(); // Returns YYYY-MM-01
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    // Fetch the last recorded meter reading
    private double getPreviousReading(int userId, String utilityType) {
        double lastReading = 0.0;
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT current_reading FROM consumption WHERE user_id = ? AND utility_type = ? ORDER BY id DESC LIMIT 1";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, utilityType);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                lastReading = rs.getDouble("current_reading");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return lastReading;
    }

    // Insert the calculated consumption into the database
    private void insertConsumptionData(int userId, String utilityType, String billingMonth, double currentReading, double usageAmount) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO consumption (user_id, utility_type, billing_month, current_reading, usage_amount) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, utilityType);
            pstmt.setString(3, billingMonth);
            pstmt.setDouble(4, currentReading);
            pstmt.setDouble(5, usageAmount);

            pstmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
