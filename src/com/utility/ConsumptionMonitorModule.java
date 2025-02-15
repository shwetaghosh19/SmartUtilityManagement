package com.utility;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;
class ConsumptionMonitorModule {
    public ConsumptionMonitorModule() {
        JFrame frame = new JFrame("Consumption Monitor");
        frame.setSize(600, 400);

        String[] columnNames = {"User ID", "Utility Type","Billing Month", "Usage Amount"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable consumptionTable = new JTable(tableModel);

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_id, utility_type, billing_month, usage_amount FROM consumption";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String utilityType = rs.getString("utility_type");
                String billingMonth = rs.getString("billing_month");
                double usageAmount = rs.getDouble("usage_amount");

                tableModel.addRow(new Object[]{userId, utilityType, billingMonth, usageAmount});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        frame.add(new JScrollPane(consumptionTable));
        frame.setVisible(true);
    }
}
