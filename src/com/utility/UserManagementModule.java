package com.utility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserManagementModule {
    public UserManagementModule() {
        JFrame frame = new JFrame("User Management");
        frame.setSize(500, 300);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"User ID", "Email", "Role"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);

        fetchUserData(tableModel);

        frame.add(new JScrollPane(table), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private void fetchUserData(DefaultTableModel tableModel) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT ID, Email, Role FROM users";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("ID");
                String email = rs.getString("Email");
                String role = rs.getString("Role");

                tableModel.addRow(new Object[]{userId, email, role});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

