import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class tasks extends JFrame {

    JLabel title;
    JButton at, back;
    JPanel mainp, southp;
    JScrollPane sp;
    String currentUser; // To track the logged-in user

    // Fonts
    Font titlef = new Font("Imprint MT Shadow", Font.BOLD, 50);
    Font buttonsf = new Font("Baskerville Old Face", Font.PLAIN, 35);

    public tasks(String username) {
        this.currentUser = username; // Get current username

        // Frame settings
        this.setTitle("My Tasks");
        this.setSize(600, 800);
        
        this.setLayout(new BorderLayout(80, 10));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Title settings
        title = new JLabel("TO DO LIST");
        title.setForeground(Color.pink);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(titlef);
        this.add(title, BorderLayout.NORTH);

        // South panel settings
        southp = new JPanel(new GridLayout(1, 2));
        this.add(southp, BorderLayout.SOUTH);

        // Back button
        back = new JButton("Back");
        back.setFont(buttonsf);
        back.setBackground(Color.pink);
        back.setBorder(BorderFactory.createRaisedBevelBorder());
        southp.add(back);
        back.addActionListener(e -> {
            Main_frame obj = new Main_frame(currentUser);
            setVisible(false);
        });

        // Add task button
        at = new JButton("Add task");
        at.setFont(buttonsf);
        at.setBackground(Color.pink);
        at.setBorder(BorderFactory.createRaisedBevelBorder());
        southp.add(at);
        at.addActionListener(e -> {
            addTaskToDatabase();
            loadTasksFromDatabase(); // Refresh tasks display
        });

        // Scroll pane settings
        mainp = new JPanel(new GridLayout(0, 1));
        sp = new JScrollPane(mainp);
        sp.setBorder(BorderFactory.createLoweredBevelBorder());
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        this.add(sp, BorderLayout.CENTER);

        // Load existing tasks from database
        loadTasksFromDatabase();

        setVisible(true);
    }

    // Method to add task to database
    void addTaskToDatabase() {
        String taskText = JOptionPane.showInputDialog("Enter your task:");
        if (taskText != null && !taskText.trim().isEmpty()) {
            try (Connection conn = DBConnect.connect()) {
                String sql = "INSERT INTO tasks (username, task_text) VALUES (?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, currentUser);
                ps.setString(2, taskText);
                ps.executeUpdate();
                ps.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to add task!");
            }
        }
    }

    // Method to load tasks from database and display them
    void loadTasksFromDatabase() {
        mainp.removeAll(); // Clear existing tasks
        try (Connection conn = DBConnect.connect()) {
            String sql = "SELECT id, task_text FROM tasks WHERE username = ? AND status = 0"; // Load only incomplete tasks
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, currentUser);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int taskId = rs.getInt("id");
                String taskText = rs.getString("task_text");
                addTaskToPanel(taskId, taskText);
            }

            rs.close();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to load tasks!");
        }
        revalidate(); // Refresh GUI
        repaint();
    }
    // Method to add task visually with checkbox and delete option
    void addTaskToPanel(int taskId, String taskText) {
        JPanel p = new JPanel();
        JCheckBox cb = new JCheckBox();
        JTextArea ta = new JTextArea(taskText, 2, 40);
        ta.setFont(buttonsf);
        ta.setEditable(false);
        p.add(cb);
        p.add(ta);
        mainp.add(p);

        // Checkbox action to mark task as completed
        cb.addItemListener(ie -> {
            if (cb.isSelected()) {
                markTaskAsCompleted(taskId);
                mainp.remove(p); // Remove from view
                revalidate();
                repaint();
            }
        });
    }

    // Method to mark task as completed in database
    void markTaskAsCompleted(int taskId) {
        try (Connection conn = DBConnect.connect()) {
            String sql = "UPDATE tasks SET status = 1 WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, taskId);
            ps.executeUpdate();
            ps.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    // Main method (for testing purposes)
    public static void main(String[] args) {
        new tasks("testuser"); // Replace "testuser" with an actual username from your DB
    }
}