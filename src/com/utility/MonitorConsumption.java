package com.utility;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MonitorConsumption {
    public MonitorConsumption(int userId) {
        JFrame frame = new JFrame("Consumption History");
        frame.setSize(600, 300);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"Billing Month", "Utility Type", "Usage Amount"};
        String[][] data = getConsumptionData(userId);

        JTable table = new JTable(data, columnNames);
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private String[][] getConsumptionData(int userId) {
        List<String[]> consumptionList = new ArrayList<>();
    
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT billing_month, utility_type, usage_amount FROM consumption WHERE user_id = ?";
            
        
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
    
           
            while (rs.next()) {
                consumptionList.add(new String[]{
                    rs.getString("billing_month"),
                    rs.getString("utility_type"),
                    String.valueOf(rs.getDouble("usage_amount")) 
                });
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    
        return consumptionList.toArray(new String[0][0]); 
    }
    
}
