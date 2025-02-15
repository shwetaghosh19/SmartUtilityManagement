package com.utility;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
public class UserConsumptionModule {
    public UserConsumptionModule(int userId) {
        JFrame frame = new JFrame("Your Consumption");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Utility Type", "Usage (Units)", "Billing Month"};
        String[][] data = getConsumptionData(userId);

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private String[][] getConsumptionData(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT utility_type, usage_amount, billing_month FROM consumption WHERE user_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            rs.last();
            int rowCount = rs.getRow();
            rs.beforeFirst();

            String[][] data = new String[rowCount][3];
            int i = 0;
            while (rs.next()) {
                data[i][0] = rs.getString("utility_type");
                data[i][1] = String.valueOf(rs.getDouble("usage_amount"));
                data[i][2] = rs.getString("billing_month");
                i++;
            }
            return data;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return new String[0][0];
        }
    }
}

