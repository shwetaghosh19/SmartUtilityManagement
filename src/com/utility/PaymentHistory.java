package com.utility;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PaymentHistory {
    public PaymentHistory(int userId) {
        JFrame frame = new JFrame("Payment History");
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Date", "Utility Type", "Amount Paid"};
        String[][] data = getPaymentData(userId);

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private String[][] getPaymentData(int userId) {
        List<String[]> paymentList = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT payment_date, utility_type, paid_amount FROM payments WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                paymentList.add(new String[]{
                        rs.getString("payment_date"),
                        rs.getString("utility_type"),
                        String.valueOf(rs.getDouble("paid_amount"))
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        return paymentList.toArray(new String[0][0]); // Convert List to 2D array
    }
}

