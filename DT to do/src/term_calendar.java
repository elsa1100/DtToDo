import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.*;

public class term_calendar extends JFrame {

    JPanel mainp, southp;
    JLabel title;
    JButton backb;
    int weeks;
    String username;
    Connection conn;

    Font titlef = new Font("Imprint MT Shadow", Font.BOLD, 35);
    Font buttonsf = new Font("Baskerville Old Face", Font.PLAIN, 15);
    Font labelsf = new Font("Baskerville Old Face", Font.PLAIN, 12);

    public term_calendar(String username) {
        this.username = username;

        conn = DBConnect.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(this, "❌ Database connection failed");
            return;
        }

        // ✅ Check if this user already has a saved calendar
        try {
            PreparedStatement check = conn.prepareStatement("SELECT COUNT(*) FROM term_calendar WHERE username = ?");
            check.setString(1, username);
            ResultSet rs = check.executeQuery();
            rs.next();
            int count = rs.getInt(1);

            if (count == 0) {
                // Ask weeks only the first time
                String w = JOptionPane.showInputDialog("Enter number of weeks: ");
                try {
                    weeks = Integer.parseInt(w);
                    saveCalendarStructure();
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(null, "Invalid number! Program will exit.");
                    System.exit(0);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error checking calendar data.");
            return;
        }

        // 🧱 Build the frame UI
        setSize(1000, 850);
        setTitle("My Calendar");
        
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        title = new JLabel("MY TERM CALENDAR");
        title.setFont(titlef);
        title.setForeground(Color.pink);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title, BorderLayout.NORTH);

        mainp = new JPanel(new GridLayout(0, 4));
        add(mainp, BorderLayout.CENTER);

        southp = new JPanel(new GridLayout(1, 2));
        add(southp, BorderLayout.SOUTH);

        backb = new JButton("Back");
        backb.setFont(labelsf);
        southp.add(backb);
        backb.addActionListener(e -> {
            Main_frame obj = new Main_frame(username);
            setVisible(false);
        });

        // 📥 Load calendar from database
        addCalendarFromDB();

        setVisible(true);
    }

    // ✅ Insert weeks and days into database (first time only)
    private void saveCalendarStructure() {
        try {
            for (int i = 1; i <= weeks; i++) {
                PreparedStatement ps = conn.prepareStatement(
                        "INSERT INTO term_calendar (username, week_number) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, username);
                ps.setInt(2, i);
                ps.executeUpdate();

                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    int calendarId = rs.getInt(1);
                    for (String day : Arrays.asList("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")) {
                        PreparedStatement ps2 = conn.prepareStatement(
                                "INSERT INTO term_calendar_days (term_calendar_id, day_name) VALUES (?, ?)");
                        ps2.setInt(1, calendarId);
                        ps2.setString(2, day);
                        ps2.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error saving calendar to DB.");
        }
    }

    // ✅ Load weeks and days from DB into the GUI
    private void addCalendarFromDB() {
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM term_calendar WHERE username = ?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int calendarId = rs.getInt("id");
                int weekNumber = rs.getInt("week_number");
                boolean isComplete = rs.getBoolean("is_complete");

                JPanel weekPanel = new JPanel(new BorderLayout());
                weekPanel.setBorder(BorderFactory.createLineBorder(Color.black));

                JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 7));
                Color myColor = new Color(170, 238, 170); // Light green for task done
                headerPanel.setBackground(isComplete ? myColor : Color.white);
                weekPanel.add(headerPanel, BorderLayout.NORTH);

                JLabel weekLabel = new JLabel("Week : " + weekNumber);
                weekLabel.setFont(buttonsf);
                weekLabel.setForeground(Color.black);
                headerPanel.add(weekLabel);

                JCheckBox weekCB = new JCheckBox();
                weekCB.setSelected(isComplete);
                headerPanel.add(weekCB);

                int finalCalId = calendarId;
                weekCB.addItemListener(e -> {
                    try {
                        PreparedStatement update = conn.prepareStatement(
                                "UPDATE term_calendar SET is_complete = ? WHERE id = ?");
                        update.setBoolean(1, weekCB.isSelected());
                        update.setInt(2, finalCalId);
                        update.executeUpdate();
                        headerPanel.setBackground(weekCB.isSelected() ? myColor : Color.white);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                JPanel daysPanel = new JPanel(new GridLayout(7, 1));
                weekPanel.add(daysPanel, BorderLayout.CENTER);

                PreparedStatement ps2 = conn.prepareStatement(
                        "SELECT * FROM term_calendar_days WHERE term_calendar_id = ?");
                ps2.setInt(1, calendarId);
                ResultSet rs2 = ps2.executeQuery();

                while (rs2.next()) {
                    int dayId = rs2.getInt("id");
                    String day = rs2.getString("day_name");
                    String task = rs2.getString("task_text");
                    boolean isDone = rs2.getBoolean("is_done");

                    JPanel dayRow = new JPanel(new GridLayout(1, 3));

                    JLabel dayLabel = new JLabel(day);
                    dayLabel.setFont(labelsf);
                    dayLabel.setHorizontalAlignment(SwingConstants.CENTER);

                    JTextField taskField = new JTextField(task);

                    JCheckBox taskCheck = new JCheckBox();
                    taskCheck.setSelected(isDone);
                    taskCheck.setHorizontalAlignment(SwingConstants.CENTER);

                    taskCheck.addItemListener(e -> {
                        try {
                            PreparedStatement update = conn.prepareStatement(
                                    "UPDATE term_calendar_days SET is_done = ? WHERE id = ?");
                            update.setBoolean(1, taskCheck.isSelected());
                            update.setInt(2, dayId);
                            update.executeUpdate();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    });

                    taskField.addFocusListener(new FocusAdapter() {
                        public void focusLost(FocusEvent e) {
                            try {
                                PreparedStatement update = conn.prepareStatement(
                                        "UPDATE term_calendar_days SET task_text = ? WHERE id = ?");
                                update.setString(1, taskField.getText());
                                update.setInt(2, dayId);
                                update.executeUpdate();
                            } catch (SQLException ex) {
                                ex.printStackTrace();
                            }
                        }
                    });

                    dayRow.add(dayLabel);
                    dayRow.add(taskField);
                    dayRow.add(taskCheck);
                    daysPanel.add(dayRow);
                }

                mainp.add(weekPanel);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "❌ Error loading calendar.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new term_calendar("test_user"));
    }
}
