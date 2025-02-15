package com.utility;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

class BillPaymentForm {
    public BillPaymentForm(int userId) {
        JFrame frame = new JFrame("Bill Payment");
        frame.setSize(400, 300);
        frame.setLayout(new GridLayout(6, 2));

        JLabel utilityLabel = new JLabel("Utility Type:");
        JComboBox<String> utilityTypeComboBox = new JComboBox<>(new String[]{"Electricity", "Water", "Gas", "Internet"});

        JLabel billingMonthLabel = new JLabel("Billing Month (YYYY-MM):");
        JTextField billingMonthField = new JTextField(10);

        JLabel amountLabel = new JLabel("Total Bill:");
        JTextField amountField = new JTextField(10);
        amountField.setEditable(false);

        JLabel payAmountLabel = new JLabel("Amount to Pay:");
        JTextField payAmountField = new JTextField(10);

        JButton payButton = new JButton("Pay");

        frame.add(utilityLabel);
        frame.add(utilityTypeComboBox);
        frame.add(billingMonthLabel);
        frame.add(billingMonthField);
        frame.add(amountLabel);
        frame.add(amountField);
        frame.add(payAmountLabel);
        frame.add(payAmountField);
        frame.add(new JLabel()); // Placeholder
        frame.add(payButton);

        frame.setVisible(true);

        utilityTypeComboBox.addActionListener(e -> {
            String selectedUtility = (String) utilityTypeComboBox.getSelectedItem();
            String billingMonth = formatBillingMonth(billingMonthField.getText());

            if (billingMonth == null) {
                amountField.setText("Invalid Format");
                return;
            }

            double totalBill = calculateTotalBill(userId, selectedUtility, billingMonth);
            amountField.setText(String.valueOf(totalBill));
        });

        payButton.addActionListener(e -> {
            String billingMonth = formatBillingMonth(billingMonthField.getText());
            if (billingMonth == null) {
                JOptionPane.showMessageDialog(frame, "Invalid Billing Month Format! Use YYYY-MM", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String amountText = payAmountField.getText();
            if (amountText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Enter an amount to pay!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double amountToPay = Double.parseDouble(amountText);
                double totalBill = Double.parseDouble(amountField.getText());

                if (amountToPay > totalBill) {
                    JOptionPane.showMessageDialog(frame, "You cannot pay more than the bill amount!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double amountDue = totalBill - amountToPay;
                String paymentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                try (Connection conn = DatabaseConnection.getConnection()) {
                    String insertQuery = "INSERT INTO payments (user_id, utility_type, paid_amount, amount_due, payment_date) " +
                                         "VALUES (?, ?, ?, ?, ?)";
                    PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                    pstmt.setInt(1, userId);
                    pstmt.setString(2, (String) utilityTypeComboBox.getSelectedItem());
                    pstmt.setDouble(3, amountToPay);
                    pstmt.setDouble(4, amountDue);
                    pstmt.setString(5, paymentDate);

                    int rowsInserted = pstmt.executeUpdate();

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(frame, "Payment Successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        frame.dispose();
                    } else {
                        JOptionPane.showMessageDialog(frame, "Payment Failed!", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid amount entered!", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(frame, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private String formatBillingMonth(String billingMonth) {
        if (billingMonth.matches("\\d{4}-\\d{2}")) {
            return billingMonth + "-01"; // Append first day of the month
        }
        return null;
    }

    private double calculateTotalBill(int userId, String utilityType, String billingMonth) {
        double totalBill = 0.0;

        if (billingMonth == null) {
            JOptionPane.showMessageDialog(null, "Invalid billing month format! Use YYYY-MM.", "Error", JOptionPane.ERROR_MESSAGE);
            return 0.0;
        }

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT c.usage_amount, b.amount " +
                           "FROM consumption c " +
                           "JOIN bills b ON c.utility_type = b.utility_type " +
                           "WHERE c.user_id = ? AND c.utility_type = ? AND c.billing_month = ?";

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            pstmt.setString(2, utilityType);
            pstmt.setString(3, billingMonth);

            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                double usageAmount = rs.getDouble("usage_amount");
                double pricePerUnit = rs.getDouble("amount");
                totalBill = usageAmount * pricePerUnit;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return totalBill;
    }
}
