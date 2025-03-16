import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
            Color.CYAN, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY
    );

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timeline.isEmpty()) return;

        int x = 30, y = 20, width = 30;

        for (int i = 0; i < timeline.size(); i++) {
            EventSJFPreemptive event = timeline.get(i);

            // Assign a unique color to each process
            processColors.putIfAbsent(event.processName, availableColors.get(processColors.size() % availableColors.size()));
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
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton;
    private List<ProcessSJFPreemptive> processes = new ArrayList<>();
    private int currentTime = 0;
    private double avgWaitingTime = 0, avgTurnaroundTime = 0;
    private CustomPanelSJFPreemptive ganttChartPanel;
    private List<EventSJFPreemptive> timeline = new ArrayList<>();

    public SJFPreemptive(CardLayout layout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Algorithm: SJF Preemptive", JLabel.LEFT), BorderLayout.WEST);
        cpuLabel = new JLabel("CPU: Idle", SwingConstants.CENTER);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel("Ready Queue: Empty", SwingConstants.RIGHT);
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
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        totalExecutionTimeLabel = new JLabel("Total Execution Time: 0 ms");
        buttonPanel.add(totalExecutionTimeLabel);
        add(buttonPanel, BorderLayout.PAGE_END);

        loadProcessData();
    }

    private void loadProcessData() {
        try (BufferedReader br = new BufferedReader(new FileReader("random_data.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.trim().split("\\s+");
                if (data.length < 3) continue;
                processes.add(new ProcessSJFPreemptive(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
            displayProcesses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProcesses() {
        tableModel.setRowCount(0);
        for (ProcessSJFPreemptive p : processes) {
            tableModel.addRow(new Object[]{p.processID, p.burstTime, p.arrivalTime, "-", "-", "-", "-"});
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

        runSJFPreemptive();
        updateTable();
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void runSJFPreemptive() {
        List<ProcessSJFPreemptive> readyQueue = new ArrayList<>();
        ProcessSJFPreemptive executingProcess = null;
        int lastSwitchTime = 0;
    
        while (true) {
            // Add newly arrived processes at the correct time
            for (ProcessSJFPreemptive p : processes) {
                if (p.arrivalTime <= currentTime && !readyQueue.contains(p) && !p.isCompleted) {
                    readyQueue.add(p);
                }
            }
    
            // Remove completed processes
            readyQueue.removeIf(p -> p.isCompleted);
    
            // If no process is available, move time forward
            if (readyQueue.isEmpty()) {
                if (processes.stream().allMatch(p -> p.isCompleted)) {
                    break; // All processes done
                }
                currentTime++; // Wait for the next process to arrive
                continue;
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
    
            // Mark process as completed
            if (executingProcess.remainingTime == 0) {
                executingProcess.isCompleted = true;
                executingProcess.completionTime = currentTime;
                executingProcess.turnaroundTime = executingProcess.completionTime - executingProcess.arrivalTime;
                executingProcess.waitingTime = executingProcess.turnaroundTime - executingProcess.burstTime;
            }
        }
    
        // Final update for Gantt chart
        if (executingProcess != null) {
            timeline.add(new EventSJFPreemptive(executingProcess.processID, lastSwitchTime, currentTime));
        }
    
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
            tableModel.addRow(new Object[]{
                p.processID, p.burstTime, p.arrivalTime, 
                p.waitingTime, p.turnaroundTime, avgWaitingTime, avgTurnaroundTime
            });
        }
    }
    
}
