package com.utility;

import javax.swing.*;
import java.awt.*;

public class UserDashboard {
    public UserDashboard(int userId) {
        JFrame frame = new JFrame("User Dashboard");
        frame.setSize(600, 400);
        frame.setLayout(new GridLayout(3, 2));

        JButton payBillButton = new JButton("Pay Utility Bill");
        JButton enterConsumptionButton = new JButton("Enter Consumption");
        JButton viewConsumptionButton = new JButton("Monitor Consumption");
        JButton paymentHistoryButton = new JButton("Payment History");
        JButton viewBillsButton = new JButton("View Pending Bills");
        JButton logoutButton = new JButton("Logout");

        frame.add(payBillButton);
        frame.add(enterConsumptionButton);
        frame.add(viewConsumptionButton);
        frame.add(paymentHistoryButton);
        frame.add(viewBillsButton);
        frame.add(logoutButton);

        // Actions for Buttons
        payBillButton.addActionListener(e -> new BillPaymentForm(userId));
        enterConsumptionButton.addActionListener(e -> new EnterConsumption(userId)); // Added for entering usage
        viewConsumptionButton.addActionListener(e -> new MonitorConsumption(userId)); // Fixed for monitoring
        paymentHistoryButton.addActionListener(e -> new PaymentHistory(userId));
        viewBillsButton.addActionListener(e -> new PendingBills(userId));

        logoutButton.addActionListener(e -> {
            frame.dispose();
            new App(); // Redirect to login
        });

        frame.setVisible(true);
    }
}
