package simulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class DataInputScreen extends JPanel  {
    private JPanel mainPanel;
    private CardLayout layout;
    private JButton continueButton; // Store as instance variable
    private Main main;

    public DataInputScreen(Main main, CardLayout layout, JPanel mainPanel) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        showRandomDataScreen();
    }

    // Data Input Selection screen
    public void showDataInputSelection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2));

        JLabel titleLabel = new JLabel("Select Data Input Method", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);

        JButton randomButton = createCardButton("Generate Random Data");
        JButton userInputButton = createCardButton("User-Inputted Data");
        JButton fileInputButton = createCardButton("Load from Text File");

        buttonPanel.add(randomButton);
        buttonPanel.add(userInputButton);
        buttonPanel.add(fileInputButton);

        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(50, 30));

        JPanel backPanel = new JPanel();
        backPanel.add(backButton);
        backPanel.setOpaque(false);

        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
        randomButton.addActionListener(e -> showRandomDataScreen());
        userInputButton.addActionListener(e -> showUserInputScreen());
        fileInputButton.addActionListener(e -> showFileInputScreen());

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonPanel, BorderLayout.CENTER);
        panel.add(backPanel, BorderLayout.SOUTH);

        mainPanel.add(panel, "DataInputSelection");
    }

    private JButton createCardButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setPreferredSize(new Dimension(250, 100));
        button.setFocusPainted(false);
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2, true),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

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

        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 3) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable dataTable = new JTable(tableModel);
        dataTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        JScrollPane scrollPane = new JScrollPane(dataTable);
        randomDataPanel.add(scrollPane, BorderLayout.CENTER);

        JButton generateButton = new JButton("Generate Data");
        generateButton.addActionListener(e -> {
            generateRandomData(tableModel);
            validateRandomTableData(tableModel, continueButton);
        });

        randomDataPanel.add(generateButton, BorderLayout.NORTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton backButton = new JButton("← Back");
        backButton.setFont(new Font("Arial", Font.BOLD, 14));
        backButton.setPreferredSize(new Dimension(120, 40));
        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
        bottomPanel.add(backButton, BorderLayout.WEST);

        continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(120, 40));
        continueButton.setEnabled(false);
        continueButton.addActionListener(e -> main.showSelectAlgorithmScreen());
        bottomPanel.add(continueButton, BorderLayout.EAST);

        randomDataPanel.add(bottomPanel, BorderLayout.SOUTH);
        tableModel.addTableModelListener(e -> validateRandomTableData(tableModel, continueButton));

        mainPanel.add(randomDataPanel, "RandomDataScreen");
        layout.show(mainPanel, "RandomDataScreen");
    }

    private void generateRandomData(DefaultTableModel model) {
        Random random = new Random();
        int numRows = random.nextInt(18) + 3;

        List<Integer> priorityList = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            priorityList.add(i);
        }
        Collections.shuffle(priorityList);

        model.setRowCount(0);
        File file = new File("D://Documents//AlgoriSim//AlgoriSim//random_data.txt");

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

        validateRandomTableData(model, continueButton);
    }

    private void validateRandomTableData(DefaultTableModel model, JButton continueButton) {
        for (int row = 0; row < model.getRowCount(); row++) {
            for (int col = 0; col < model.getColumnCount(); col++) {
                Object value = model.getValueAt(row, col);
                if (value == null || value.toString().trim().isEmpty()) {
                    continueButton.setEnabled(false);
                    return;
                }
            }
        }
        continueButton.setEnabled(true);
    }

    public void showUserInputScreen() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Border around the panel
    
        JLabel titleLabel = new JLabel("Enter Process Data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    
        // Table Model (Process ID, Arrival Time, Burst Time, Priority Number)
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Process ID column should NOT be editable
            }
        };
    
        // Initialize table with 3 rows (minimum requirement)
        JTable processTable = new JTable(tableModel);
        processTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        for (int i = 1; i <= 3; i++) {
            tableModel.addRow(new Object[]{"P" + i, "", "", ""});
        }
    
        JScrollPane scrollPane = new JScrollPane(processTable);
    
        // Add Process Button
        JButton addProcessButton = new JButton("Add Process");
        addProcessButton.addActionListener(e -> {
            int rowCount = tableModel.getRowCount();
            if (rowCount < 20) {
                tableModel.addRow(new Object[]{"P" + (rowCount + 1), "", "", ""});
            } else {
                JOptionPane.showMessageDialog(null, "Maximum of 20 processes reached!", "Limit Reached", JOptionPane.WARNING_MESSAGE);
            }
            validateUserInputTableData(tableModel, continueButton);
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
        continueButton.setEnabled(false); // Initially disabled
    
        continueButton.addActionListener(e -> {
            if (validateUserInputTableData(tableModel, continueButton)) {
                saveDataToFile(tableModel); // Save data before proceeding
                main.showSelectAlgorithmScreen(); // Proceed to the next screen
            }
        });
    
        // Create a separate panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(addProcessButton);
        buttonPanel.add(backButton);
        buttonPanel.add(continueButton);
    
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
    
        // Validate table whenever data changes
        tableModel.addTableModelListener(e -> validateUserInputTableData(tableModel, continueButton));
    
        mainPanel.add(panel, "UserInputScreen");
        layout.show(mainPanel, "UserInputScreen");
    }
    
    // Method to validate table data
    private boolean validateUserInputTableData(DefaultTableModel model, JButton continueButton) {
        HashSet<Integer> prioritySet = new HashSet<>();
        int rowCount = model.getRowCount();
    
        if (rowCount < 3) {
            continueButton.setEnabled(false);
            return false;
        }
    
        for (int row = 0; row < rowCount; row++) {
            try {
                String processID = model.getValueAt(row, 0).toString();
                int arrivalTime = Integer.parseInt(model.getValueAt(row, 1).toString());
                int burstTime = Integer.parseInt(model.getValueAt(row, 2).toString());
                int priority = Integer.parseInt(model.getValueAt(row, 3).toString());
    
                // Validate arrival time & burst time
                if (arrivalTime < 0 || arrivalTime > 30 || burstTime < 1 || burstTime > 30) {
                    continueButton.setEnabled(false);
                    return false;
                }
    
                // Validate priority (unique and between 1-20)
                if (priority < 1 || priority > 20 || !prioritySet.add(priority)) {
                    continueButton.setEnabled(false);
                    return false;
                }
    
                // Validate Process ID format (P1, P2, ..., P20)
                if (!processID.matches("P[1-9]|P1[0-9]|P20")) {
                    continueButton.setEnabled(false);
                    return false;
                }
            } catch (Exception ex) {
                continueButton.setEnabled(false);
                return false;
            }
        }
    
        continueButton.setEnabled(true);
        return true;
    }
    
    // Method to save data to a file
    private void saveDataToFile(DefaultTableModel model) {
        File file = new File("D://Documents//AlgoriSim//AlgoriSim//user_input_data.txt");
    
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (int row = 0; row < model.getRowCount(); row++) {
                String processID = model.getValueAt(row, 0).toString();
                String arrivalTime = model.getValueAt(row, 1).toString();
                String burstTime = model.getValueAt(row, 2).toString();
                String priority = model.getValueAt(row, 3).toString();
    
                writer.write(String.join(" ", processID, arrivalTime, burstTime, priority));
                writer.newLine();
            }
            writer.flush();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void showFileInputScreen() {
        JPanel filePanel = new JPanel(new BorderLayout());
        filePanel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
        JLabel titleLabel = new JLabel("Load from Text File", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    
        // Create Table with Uneditable Rows
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 3) { // Changed initial row count to 0
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table uneditable
            }
        };
    
        JTable table = new JTable(model);
        table.getTableHeader().setReorderingAllowed(false); // Prevent column reordering
        JScrollPane tableScrollPane = new JScrollPane(table);
    
        // Choose File Button
        JButton chooseFileButton = new JButton("Choose File");
        
        // Continue Button (Initially disabled)
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(100, 30));
        continueButton.setEnabled(false); // Initially disabled
    
        // Load file and enable continueButton when data is present
        chooseFileButton.addActionListener(e -> loadFileData(model, continueButton));
    
        // Back Button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
    
        // Continue Button action
        continueButton.addActionListener(e -> main.showSelectAlgorithmScreen());

        // Listen for table changes and update continue button accordingly
        model.addTableModelListener(e -> updateContinueButtonState(model, continueButton));
    
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
    private void loadFileData(DefaultTableModel model, JButton continueButton) {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(null);
    
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
    
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                List<String[]> rowData = new ArrayList<>();
    
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

                // ✅ Now update button state only after successfully adding rows
                updateContinueButtonState(model, continueButton);
    
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    // ===============================
    // 🔹 Method to Enable Continue Button if Data Exists
    // ===============================
    private void updateContinueButtonState(DefaultTableModel model, JButton continueButton) {
        continueButton.setEnabled(model.getRowCount() > 0);
    }
}