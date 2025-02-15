package com.utility;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.io.FileWriter;
import java.io.IOException;

public class ReportGenerator {
    private JFrame frame;
    private JTable reportTable;
    private DefaultTableModel tableModel;

    public ReportGenerator() {
        frame = new JFrame("Reports");
        frame.setSize(700, 400);
        frame.setLayout(new BorderLayout());

        String[] columnNames = {"User ID", "Utility Type", "Paid Amount", "Amount Due", "Payment Date"};
        tableModel = new DefaultTableModel(columnNames, 0);
        reportTable = new JTable(tableModel);

        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(e -> exportToCSV());

        fetchReportData();

        frame.add(new JScrollPane(reportTable), BorderLayout.CENTER);
        frame.add(exportButton, BorderLayout.SOUTH);
        frame.setVisible(true);
    }

    private void fetchReportData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT user_id, utility_type, paid_amount, amount_due, payment_date FROM payments";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String utilityType = rs.getString("utility_type");
                double paidAmount = rs.getDouble("paid_amount");
                double amountDue = rs.getDouble("amount_due");
                String paymentDate = rs.getString("payment_date");

                tableModel.addRow(new Object[]{userId, utilityType, paidAmount, amountDue, paymentDate});
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Database Error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToCSV() {
        try {
            FileWriter writer = new FileWriter("report.csv");
            writer.write("User ID,Utility Type,Paid Amount,Amount Due,Payment Date\n");

            for (int i = 0; i < tableModel.getRowCount(); i++) {
                for (int j = 0; j < tableModel.getColumnCount(); j++) {
                    writer.write(tableModel.getValueAt(i, j).toString() + ",");
                }
                writer.write("\n");
            }

            writer.close();
            JOptionPane.showMessageDialog(frame, "Report Exported Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error Exporting Report!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
