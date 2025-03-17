import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.*;

import java.util.*;
import java.util.List;

public class DataInputScreen extends JPanel  {
    private JPanel mainPanel;
    private CardLayout layout;
    private JButton continueButton; // Store as instance variable
    private Main main;
    private Image backgroundImage;

    public DataInputScreen(Main main, CardLayout layout, JPanel mainPanel) {
        this.main = main;
        this.layout = layout;
        this.mainPanel = mainPanel;
        this.continueButton = continueButton;

        backgroundImage = new ImageIcon("C:\\Users\\Eugene\\Desktop\\Git\\AlgoriSim\\AlgoriSim\\src\\simulator\\resources\\DataInputScreen.jpg").getImage();  // Replace with your image path
   
        showRandomDataScreen();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    // Data Input Selection screen
    public void showDataInputSelection() {
        
        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (backgroundImage != null) {
                    g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
                }
            }
        };

        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setOpaque(false);

        JButton randomButton = createStyledButtonDATAINPUT(CommonConstants.randomDefault, CommonConstants.randomClicked);
        JButton userInputButton = createStyledButtonDATAINPUT(CommonConstants.userinpDefault, CommonConstants.userinpClicked);
        JButton fileInputButton = createStyledButtonDATAINPUT(CommonConstants.fileDefault, CommonConstants.fileClicked);

        buttonPanel.setBorder(new EmptyBorder(150, 0, 0, 0)); // Adjust the first value (top padding)

        buttonPanel.add(randomButton);
        buttonPanel.add(userInputButton);
        buttonPanel.add(fileInputButton);
        

        JButton backButton = createStyledButtonBUTTON(CommonConstants.backDefault, CommonConstants.backClicked);

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

    private static JButton createStyledButtonDATAINPUT(String defaultIconPath,String clickIconPath) {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(200, 200));
    
        // Load and scale the images
        ImageIcon defaultIcon = scaleImage(defaultIconPath, button.getPreferredSize());
        ImageIcon clickIcon = scaleImage(clickIconPath, button.getPreferredSize());
    
        button.setIcon(defaultIcon);
    
        button.addMouseListener(new MouseAdapter() {
    
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(defaultIcon);
            }
    
            @Override
            public void mousePressed(MouseEvent e) {
                button.setIcon(clickIcon);
            }
        });
    
        return button;
    }

    private static JButton createStyledButtonBUTTON(String defaultIconPath,String clickIconPath) {
        JButton button = new JButton();
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setPreferredSize(new Dimension(150, 50));
    
        // Load and scale the images
        ImageIcon defaultIcon = scaleImage(defaultIconPath, button.getPreferredSize());
        ImageIcon clickIcon = scaleImage(clickIconPath, button.getPreferredSize());
    
        button.setIcon(defaultIcon);
    
        button.addMouseListener(new MouseAdapter() {
    
            @Override
            public void mouseExited(MouseEvent e) {
                button.setIcon(defaultIcon);
            }
    
            @Override
            public void mousePressed(MouseEvent e) {
                button.setIcon(clickIcon);
            }
        });
    
        return button;
    }

    private static ImageIcon scaleImage(String imagePath, Dimension size) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(size.width, size.height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    private void showRandomDataScreen() {
        JPanel randomDataPanel = new JPanel(new BorderLayout());
        randomDataPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
        // Title Label
        JLabel titleLabel = new JLabel("Generate Random Data", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
    
        // Create Table with Uneditable Rows
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", "Priority Number"};
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 3) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    
        JTable dataTable = new JTable(tableModel);
        dataTable.getTableHeader().setReorderingAllowed(false);
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
    
        // Generate Data Button
        JButton generateButton = new JButton("Generate Data");
    
        // Continue Button (Initially disabled)
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(100, 30));
        continueButton.setEnabled(false);
    
        // Generate data and validate table when clicked
        generateButton.addActionListener(e -> {
            generateRandomData(tableModel);
            validateRandomTableData(tableModel, continueButton);
        });
    
        // Back Button
        JButton backButton = new JButton("←");
        backButton.setFont(new Font("Arial", Font.PLAIN, 12));
        backButton.setPreferredSize(new Dimension(50, 30));
        backButton.addActionListener(e -> layout.show(mainPanel, "DataInputSelection"));
    
        // Continue Button action
        continueButton.addActionListener(e -> main.showSelectAlgorithmScreen());
    
        // Listen for table changes and update continue button accordingly
        tableModel.addTableModelListener(e -> validateRandomTableData(tableModel, continueButton));
    
        // Panel Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
    
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(generateButton);
        bottomPanel.add(backButton);
        bottomPanel.add(continueButton);
    
        // Add components
        randomDataPanel.add(titleLabel, BorderLayout.NORTH);
        randomDataPanel.add(centerPanel, BorderLayout.CENTER);
        randomDataPanel.add(bottomPanel, BorderLayout.SOUTH);
    
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
        File file = new File("random_data.txt");

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
        JPanel userInputPanel = new JPanel(new BorderLayout());
        userInputPanel.setBorder(new EmptyBorder(50, 50, 50, 50));
    
        // Title Label
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
    
        // Initialize table with 3 rows
        JTable processTable = new JTable(tableModel);
        processTable.getTableHeader().setReorderingAllowed(false);
    
        for (int i = 1; i <= 3; i++) {
            tableModel.addRow(new Object[]{"P" + i, "", "", ""});
        }
    
        JScrollPane tableScrollPane = new JScrollPane(processTable);
    
        // Add Process Button
        JButton addProcessButton = new JButton("Add Process");
    
        // Continue Button (Initially disabled)
        JButton continueButton = new JButton("Continue");
        continueButton.setFont(new Font("Arial", Font.BOLD, 14));
        continueButton.setPreferredSize(new Dimension(100, 30));
        continueButton.setEnabled(false);
    
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
    
        // Continue Button Action
        continueButton.addActionListener(e -> {
            if (validateUserInputTableData(tableModel, continueButton)) {
                saveDataToFile(tableModel);
                main.showSelectAlgorithmScreen();
            }
        });
    
        // Panel Layout
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
    
        JPanel bottomPanel = new JPanel(new FlowLayout());
        bottomPanel.add(addProcessButton);
        bottomPanel.add(backButton);
        bottomPanel.add(continueButton);
    
        // Add Components
        userInputPanel.add(titleLabel, BorderLayout.NORTH);
        userInputPanel.add(centerPanel, BorderLayout.CENTER);
        userInputPanel.add(bottomPanel, BorderLayout.SOUTH);
    
        // Validate table whenever data changes
        tableModel.addTableModelListener(e -> validateUserInputTableData(tableModel, continueButton));
    
        mainPanel.add(userInputPanel, "UserInputScreen");
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
        File file = new File("data.txt");
    
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