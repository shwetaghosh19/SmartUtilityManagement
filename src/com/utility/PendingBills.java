package com.utility;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class PendingBills {
    public PendingBills(int userId) {
        JFrame frame = new JFrame("Pending Bills");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Utility Type", "Amount Due"};
        String[][] data = getPendingBills(userId);

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private String[][] getPendingBills(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT utility_type, amount_due FROM payments WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            // Check if there are any rows before calling rs.last()
            int rowCount = 0;
            if (rs.last()) {
                rowCount = rs.getRow();  // Get total row count
                rs.beforeFirst();  // Move back to first row
            }

            String[][] data = new String[rowCount][2];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("utility_type");
                data[i][1] = String.valueOf(rs.getDouble("amount_due"));
                i++;
            }
            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new String[0][0]; // Return empty array in case of error
        }
    }
}
