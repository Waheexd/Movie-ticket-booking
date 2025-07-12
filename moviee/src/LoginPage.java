import java.awt.*;
import java.sql.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class LoginPage extends JFrame {
    JTextField userField;
    JPasswordField passField;
    JButton loginButton;
    JLabel statusLabel;

    final String DB_URL = "jdbc:mysql://localhost:3306/testdb";
    final String DB_USER = "root";
    final String DB_PASS = "";

    public LoginPage() {
        setTitle("User Login");
        setSize(400, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BoxLayout(wrapperPanel, BoxLayout.Y_AXIS));
        wrapperPanel.setBackground(Color.WHITE);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(40, 30, 30, 30));

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new TitledBorder("Login Form"));
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(userLabel, gbc);

        userField = new JTextField(20);
        gbc.gridx = 1; gbc.gridy = 0;
        formPanel.add(userField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(passLabel, gbc);

        passField = new JPasswordField(20);
        gbc.gridx = 1; gbc.gridy = 1;
        formPanel.add(passField, gbc);

        loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(59, 89, 182));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("Tahoma", Font.BOLD, 12));

        statusLabel = new JLabel("", SwingConstants.CENTER);
        statusLabel.setForeground(Color.RED);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        wrapperPanel.add(formPanel);
        wrapperPanel.add(Box.createVerticalStrut(20));
        wrapperPanel.add(loginButton);
        wrapperPanel.add(Box.createVerticalStrut(10));
        wrapperPanel.add(statusLabel);

        add(wrapperPanel, BorderLayout.CENTER);

        loginButton.addActionListener(e -> authenticate());
    }

    void authenticate() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

            String sql = "SELECT * FROM users WHERE username=? AND password=?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("id");

                statusLabel.setForeground(new Color(0, 128, 0));
                statusLabel.setText("Login Successful!");
                JOptionPane.showMessageDialog(this, "Welcome, " + username + "!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // ✅ This now works since MovieSelectionPage has (String, int) constructor
                SwingUtilities.invokeLater(() -> new MovieSelectionPage(username, userId).setVisible(true));
                dispose();
            } else {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("Invalid Credentials");
            }

            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            statusLabel.setText("Database Error");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginPage().setVisible(true));
    }
}
