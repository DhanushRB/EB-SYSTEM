import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class ElectricityBillingSystem extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/electricity_billing";
    private static final String USER = "your_username"; // Replace with your MySQL username
    private static final String PASS = "your_password"; // Replace with your MySQL password

    private JTextField customerNameField;
    private JTextField unitsConsumedField;
    private JTextArea billsArea;

    public ElectricityBillingSystem() {
        setTitle("Electricity Billing System");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        customerNameField = new JTextField(15);
        unitsConsumedField = new JTextField(5);
        billsArea = new JTextArea(10, 30);
        billsArea.setEditable(false);
        
        JButton addButton = new JButton("Add Bill");
        JButton displayButton = new JButton("Display Bills");

        add(new JLabel("Customer Name:"));
        add(customerNameField);
        add(new JLabel("Units Consumed:"));
        add(unitsConsumedField);
        add(addButton);
        add(displayButton);
        add(new JScrollPane(billsArea));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addBill();
            }
        });

        displayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                displayBills();
            }
        });
    }

    private void addBill() {
        String customerName = customerNameField.getText();
        int unitsConsumed;
        try {
            unitsConsumed = Integer.parseInt(unitsConsumedField.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid number of units.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "INSERT INTO bills (customer_name, units_consumed, bill_amount) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                double billAmount = calculateBillAmount(unitsConsumed);
                pstmt.setString(1, customerName);
                pstmt.setInt(2, unitsConsumed);
                pstmt.setDouble(3, billAmount);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Bill added successfully.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void displayBills() {
        StringBuilder bills = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(DB_URL, USER, PASS)) {
            String query = "SELECT * FROM bills";
            try (PreparedStatement pstmt = connection.prepareStatement(query);
                 ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bills.append("ID: ").append(rs.getInt("id"))
                         .append(", Customer Name: ").append(rs.getString("customer_name"))
                         .append(", Units Consumed: ").append(rs.getInt("units_consumed"))
                         .append(", Bill Amount: ").append(rs.getDouble("bill_amount"))
                         .append("\n");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        billsArea.setText(bills.toString());
    }

    private double calculateBillAmount(int unitsConsumed) {
        double ratePerUnit = 5.0; // Example rate
        return unitsConsumed * ratePerUnit;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ElectricityBillingSystem frame = new ElectricityBillingSystem();
            frame.setVisible(true);
        });
    }
}