import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Timer;
import java.util.List;

class ProcessRR {
    String processID;
    int arrivalTime, burstTime, remainingTime, completionTime, turnaroundTime, waitingTime;

    public ProcessRR(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

class EventRR {
    String processName;
    int startTime, finishTime;

    public EventRR(String processName, int startTime, int finishTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}

class CustomPanelRR extends JPanel {
    private List<EventRR> timeline = new ArrayList<>();
    private final Map<String, Color> processColors = new HashMap<>();
    private final List<Color> availableColors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timeline.isEmpty())
            return;

        int x = 30, y = 20, width = 30;
        for (int i = 0; i < timeline.size(); i++) {
            EventRR event = timeline.get(i);
            processColors.putIfAbsent(event.processName,
                    availableColors.get(processColors.size() % availableColors.size()));
            g.setColor(processColors.get(event.processName));
            g.fillRect(x, y, width, 30);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, 30);
            g.drawString(event.processName, x + 5, y + 20);
            g.drawString(Integer.toString(event.startTime), x - 5, y + 45);
            if (i == timeline.size() - 1) {
                g.drawString(Integer.toString(event.finishTime), x + 20, y + 45);
            }
            x += width;
        }
        setPreferredSize(new Dimension(x + 30, 75));
        revalidate();
    }

    public void setTimeline(List<EventRR> timeline) {
        this.timeline = timeline;
        repaint();
    }
}

public class RoundRobin extends JPanel {
    private Timer simulationTimer;
    private Queue<ProcessRR> readyQueue = new LinkedList<>();
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton;
    private JTextField quantumField;
    private List<ProcessRR> processes = new ArrayList<>();
    private int currentTime = 0, timeQuantum;
    private double avgWaitingTime = 0, avgTurnaroundTime = 0;
    private CustomPanelRR ganttChartPanel;
    private List<EventRR> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;
    private Image backgroundImage;
    private int simulationTime = 0;
    private JLabel simulationTimeLabel;

    public RoundRobin(CardLayout layout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        backgroundImage = new ImageIcon(CommonConstants.BG).getImage();

        this.layout = layout;
        this.mainPanel = mainPanel;

        JButton homeButton = createStyledButton(CommonConstants.homeDefault, CommonConstants.homeHover,
                CommonConstants.homeClicked);

        // Home Button Action: Go back to Lobby
        homeButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Algorithm: Round Robin", JLabel.RIGHT), BorderLayout.WEST);
        cpuLabel = new JLabel("Algorithm: Round Robin | CPU: Idle", SwingConstants.CENTER);
        topPanel.setOpaque(false);
        cpuLabel.setForeground(Color.WHITE);
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel(" ", SwingConstants.RIGHT);
        readyQueueLabel.setForeground(Color.WHITE);
        topPanel.add(readyQueueLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {
                "Process ID", "Burst Time", "Arrival Time",
                "Waiting Time", "Turnaround Time", "Avg Waiting Time", "Avg Turnaround Time"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        ganttChartPanel = new CustomPanelRR();
        ganttChartPanel.setPreferredSize(new Dimension(700, 75));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        ganttChartPanel.setBackground(Color.WHITE);

        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(ganttScrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        startButton = createStyledButton(CommonConstants.startDefault, CommonConstants.startHover,
                CommonConstants.startClicked);

        JLabel quantumLabel = new JLabel("Time Quantum:");
        quantumLabel.setForeground(Color.WHITE);
        quantumField = new JTextField("2", 5);

        startButton.addActionListener(e -> startSimulation());

        buttonPanel.add(quantumLabel);
        buttonPanel.add(quantumField);
        buttonPanel.add(startButton);

        buttonPanel.setOpaque(false);
        totalExecutionTimeLabel = new JLabel(" ");
        totalExecutionTimeLabel.setForeground(Color.WHITE);
        buttonPanel.add(totalExecutionTimeLabel);
        add(buttonPanel, BorderLayout.PAGE_END);

        loadProcessData();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
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
                    processes.add(new ProcessRR(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in file: " + filename);
                }
            }

            // Sort processes by arrival time
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
            displayProcesses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + filename, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProcesses() {
        tableModel.setRowCount(0);
        for (ProcessRR p : processes) {
            tableModel.addRow(new Object[] { p.processID, p.burstTime, p.arrivalTime, "-", "-", "-", "-" });
        }
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        currentTime = 0;
        simulationTime = 0;
        timeline.clear();
        ganttChartPanel.setTimeline(timeline);
        readyQueue.clear();

        for(ProcessRR p: processes) {
            p.completionTime = 0;
            p.remainingTime = p.burstTime; // Reset remaining time for each process
            p.waitingTime = 0; // Reset waiting time for each process
            p.turnaroundTime = 0; // Reset turnaround time for each process
        }

        timeQuantum = Integer.parseInt(quantumField.getText());
        List<ProcessRR> remainingProcesses = new ArrayList<>(processes);
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        simulationTimer = new Timer(500, e -> processNextQuantum(remainingProcesses)); // 500ms per step
        simulationTimer.start();
    }

    private void processNextQuantum(List<ProcessRR> remainingProcesses) {
        // Check if all processes are completed
        if (readyQueue.isEmpty() && remainingProcesses.isEmpty()) {
            simulationTimer.stop();
            stopSimulation();
            return;
        }

        // Add newly arrived processes to the ready queue
        while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
            readyQueue.add(remainingProcesses.remove(0));
        }

        // Build the Ready Queue display string
        StringBuilder readyQueueDisplay = new StringBuilder();
        for (ProcessRR p : readyQueue) {
            readyQueueDisplay.append(p.processID).append(" ");
        }

        if (readyQueue.isEmpty()) {
            // CPU is idle if no processes are ready
            timeline.add(new EventRR("Idle", currentTime, currentTime + 1));
            cpuLabel.setText("Algorithm: Round Robin | CPU: Idle | Ready Queue: Empty");
            currentTime++;
        } else {
            // Execute the next process in the queue
            ProcessRR executingProcess = readyQueue.poll();
            int executionTime = Math.min(timeQuantum, executingProcess.remainingTime);

            timeline.add(new EventRR(executingProcess.processID, currentTime, currentTime + executionTime));
            executingProcess.remainingTime -= executionTime;
            currentTime += executionTime;

            // Update CPU label to show the running process and Ready Queue
            cpuLabel.setText("Algorithm: Round Robin | CPU: " + executingProcess.processID +
                    " | Ready Queue: " + (readyQueue.isEmpty() ? "Empty" : readyQueueDisplay.toString()));

            // Add new arrivals after execution
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }

            // If the process still has remaining time, add it back to the queue
            if (executingProcess.remainingTime > 0) {
                readyQueue.add(executingProcess);
            } else {
                // Process is complete, calculate waiting & turnaround time
                executingProcess.completionTime = currentTime;
                executingProcess.turnaroundTime = executingProcess.completionTime - executingProcess.arrivalTime;
                executingProcess.waitingTime = executingProcess.turnaroundTime - executingProcess.burstTime;
            }
        }

        // Update the Gantt Chart title with the running time
        ganttChartPanel
                .setBorder(BorderFactory.createTitledBorder("Gantt Chart | Running Time: " + currentTime + " ms"));
        ganttChartPanel.setTimeline(timeline);
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        double totalWaitingTime = 0, totalTurnaroundTime = 0;

        for (ProcessRR p : processes) {
            totalWaitingTime += p.waitingTime;
            totalTurnaroundTime += p.turnaroundTime;
            tableModel.addRow(new Object[] {
                    p.processID, p.burstTime, p.arrivalTime, p.waitingTime, p.turnaroundTime, "-", "-"
            });
        }

        // Calculate averages
        int processCount = processes.size();
        double avgWaitingTime = (processCount > 0) ? totalWaitingTime / processCount : 0;
        double avgTurnaroundTime = (processCount > 0) ? totalTurnaroundTime / processCount : 0;

        String formattedAvgWaitingTime = String.format("%.2f", avgWaitingTime);
        String formattedAvgTurnaroundTime = String.format("%.2f", avgTurnaroundTime);

        // Add the averages row
        tableModel.addRow(
                new Object[] { "Averages", "-", "-", "-", "-", formattedAvgWaitingTime, formattedAvgTurnaroundTime });
    }

}
