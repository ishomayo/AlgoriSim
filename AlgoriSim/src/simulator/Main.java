package simulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.ArrayList;
import java.util.Collections;

public class Main extends JFrame {

    protected static int width = 900, height = 600;
    private CardLayout layout = new CardLayout();
    private JPanel mainPanel = new JPanel(layout);
    private JButton continueButton;

    public Main() {
        setTitle("AlgoriSim - CPU Scheduling Simulator");
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        mainPanel = new JPanel(layout); // Main container with CardLayout
        add(mainPanel);

        InfoScreens infoScreens = new InfoScreens(layout, mainPanel);

        Lobby();
        DataInputSelection();
        infoScreens.showCredits();
        infoScreens.showHelp();

        layout.show(mainPanel, "Lobby");
    }

    // Lobby screen with buttons
    public void Lobby() {
        JPanel panel = new JPanel(new BorderLayout()); // Main panel with border layout
        panel.setBorder(BorderFactory.createEmptyBorder(50, 50, 50, 50)); // Add padding
    
        JLabel titleLabel = new JLabel("Welcome to AlgoriSim", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        // Wrapper panel to hold buttons and keep them centered
        JPanel buttonPanel = new JPanel(new GridBagLayout()); // Centers components automatically
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
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(150, 40)); // Adjust button size
        return button;
    }    

    // Data Input Selection screen
    public void DataInputSelection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(50, 50, 50, 50)); // Padding around content
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Border around the panel
    
        JLabel titleLabel = new JLabel("Select Data Input Method", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    
        // Create a container for the "floating" buttons (aligned horizontally)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false); // Transparent background
    
        // Create card-like buttons
        JButton randomButton = createCardButton("Generate Random Data");
        JButton userInputButton = createCardButton("User-Inputted Data");
        JButton fileInputButton = createCardButton("Load from Text File");
    
        // Add buttons to the horizontal layout
        buttonPanel.add(randomButton);
        buttonPanel.add(userInputButton);
        buttonPanel.add(fileInputButton);
    
        // Small Back Button (aligned at the bottom center)
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(50, 30));
    
        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        backPanel.setOpaque(false);
    
        // Add action listeners
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
        randomButton.addActionListener(e -> showRandomDataScreen());
        userInputButton.addActionListener(e -> showUserInputScreen());
        fileInputButton.addActionListener(e -> showFileInputScreen());
    
        // Add components to the panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(backPanel, BorderLayout.SOUTH);
    
        mainPanel.add(panel, "DataInputSelection");
    }
    
    // Helper method to create "card-like" buttons
    private JButton createCardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(250, 100)); // Larger buttons for better visibility
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true), // Rounded border
                BorderFactory.createEmptyBorder(10, 15, 10, 15) // Padding
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
    
        // Hover effect (optional)
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(230, 230, 230));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
    
        return button;
    }

    public void showRandomDataScreen() {
        JPanel randomDataPanel = new JPanel(new BorderLayout());
        randomDataPanel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
    
        // Table Columns
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 3) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table uneditable
            }
        };
        JTable dataTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(dataTable);
        randomDataPanel.add(scrollPane, BorderLayout.CENTER);
    
        // Generate Data Button
        JButton generateButton = new JButton("Generate Data");
        generateButton.addActionListener(e -> {
            generateRandomData(tableModel);
            validateTableData(tableModel, continueButton); // Ensure continueButton is updated
        });
    
        randomDataPanel.add(generateButton, BorderLayout.NORTH);
    
        // Create Bottom Panel (for Buttons)
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding
    
        // Back Button
        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
        bottomPanel.add(backButton, BorderLayout.WEST);
    
        // Continue Button (Initially Disabled)
        continueButton = new JButton("Continue"); // Make continueButton an instance variable
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(120, 40));
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> showSelectAlgorithmScreen());
        bottomPanel.add(continueButton, BorderLayout.EAST);
    
        randomDataPanel.add(bottomPanel, BorderLayout.SOUTH);
    
        // Add Table Listener to enable Continue Button dynamically
        tableModel.addTableModelListener(e -> validateTableData(tableModel, continueButton));
    
        mainPanel.add(randomDataPanel, "RandomDataScreen");
        layout.show(mainPanel, "RandomDataScreen");
    }
    
    // Generates Random Data without removing buttons
    private void generateRandomData(DefaultTableModel model) {
        Random random = new Random();
        int numRows = random.nextInt(18) + 3; // Between 3 and 20
    
        // Ensure Unique Priority Numbers
        List<Integer> priorityList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            priorityList.add(i);
        }
        Collections.shuffle(priorityList);
    
        // Clear and Generate Data
        model.setRowCount(0);
        File file = new File("D://Documents//AlgoriSim//AlgoriSim//src//simulator//random_data.txt");
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int i = 0; i < numRows; i++) {
                int arrivalTime = random.nextInt(31);
                int burstTime = random.nextInt(30) + 1;
                int priority = priorityList.get(i);
    
                String[] rowData = {"P" + (i + 1), String.valueOf(arrivalTime), String.valueOf(burstTime), String.valueOf(priority)};
                model.addRow(rowData);
    
                writer.write(String.join(" ", rowData));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    
        // Ensure the Continue Button is updated after data generation
        validateTableData(model, continueButton);
    }
    
    // Validates if all table cells contain data and enables Continue Button
    private void validateTableData(DefaultTableModel model, JButton continueButton) {
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                if (value == null || value.toString().trim().isEmpty()) {
                    continueButton.setEnabled(false);
                    return;
                }
            }
        }
        continueButton.setEnabled(true); // Enable if all cells are valid
    }    

    // User Input screen
    public void showUserInputScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Border around the panel

        JLabel titleLabel = new JLabel("Enter Process Data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Table Model (Process ID, Arrival Time, Burst Time)
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Process ID column should NOT be editable
            }
        };

        // Initialize table with 3 rows (minimum requirement)
        JTable processTable = new JTable(tableModel);
        for (int i = 1; i <= 3; i++) {
            tableModel.addRow(new Object[]{"P" + i, "", ""});
        }

        JScrollPane scrollPane = new JScrollPane(processTable);

        // Add Process Button
        JButton addProcessButton = new JButton("Add Process");
        addProcessButton.addActionListener(e -> {
            int rowCount = tableModel.getRowCount();
            if (rowCount < 20) {
                tableModel.addRow(new Object[]{"P" + (rowCount + 1), "", ""});
            } else {
                JOptionPane.showMessageDialog(null, "Maximum of 20 processes reached!", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Back Button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));

        // Continue Button (Navigates to Algorithm Selection Screen)
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(100, 30));
        continueButton.addActionListener(e -> showSelectAlgorithmScreen());

        // Create a separate panel for buttons (so both are visible)
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addProcessButton);
        buttonPanel.add(backButton); // Now both buttons are in the same panel
        buttonPanel.add(continueButton);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "UserInputScreen");
        layout.show(mainPanel, "UserInputScreen");
    }

    // ===============================
    // 🔹 New Method: File Input Screen
    // ===============================
    private void showFileInputScreen() {
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Load from Text File", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        // Create Table with Uneditable Rows
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 3) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table uneditable
            }
        };
        JTable table = new JTable(model);
        JScrollPane tableScrollPane = new JScrollPane(table);

        // Choose File Button
        JButton chooseFileButton = new JButton("Choose File");
        chooseFileButton.addActionListener(e -> loadFileData(model));

        // Back Button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));

        // Continue Button (Navigates to Algorithm Selection Screen)
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(100, 30));
        continueButton.addActionListener(e -> showSelectAlgorithmScreen());

        // Panel Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(chooseFileButton);
        bottomPanel.add(backButton);
        bottomPanel.add(continueButton);

        // Add components
        filePanel.add(titleLabel, BorderLayout.NORTH);
        filePanel.add(centerPanel, BorderLayout.CENTER);
        filePanel.add(bottomPanel, BorderLayout.SOUTH);

        mainPanel.add(filePanel, "FileInputScreen");
        layout.show(mainPanel, "FileInputScreen");
    }

    // ===============================
    // 🔹 Method to Load File Data into Table
    // ===============================
    private void loadFileData(DefaultTableModel model) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();

            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                List<String[]> rowData = new ArrayList<>(); // ✅ Fixed: Now properly parameterized
                
                // Read and store file data
                while ((line = br.readLine()) != null) {
                    String[] data = line.trim().split("\\s+"); // Split by spaces/tabs
                    
                    if (data.length >= 3) {
                        rowData.add(data);
                    }
                }

                // Ensure minimum of 3 rows and maximum of 20 rows
                if (rowData.size() < 3) {
                    JOptionPane.showMessageDialog(null, "File must have at least 3 rows!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int totalRows = Math.min(rowData.size(), 20);

                // Clear existing table data
                model.setRowCount(0);

                // Add new rows dynamically
                for (int i = 0; i < totalRows; i++) {
                    String[] data = rowData.get(i);
                    model.addRow(new Object[]{"P" + (i + 1), data[0], data[1], data[2]});
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void showSelectAlgorithmScreen() {
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

    // Start the application
    public static void startApplication() {
        SwingUtilities.invokeLater(() -> {
            Main main = new Main();
            main.setVisible(true);
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SplashScreen splash = new SplashScreen();
            splash.showSplash();
        });
    }
}