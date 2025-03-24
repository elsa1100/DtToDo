import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import javax.swing.*;

public class users {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;

    public users() {
        // === Frame Setup ===
        frame = new JFrame("User Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 350); // Larger window
        frame.setLocationRelativeTo(null);

        // === Panel with BoxLayout ===
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        // === Title ===
        JLabel title = new JLabel("Welcome to the System");
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(title);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 25)));

        // === Username Panel (Label + Field) ===
        JPanel userPanel = new JPanel(new BorderLayout());
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        usernameField = new JTextField(15);
        userPanel.add(usernameLabel, BorderLayout.NORTH);
        userPanel.add(usernameField, BorderLayout.CENTER);
        mainPanel.add(userPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // === Password Panel (Label + Field) ===
        JPanel passPanel = new JPanel(new BorderLayout());
        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordLabel, BorderLayout.NORTH);
        passPanel.add(passwordField, BorderLayout.CENTER);
        mainPanel.add(passPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        // === Buttons ===
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);

        mainPanel.add(buttonPanel);

        // === Add Panel to Frame ===
        frame.getContentPane().add(mainPanel);
        frame.setVisible(true);

        // === Login Action ===
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (checkLogin(username, password)) {
                frame.setVisible(false);
                new Main_frame(username);
            } else {
                JOptionPane.showMessageDialog(frame, "Incorrect username or password!", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        });

        // === Register Action ===
        registerButton.addActionListener(e -> {
            String newUsername = JOptionPane.showInputDialog(frame, "Enter new username:");
            if (newUsername == null || newUsername.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username cannot be empty!");
                return;
            }
            String newPassword = JOptionPane.showInputDialog(frame, "Enter password for " + newUsername + ":");
            if (newPassword == null || newPassword.trim().isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Password cannot be empty!");
                return;
            }

            if (registerNewUser(newUsername, newPassword)) {
                JOptionPane.showMessageDialog(frame, "User registered successfully!");
            } else {
                JOptionPane.showMessageDialog(frame, "Registration failed. User might already exist.");
            }
        });
    }

    // ====================== DATABASE FUNCTIONS ======================

    private boolean checkLogin(String username, String password) {
        try (Connection conn = DBConnect.connect()) {
            if (conn != null) {
                String sql = "SELECT password FROM users WHERE username = ?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String correctPassword = rs.getString("password");
                    return password.equals(correctPassword);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    }

    private boolean registerNewUser(String username, String password) {
        try (Connection conn = DBConnect.connect()) {
            if (conn != null) {
                String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, username);
                ps.setString(2, password);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(users::new);
    }
}
