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

class ProcessSJFPreemptive {
    String processID;
    int arrivalTime, burstTime, remainingTime, completionTime, turnaroundTime, waitingTime;
    boolean isCompleted = false;

    public ProcessSJFPreemptive(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
    }
}

class EventSJFPreemptive {
    String processName;
    int startTime, finishTime;

    public EventSJFPreemptive(String processName, int startTime, int finishTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}

class CustomPanelSJFPreemptive extends JPanel {
    private List<EventSJFPreemptive> timeline = new ArrayList<>();
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
            EventSJFPreemptive event = timeline.get(i);

            // Assign a unique color to each process
            processColors.putIfAbsent(event.processName,
                    availableColors.get(processColors.size() % availableColors.size()));
            g.setColor(processColors.get(event.processName));

            // Fill rectangle with color
            g.fillRect(x, y, width, 30);

            // Draw border around block
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, 30);

            // Draw process name inside the block
            g.drawString(event.processName, x + 5, y + 20);

            // Draw start time
            g.drawString(Integer.toString(event.startTime), x - 5, y + 45);

            // Draw finish time for last event
            if (i == timeline.size() - 1) {
                g.drawString(Integer.toString(event.finishTime), x + 20, y + 45);
            }

            x += width;
        }

        setPreferredSize(new Dimension(x + 30, 75));
        revalidate();
    }

    public void setTimeline(List<EventSJFPreemptive> timeline) {
        this.timeline = timeline;
        repaint();
    }
}

public class SJFPreemptive extends JPanel {
    private Image backgroundImage;
    private Timer simulationTimer;
    private List<ProcessSJFPreemptive> readyQueue = new ArrayList<>();
    private ProcessSJFPreemptive executingProcess = null;
    private int lastSwitchTime = 0;
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton;
    private List<ProcessSJFPreemptive> processes = new ArrayList<>();
    private int currentTime = 0;
    private double avgWaitingTime = 0, avgTurnaroundTime = 0;
    private CustomPanelSJFPreemptive ganttChartPanel;
    private List<EventSJFPreemptive> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;

    public SJFPreemptive(CardLayout layout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        backgroundImage = new ImageIcon(CommonConstants.BG).getImage();

        this.layout = layout;
        this.mainPanel = mainPanel;

        JButton homeButton = createStyledButton(CommonConstants.homeDefault, CommonConstants.homeHover,
                CommonConstants.homeClicked);

        // Home Button Action: Go back to Lobby
        homeButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Algorithm: SJF Preemptive", JLabel.LEFT), BorderLayout.WEST);
        topPanel.setOpaque(false);
        cpuLabel = new JLabel("CPU: Idle", SwingConstants.CENTER);
        cpuLabel.setForeground(Color.WHITE);
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel("Ready Queue: Empty", SwingConstants.RIGHT);
        readyQueueLabel.setForeground(Color.WHITE);
        topPanel.add(readyQueueLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // === PROCESS TABLE ===
        String[] columnNames = {
                "Process ID", "Burst Time", "Arrival Time",
                "Waiting Time", "Turnaround Time", "Avg Waiting Time", "Avg Turnaround Time"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        // === GANTT CHART PANEL ===
        ganttChartPanel = new CustomPanelSJFPreemptive();
        ganttChartPanel.setPreferredSize(new Dimension(700, 75));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        ganttChartPanel.setBackground(Color.WHITE);

        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(tableScrollPane, BorderLayout.CENTER);
        centerPanel.add(ganttScrollPane, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);

        // === BUTTONS ===
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        startButton = createStyledButton(CommonConstants.startDefault, CommonConstants.startHover,
                CommonConstants.startClicked);
        stopButton = createStyledButton(CommonConstants.stopDefault, CommonConstants.stopHover,
                CommonConstants.stopClicked);
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        totalExecutionTimeLabel = new JLabel("Total Execution Time: 0 ms");
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
                    processes.add(
                            new ProcessSJFPreemptive(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
                } catch (NumberFormatException e) {
                    System.err.println("Invalid number format in file: " + filename);
                }
            }
            displayProcesses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + filename, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProcesses() {
        tableModel.setRowCount(0);
        for (ProcessSJFPreemptive p : processes) {
            tableModel.addRow(new Object[] { p.processID, p.burstTime, p.arrivalTime, "-", "-", "-", "-" });
        }
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        currentTime = 0;
        avgWaitingTime = 0;
        avgTurnaroundTime = 0;
        timeline.clear();
        ganttChartPanel.repaint();
        readyQueue.clear();
        executingProcess = null;
        lastSwitchTime = 0;

        // Reset processes
        for (ProcessSJFPreemptive p : processes) {
            p.remainingTime = p.burstTime;
            p.isCompleted = false;
        }

        // Precompute the entire scheduling (fills `timeline`)
        precomputeSJFPreemptive();

        // Update table instantly
        updateTable();

        // Animate the Gantt chart faster (100ms per event)
        int[] eventIndex = { 0 }; // Use an array to modify inside lambda
        simulationTimer = new Timer(100, e -> {
            if (eventIndex[0] < timeline.size()) {
                ganttChartPanel.setTimeline(timeline.subList(0, eventIndex[0] + 1));
                cpuLabel.setText("CPU: " + timeline.get(eventIndex[0]).processName);
                eventIndex[0]++;
            } else {
                simulationTimer.stop();
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
            }
        });

        simulationTimer.start();
    }

    private void precomputeSJFPreemptive() {
        List<ProcessSJFPreemptive> remainingProcesses = new ArrayList<>(processes);
        remainingProcesses.sort(Comparator.comparingInt(p -> p.arrivalTime));

        readyQueue.clear();
        executingProcess = null;
        lastSwitchTime = 0;
        currentTime = 0;
        timeline.clear();

        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            // Add newly arrived processes to ready queue
            Iterator<ProcessSJFPreemptive> iter = remainingProcesses.iterator();
            while (iter.hasNext()) {
                ProcessSJFPreemptive p = iter.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    iter.remove();
                }
            }

            // Remove completed processes from the ready queue
            readyQueue.removeIf(p -> p.isCompleted);

            // If no process is available, move time forward
            if (readyQueue.isEmpty()) {
                if (remainingProcesses.isEmpty())
                    break;
                currentTime++;
                continue;
            }

            // Select the shortest job
            ProcessSJFPreemptive shortestJob = readyQueue.stream()
                    .min(Comparator.comparingInt(p -> p.remainingTime))
                    .orElse(null);

            // Context switch if needed
            if (executingProcess != shortestJob) {
                if (executingProcess != null) {
                    timeline.add(new EventSJFPreemptive(executingProcess.processID, lastSwitchTime, currentTime));
                }
                executingProcess = shortestJob;
                lastSwitchTime = currentTime;
            }

            // Execute process for 1 unit of time
            executingProcess.remainingTime--;
            currentTime++;

            // If process is completed, finalize its values
            if (executingProcess.remainingTime == 0) {
                executingProcess.isCompleted = true;
                executingProcess.completionTime = currentTime;
                executingProcess.turnaroundTime = executingProcess.completionTime - executingProcess.arrivalTime;
                executingProcess.waitingTime = executingProcess.turnaroundTime - executingProcess.burstTime;
            }
        }

        // Final timeline update
        if (executingProcess != null) {
            timeline.add(new EventSJFPreemptive(executingProcess.processID, lastSwitchTime, currentTime));
        }
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
        if (simulationTimer != null) {
            simulationTimer.stop();
        }

        // Final update for Gantt chart
        if (executingProcess != null) {
            timeline.add(new EventSJFPreemptive(executingProcess.processID, lastSwitchTime, currentTime));
        }

        ganttChartPanel.setTimeline(timeline);
        updateTable();
    }

    private void runSJFPreemptiveStep() {
        // Add newly arrived processes to the ready queue
        for (ProcessSJFPreemptive p : processes) {
            if (p.arrivalTime == currentTime && !p.isCompleted) {
                readyQueue.add(p);
            }
        }

        // Remove completed processes
        readyQueue.removeIf(p -> p.isCompleted);

        // If no process is available, move time forward
        if (readyQueue.isEmpty()) {
            if (processes.stream().allMatch(p -> p.isCompleted)) {
                stopSimulation();
                return;
            }
            currentTime++;
            return;
        }

        // Select the shortest job (preemptive)
        ProcessSJFPreemptive shortestJob = readyQueue.stream()
                .min(Comparator.comparingInt(p -> p.remainingTime))
                .orElse(null);

        // Preempt if a new shortest job arrives
        if (executingProcess != shortestJob) {
            if (executingProcess != null) {
                timeline.add(new EventSJFPreemptive(executingProcess.processID, lastSwitchTime, currentTime));
            }
            executingProcess = shortestJob;
            lastSwitchTime = currentTime;
        }

        // Execute process for 1 time unit
        executingProcess.remainingTime--;
        currentTime++;

        // Update CPU Label
        cpuLabel.setText("CPU: " + executingProcess.processID);

        // Mark process as completed
        if (executingProcess.remainingTime == 0) {
            executingProcess.isCompleted = true;
            executingProcess.completionTime = currentTime;
            executingProcess.turnaroundTime = executingProcess.completionTime - executingProcess.arrivalTime;
            executingProcess.waitingTime = executingProcess.turnaroundTime - executingProcess.burstTime;
        }

        // Update UI
        ganttChartPanel.setTimeline(timeline);
        updateTable();
    }

    private void updateTable() {
        avgWaitingTime = 0;
        avgTurnaroundTime = 0;

        for (ProcessSJFPreemptive p : processes) {
            avgWaitingTime += p.waitingTime;
            avgTurnaroundTime += p.turnaroundTime;
        }

        avgWaitingTime /= processes.size();
        avgTurnaroundTime /= processes.size();

        tableModel.setRowCount(0);
        for (ProcessSJFPreemptive p : processes) {
            tableModel.addRow(new Object[] {
                    p.processID, p.burstTime, p.arrivalTime,
                    p.waitingTime, p.turnaroundTime, avgWaitingTime, avgTurnaroundTime
            });
        }
    }

}
