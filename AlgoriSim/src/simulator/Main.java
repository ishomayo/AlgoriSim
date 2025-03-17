
import javax.swing.*;

import java.awt.*;

public class Main extends JFrame {

    protected static int width = 900, height = 600;
    private CardLayout layout = new CardLayout();
    private JPanel mainPanel = new JPanel(layout);
    

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
        });
    }

    public Main() {
        setSize(width, height); 
        setUndecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        mainPanel = new JPanel(layout); // Main container with CardLayout
        add(mainPanel);

        InfoScreens infoScreens = new InfoScreens(layout, mainPanel);
        DataInputScreen dataInputScreen = new DataInputScreen(this, layout, mainPanel);

        Lobby();
        dataInputScreen.showDataInputSelection();
        infoScreens.showCredits();
        infoScreens.showHelp();

        layout.show(mainPanel, "Lobby");
    }

    // Lobby screen with buttons
    public void Lobby() {
        JPanel panel = new JPanel(new BorderLayout()); // Main panel with border layout
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding
        panel.setBackground(new Color(1, 18, 34)); // Set background color to #011222

        JLabel titleLabel = new JLabel("Welcome to AlgoriSim", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE); // Ensure visibility on dark background

        // Wrapper panel to hold buttons and keep them centered
        JPanel buttonPanel = new JPanel(new GridBagLayout()); // Centers components automatically
        buttonPanel.setBackground(new Color(1, 18, 34)); // Match background color
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; // Column index
        gbc.gridy = GridBagConstraints.RELATIVE; // Auto-increment row
        gbc.insets = new Insets(10, 0, 10, 0); // Spacing between buttons
        gbc.anchor = GridBagConstraints.CENTER; // Center alignment

        // Create buttons
        JButton startButton = createStyledButton("START");
        JButton creditsButton = createStyledButton("CREDITS");
        JButton helpButton = createStyledButton("HELP");
        JButton exitButton = createStyledButton("EXIT");

        // Add buttons to buttonPanel with centering
        buttonPanel.add(startButton, gbc);
        buttonPanel.add(creditsButton, gbc);
        buttonPanel.add(helpButton, gbc);
        buttonPanel.add(exitButton, gbc);

        // Add action listeners
        exitButton.addActionListener(e -> System.exit(0));
        startButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
        creditsButton.addActionListener(e -> layout.show(mainPanel, "Credits"));
        helpButton.addActionListener(e -> layout.show(mainPanel, "Help"));

        // Add components to the main panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);

        mainPanel.add(panel, "Lobby");
    }

    // Helper method for button styling
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                String colorHex;

                if (getModel().isArmed()) {
                    colorHex = "#f1ffb9";
                } else {
                    colorHex = "#ffffff";
                }

                Color color = Color.decode(colorHex); // Convert hex to Color
                g.setColor(color);
                g.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight()); // Oblong shape

                super.paintComponent(g);
            }

            @Override
            protected void paintBorder(Graphics g) {
                g.setColor(Color.WHITE);
                g.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, getHeight(), getHeight());
            }
        };

        button.setPreferredSize(new Dimension(150, 40)); // Adjust button size
        button.setContentAreaFilled(false); // Makes the button transparent
        button.setFocusPainted(false); // Removes focus border
        button.setBorderPainted(false); // Removes default border

        return button;
    }

    public void showSelectAlgorithmScreen() {
        layout.show(mainPanel, "AlgorithmSelectionScreen");

        JPanel algorithmPanel = new JPanel(new BorderLayout());
        algorithmPanel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding

        JLabel titleLabel = new JLabel("Select Scheduling Algorithm", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Panel for algorithm buttons (3x2 Grid)
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 20, 20)); // 3 Rows, 2 Columns
        buttonPanel.setOpaque(false); // Transparent background

        // Create buttons for each algorithm
        JButton fcfsButton = new JButton("First Come First Serve");
        JButton rrButton = new JButton("Round Robin");
        JButton sjfPreButton = new JButton("Shortest Job First (Preemptive)");
        JButton sjfNonPreButton = new JButton("Shortest Job First (Non-preemptive)");
        JButton priorityPreButton = new JButton("Priority (Preemptive)");
        JButton priorityNonPreButton = new JButton("Priority (Non-preemptive)");

        // Add action listeners for buttons
        fcfsButton.addActionListener(e -> showSimulationScreenFCFS("FCFS"));
        rrButton.addActionListener(e -> showSimulationScreenRoundRobin("Round Robin"));
        sjfPreButton.addActionListener(e -> showSimulationScreenSJFPreemptive("SJFPre"));
        sjfNonPreButton.addActionListener(e -> showSimulationSJFNonPreemptive("SJFNon"));
        priorityPreButton.addActionListener(e -> showSimulationScreenPriorityPreemptive("PriorityPre"));
        priorityNonPreButton.addActionListener(e -> showSimulationScreenPriorityNonPreemptive("PriorityNon"));

        // Add buttons to panel
        buttonPanel.add(fcfsButton);
        buttonPanel.add(rrButton);
        buttonPanel.add(sjfPreButton);
        buttonPanel.add(sjfNonPreButton);
        buttonPanel.add(priorityPreButton);
        buttonPanel.add(priorityNonPreButton);

        // Panel for back and continue buttons (Centered horizontally)
        JPanel navigationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton backButton = new JButton("Back");
        JButton continueButton = new JButton("Continue");

        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
        continueButton.addActionListener(e -> System.out.println("Proceed to simulation setup"));

        navigationPanel.add(backButton);
        navigationPanel.add(continueButton);

        // Center the buttons in the main panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.add(buttonPanel);

        // Add components to the panel
        algorithmPanel.add(titleLabel, BorderLayout.NORTH);
        algorithmPanel.add(centerPanel, BorderLayout.CENTER);
        algorithmPanel.add(navigationPanel, BorderLayout.SOUTH);

        // Add the new screen to the main panel
        mainPanel.add(algorithmPanel, "SelectAlgorithmScreen");

        // Show the screen
        layout.show(mainPanel, "SelectAlgorithmScreen");
    }

    private void showSimulationSJFNonPreemptive(String algorithm) {
        SJFNonPreemptive simulationScreen = new SJFNonPreemptive(layout, null);
        mainPanel.add(simulationScreen, "SimulationScreen");
        layout.show(mainPanel, "SimulationScreen");
    }

    private void showSimulationScreenPriorityPreemptive(String algorithm) {
        PriorityPreemptive simulationScreen = new PriorityPreemptive(layout, null);
        mainPanel.add(simulationScreen, "SimulationScreen");
        layout.show(mainPanel, "SimulationScreen");
    }

    private void showSimulationScreenFCFS(String algorithm) {
        FCFS simulationScreen = new FCFS(layout, null);
        mainPanel.add(simulationScreen, "SimulationScreen");
        layout.show(mainPanel, "SimulationScreen");
    }

    private void showSimulationScreenPriorityNonPreemptive(String algorithm) {
        PriorityNonPreemptive simulationScreen = new PriorityNonPreemptive(layout, null);
        mainPanel.add(simulationScreen, "SimulationScreen");
        layout.show(mainPanel, "SimulationScreen");
    }

    private void showSimulationScreenSJFPreemptive(String algorithm) {
        SJFPreemptive simulationScreen = new SJFPreemptive(layout, null);
        mainPanel.add(simulationScreen, "SimulationScreen");
        layout.show(mainPanel, "SimulationScreen");
    }

    private void showSimulationScreenRoundRobin(String algorithm) {
        RoundRobin simulationScreen = new RoundRobin();
        mainPanel.add(simulationScreen, "SimulationScreen");
        layout.show(mainPanel, "SimulationScreen");
    }

    // Start the application
    public static void startApplication() {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }
}