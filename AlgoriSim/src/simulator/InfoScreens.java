
import javax.swing.*;
import java.awt.*;

public class InfoScreens {
    private CardLayout layout;
    private JPanel mainPanel;

    // Constructor to initialize layout and mainPanel from Main.java
    public InfoScreens(CardLayout layout, JPanel mainPanel) {
        this.layout = layout;
        this.mainPanel = mainPanel;
    }

    // Credits screen
    public void showCredits() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Credits", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea creditsText = new JTextArea(
                "AlgoriSim - CPU Scheduling Algorithm Simulator\n" +
                "Developed by: Eugene, Euvan, and Matthew\n" +
                "Version: 1.0\n\n" +
                "Special Thanks:\n" +
                "- Ms. Therese Nuelle Roca\n" +
                "- Class of CMSC 125-M Operating Systems (SS 2024-2025)\n"
        );
        creditsText.setEditable(false);
        creditsText.setFont(new Font("Arial", Font.PLAIN, 16));
        creditsText.setWrapStyleWord(true);
        creditsText.setLineWrap(true);

        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(creditsText), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        mainPanel.add(panel, "Credits");
    }

    // Help screen
    public void showHelp() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel titleLabel = new JLabel("Help - How to Use", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JTextArea helpText = new JTextArea(
                "Welcome to AlgoriSim - CPU Scheduling Simulator!\n\n" +
                "How to use:\n" +
                "1. Click 'START' to begin.\n" +
                "2. Choose how to input data (Random, User Input, or File).\n" +
                "3. Select a CPU Scheduling Algorithm:\n" +
                "   - First Come First Serve (FCFS)\n" +
                "   - Shortest Job First (SJF)\n" +
                "   - Round Robin (RR)\n" +
                "   - Priority Scheduling\n" +
                "4. View the simulation results and performance metrics.\n\n" +
                "For more details, refer to the documentation."
        );
        helpText.setEditable(false);
        helpText.setFont(new Font("Arial", Font.PLAIN, 16));
        helpText.setWrapStyleWord(true);
        helpText.setLineWrap(true);

        JButton backButton = new JButton("BACK");
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(helpText), BorderLayout.CENTER);
        panel.add(backButton, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        mainPanel.add(panel, "Help");
    }
}
