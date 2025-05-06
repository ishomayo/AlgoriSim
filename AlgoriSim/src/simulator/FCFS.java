import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;

class Process {
    String processID;
    int arrivalTime, burstTime, completionTime, turnaroundTime, waitingTime, startTime;
    String priorityNumber; // Placeholder (FCFS does not use priority)

    public Process(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priorityNumber = "-"; // Default value
    }
}

class Event {
    String processName;
    int startTime, finishTime;

    public Event(String processName, int startTime, int finishTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}

class CustomPanel extends JPanel {
    private List<Event> timeline = new ArrayList<>();
    private static final Map<String, Color> processColors = new HashMap<>();
    private static final List<Color> availableColors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY,
            Color.WHITE, Color.DARK_GRAY, new Color(128, 0, 128), // Purple
            new Color(255, 165, 0), // Deep Orange
            new Color(0, 128, 0), // Dark Green
            new Color(75, 0, 130), // Indigo
            new Color(255, 105, 180), // Hot Pink
            new Color(139, 69, 19), // Saddle Brown
            new Color(0, 191, 255), // Deep Sky Blue
            new Color(47, 79, 79) // Dark Slate Gray
    );

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timeline.isEmpty())
            return;

        int x = 30, y = 20, width = 50;

        for (int i = 0; i < timeline.size(); i++) {
            Event event = timeline.get(i);

            // Assign a unique persistent color to each process
            if (!processColors.containsKey(event.processName)) {
                processColors.put(event.processName,
                        availableColors.get(processColors.size() % availableColors.size()));
            }
            g.setColor(processColors.get(event.processName));

            // Fill rectangle with color
            g.fillRect(x, y, width, 30);

            // Draw border around block
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, 30);

            // Draw process name inside the block
            g.drawString(event.processName, x + 10, y + 20);

            // Draw start time
            g.drawString(Integer.toString(event.startTime), x - 5, y + 45);

            // Draw finish time for last event
            if (i == timeline.size() - 1) {
                g.drawString(Integer.toString(event.finishTime), x + 30, y + 45);
            }

            x += width;
        }

        setPreferredSize(new Dimension(x + 30, 75));
        revalidate();
    }

    public void setTimeline(List<Event> timeline) {
        this.timeline = timeline;
        repaint();
    }
}

public class FCFS extends JPanel {
    private JLabel cpuLabel, readyQueueLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton;
    private Timer timer;
    private List<Process> processes = new ArrayList<>();
    private int currentTime = 0, index = 0;
    private double avgWaitingTime = 0, avgTurnaroundTime = 0;
    private CustomPanel ganttChartPanel;
    private List<Event> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;
    private JLabel timerLabel;

    class BackgroundPanel extends JPanel {
        private Image bgImage;

        public BackgroundPanel(String imagePath) {
            try {
                bgImage = new ImageIcon(imagePath).getImage();
            } catch (Exception e) {
                System.err.println("Error loading background image: " + e.getMessage());
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (bgImage != null) {
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

    public FCFS(CardLayout layout, JPanel mainPanel) {
        super(new BorderLayout()); // Remove setLayout and setBorder (handled by BackgroundPanel)

        // Create background panel
        BackgroundPanel bgPanel = new BackgroundPanel(CommonConstants.BG); // Set your image path
        bgPanel.setLayout(new BorderLayout(10, 10)); // Use layout to add components
        bgPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        this.layout = layout;
        this.mainPanel = mainPanel;

        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false); // Makes it transparent to show background

        JButton homeButton = createStyledButton(CommonConstants.homeDefault, CommonConstants.homeHover,
                CommonConstants.homeClicked);
        homeButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        topPanel.add(homeButton, BorderLayout.WEST);

        cpuLabel = new JLabel("Algorithm: FCFS | CPU: Idle", SwingConstants.CENTER);
        cpuLabel.setForeground(Color.WHITE); // Set font color to white
        topPanel.add(cpuLabel, BorderLayout.EAST);

        bgPanel.add(topPanel, BorderLayout.NORTH);

        // Process Table and Gantt Chart Panel
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);

        String[] columnNames = {
                "Process ID", "Burst Time", "Arrival Time", "Priority Number",
                "Waiting Time", "Turnaround Time", "Avg Waiting Time", "Avg Turnaround Time"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);
        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        ganttChartPanel = new CustomPanel();
        ganttChartPanel.setPreferredSize(new Dimension(700, 75));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart | Running Time: 0 ms"));
        ganttChartPanel.setBackground(Color.WHITE);
        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);

        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(ganttScrollPane, BorderLayout.SOUTH);

        bgPanel.add(centerPanel, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);

        startButton = createStyledButton(CommonConstants.startDefault, CommonConstants.startHover,
                CommonConstants.startClicked);

        startButton.addActionListener(e -> startSimulation());

        timerLabel = new JLabel(" ");
        timerLabel.setForeground(Color.WHITE);

        buttonPanel.add(startButton);
        buttonPanel.add(timerLabel);

        bgPanel.add(buttonPanel, BorderLayout.PAGE_END);

        // Set the background panel as the main component
        add(bgPanel);

        loadProcessData();
    }

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

    private void loadProcessData() {
        String filename;

        switch (DataInputScreen.checker) {
            case 1:
                filename = "random_data.txt";
                break;
            case 2:
                filename = "data.txt";
                break;
            case 3:
                filename = "file_input.txt";
                break;
            default:
                JOptionPane.showMessageDialog(this, "Invalid data source!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
        }

        // Check if the file exists and is not empty
        File file = new File(filename);
        if (!file.exists() || file.length() == 0) {
            JOptionPane.showMessageDialog(this, "Selected file is missing or empty!", "Warning",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        processes.clear();
        readFileAndLoadProcesses(filename);
    }

    private void readFileAndLoadProcesses(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.trim().split("\\s+");
                if (data.length < 3)
                    continue;

                try {
                    processes.add(new Process(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in file: " + filename);
                }
            }

            // Sort by arrival time
            // processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
            displayProcesses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + filename, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProcesses() {
        tableModel.setRowCount(0);
        for (Process p : processes) {
            tableModel.addRow(new Object[] {
                    p.processID, p.burstTime, p.arrivalTime, p.priorityNumber, "-", "-", "-", "-"
            });
        }
    }

    private int simIndex = 0;
    private List<Process> simulationOrder;

    private void startSimulation() {
        resetSimulation();
        currentTime = 0;
        avgWaitingTime = 0;
        avgTurnaroundTime = 0;
        timeline.clear();
        ganttChartPanel.repaint();

        // Make a sorted list for simulation order (arrival time)
        simulationOrder = new ArrayList<>(processes);
        simulationOrder.sort(Comparator.comparingInt(p -> p.arrivalTime));

        simIndex = 0;
        timer = new Timer(1000, e -> simulateStep()); // 1000 ms = 1 sec per process
        timer.start();
    }

    private boolean isAverageRowAdded = false;

    private void resetSimulation() {
        if (timer != null && timer.isRunning()) {
            timer.stop();
        }

        simIndex = 0;
        currentTime = 0;
        avgWaitingTime = 0;
        avgTurnaroundTime = 0;
        timeline.clear();

        // Reset table values (waiting time and turnaround time)
        for (int i = 0; i < processes.size(); i++) {
            tableModel.setValueAt("", i, 4); // Waiting Time
            tableModel.setValueAt("", i, 5); // Turnaround Time
        }

        // Repaint Gantt chart
        ganttChartPanel.setTimeline(timeline);
        ganttChartPanel.repaint();

        // Re-enable start button
        startButton.setEnabled(true);
        isAverageRowAdded = false;
    }

    private void simulateStep() {
        if (simIndex >= simulationOrder.size()) {
            timer.stop();
            updateAverageTimes();
            return;
        }

        Process p = simulationOrder.get(simIndex);

        p.startTime = Math.max(currentTime, p.arrivalTime);
        p.completionTime = p.startTime + p.burstTime;
        p.turnaroundTime = p.completionTime - p.arrivalTime;
        p.waitingTime = p.startTime - p.arrivalTime;
        currentTime = p.completionTime;

        avgWaitingTime += p.waitingTime;
        avgTurnaroundTime += p.turnaroundTime;

        timeline.add(new Event(p.processID, p.startTime, p.completionTime));
        ganttChartPanel.setTimeline(timeline);
        ganttChartPanel.repaint();

        // Update values in table by matching process ID
        for (int i = 0; i < processes.size(); i++) {
            if (processes.get(i).processID.equals(p.processID)) {
                tableModel.setValueAt(p.waitingTime, i, 4);
                tableModel.setValueAt(p.turnaroundTime, i, 5);
                break;
            }
        }

        simIndex++;
    }

    private void stopSimulation() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        startButton.setEnabled(true);
    }

    private void runFCFS() {
        if (index >= processes.size()) {
            stopSimulation();
            return;
        }

        // Build the Ready Queue display string
        StringBuilder readyQueueDisplay = new StringBuilder();
        for (int i = index + 1; i < processes.size(); i++) {
            readyQueueDisplay.append(processes.get(i).processID).append(" ");
        }

        Process p = processes.get(index);
        p.startTime = Math.max(currentTime, p.arrivalTime);
        p.completionTime = p.startTime + p.burstTime;
        p.turnaroundTime = p.completionTime - p.arrivalTime;
        p.waitingTime = p.startTime - p.arrivalTime;
        currentTime = p.completionTime;

        avgWaitingTime += p.waitingTime;
        avgTurnaroundTime += p.turnaroundTime;

        // Update CPU label to show running process and Ready Queue
        cpuLabel.setText("Algorithm: FCFS | CPU: " + p.processID +
                " | Ready Queue: " + (readyQueueDisplay.length() == 0 ? "Empty" : readyQueueDisplay.toString()));

        ganttChartPanel
                .setBorder(BorderFactory.createTitledBorder("Gantt Chart | Running Time: " + currentTime + " ms"));

        updateUI(p);
        index++;

        if (index == processes.size()) {
            updateAverageTimes();
        }
    }

    private void updateAverageTimes() {
        avgWaitingTime /= processes.size();
        avgTurnaroundTime /= processes.size();

        // Only add the average row if it has not been added already
        if (!isAverageRowAdded) {
            tableModel.addRow(new Object[] {
                    "Average", "-", "-", "-", "-", "-",
                    String.format("%.2f", avgWaitingTime),
                    String.format("%.2f", avgTurnaroundTime)
            });

            isAverageRowAdded = true; // Set flag to true to prevent adding it again
        }
    }

    private void updateUI(Process p) {
        updateGanttChart(p);
        updateTable();
    }

    private void updateTable() {
        for (int i = 0; i < processes.size(); i++) {
            Process p = processes.get(i);
            tableModel.setValueAt(p.waitingTime, i, 4);
            tableModel.setValueAt(p.turnaroundTime, i, 5);
        }
    }

    private void updateGanttChart(Process p) {
        timeline.add(new Event(p.processID, p.startTime, p.completionTime));
        ganttChartPanel.setTimeline(timeline);
    }
}
