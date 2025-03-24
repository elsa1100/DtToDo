import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main_frame {

    String currentUser; // To track the logged-in user

    public Main_frame(String username) {
        this.currentUser = username; // Set current username

        // Fonts
        Font titlef = new Font("Imprint MT Shadow", Font.BOLD, 50);
        Font buttonsf = new Font("Baskerville Old Face", Font.PLAIN, 35);

        // Frame   
        JFrame frame = new JFrame("Main Window");
        frame.setBackground(Color.white);
        frame.setSize(700, 700);
        
        Container container = frame.getContentPane();

        // Main Panel
        JPanel mainp = new JPanel(new GridLayout(3, 1, 30, 20));
        container.add(mainp, BorderLayout.CENTER);

        // Label Panel
        JPanel labelp = new JPanel();
        JLabel label = new JLabel("MY TO DO ");
        label.setForeground(Color.pink);
        label.setFont(titlef);
        labelp.add(label);
        container.add(labelp, BorderLayout.NORTH);

        // Menu
        JMenuBar BAR = new JMenuBar();
        JMenu HOME = new JMenu("HOME ");
        JMenuItem USERS_ = new JMenuItem("Users window");
        JMenuItem EXIT_ = new JMenuItem("Exit the program");
        HOME.add(USERS_);
        HOME.add(EXIT_);
        BAR.add(HOME);
        frame.setJMenuBar(BAR);

        USERS_.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                frame.setVisible(false);
                new users(); // This doesn't require username as it's for all users
            }
        });

        EXIT_.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                System.exit(0);
            }
        });

        // Icons
        Icon truei = new ImageIcon(getClass().getResource("photos/true-32.png"));
        Icon calendari = new ImageIcon(getClass().getResource("photos/calendar-412-32.png"));
        Icon reminderi = new ImageIcon(getClass().getResource("photos/notification-reminder-32.png"));

        // Buttons
        JButton taskb = new JButton("   My Tasks", truei);
        taskb.setFont(buttonsf);
        taskb.setBackground(Color.white);
        taskb.setForeground(Color.pink);

        JButton calendarb = new JButton("   My Calendar ", calendari);
        calendarb.setBackground(Color.white);
        calendarb.setFont(buttonsf);
        calendarb.setForeground(Color.pink);

        JButton reminderb = new JButton("  My Alarm Trigger", reminderi);
        reminderb.setBackground(Color.white);
        reminderb.setFont(buttonsf);
        reminderb.setForeground(Color.pink);

        // Add buttons to panel
        mainp.add(taskb);
        mainp.add(calendarb);
        mainp.add(reminderb);

        // Actions for buttons - Pass username to each class
        taskb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                tasks taskWindow = new tasks(currentUser); // Pass username
                frame.setVisible(false);
            }
        });

        calendarb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                term_calendar calendarWindow = new term_calendar(currentUser); // Pass username
                frame.setVisible(false);
            }
        });

        reminderb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                Alarm alarmWindow = new Alarm(); // Pass username
                frame.setVisible(false);
            }
        });

        // Frame settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    // Main method for testing (optional)
    public static void main(String[] args) {
        // Example: pass a test username for testing
        new Main_frame("testuser");
    }
}