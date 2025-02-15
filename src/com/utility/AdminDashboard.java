package com.utility;
import javax.swing.*;
import java.awt.*;


public class AdminDashboard {
    public AdminDashboard() {
        JFrame frame = new JFrame("Admin Dashboard");
        frame.setSize(600, 400);
        frame.setLayout(new GridLayout(4, 1));

        JButton userManagementButton = new JButton("User Management");
        JButton consumptionButton = new JButton("Monitor Consumption");
        JButton reportButton = new JButton("Generate Reports");
        JButton graphButton = new JButton("View Consumption Graph");

        userManagementButton.addActionListener(e -> new UserManagementModule());
        consumptionButton.addActionListener(e -> new ConsumptionMonitorModule());
        reportButton.addActionListener(e -> new ReportGenerator());


        frame.add(userManagementButton);
        frame.add(consumptionButton);
        frame.add(reportButton);
        frame.add(graphButton);

        frame.setVisible(true);
    }
}
