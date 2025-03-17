
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

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
        // Load the background image
        ImageIcon backgroundImage = new ImageIcon("C:\\Users\\Eugene\\Desktop\\Git\\AlgoriSim\\AlgoriSim\\src\\simulator\\resources\\Lobby.png"); // Replace with your actual image path
    
        // Custom JPanel to draw the background image
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
            }
        };
    
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding
        panel.setBackground(new Color(1, 18, 34)); // Fallback background color
    
        // Wrapper panel to hold buttons and keep them centered
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        buttonPanel.setOpaque(false); // Make the button panel transparent
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(0, 0, 25, 0);
        gbc.anchor = GridBagConstraints.CENTER;
    
        // Create buttons
        JButton startButton = createStyledButton(CommonConstants.startDefault, CommonConstants.startDefault, CommonConstants.startClicked);
        JButton creditsButton = createStyledButton(CommonConstants.creditsDefault, CommonConstants.creditsDefault, CommonConstants.creditsClicked);
        JButton helpButton = createStyledButton(CommonConstants.helpDefault, CommonConstants.helpDefault, CommonConstants.helpClicked);
        JButton exitButton = createStyledButton(CommonConstants.exitDefault, CommonConstants.exitDefault, CommonConstants.exitClicked);
    
        // Add buttons to buttonPanel
        buttonPanel.add(Box.createVerticalStrut(100));
        buttonPanel.add(startButton, gbc);
        buttonPanel.add(creditsButton, gbc);
        buttonPanel.add(helpButton, gbc);
        buttonPanel.add(exitButton, gbc);
    
        // Add action listeners
        exitButton.addActionListener(e -> System.exit(0));
        startButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
        creditsButton.addActionListener(e -> layout.show(mainPanel, "Credits"));
        helpButton.addActionListener(e -> layout.show(mainPanel, "Help"));
    
        panel.add(buttonPanel, BorderLayout.CENTER);
    
        mainPanel.add(panel, "Lobby");
    }
    

    // Helper method for button styling
    private static JButton createStyledButton(String defaultIconPath, String hoverIconPath, String clickIconPath) {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 50));
    
        // Load and scale the images
        ImageIcon defaultIcon = scaleImage(defaultIconPath, button.getPreferredSize());
        ImageIcon hoverIcon = scaleImage(hoverIconPath, button.getPreferredSize());
        ImageIcon clickIcon = scaleImage(clickIconPath, button.getPreferredSize());
    
        button.setIcon(defaultIcon);
    
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setIcon(hoverIcon);
            }
    
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(defaultIcon);
            }
    
            @Override
            public void mousePressed(MouseEvent e) {
                button.setIcon(clickIcon);
            }
    
            @Override
            public void mouseReleased(MouseEvent e) {
                button.setIcon(hoverIcon);
            }
        });
    
        return button;
    }
    
    // Helper method to scale an image to fit the button
    private static ImageIcon scaleImage(String imagePath, Dimension size) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
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