import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;

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
    private JButton startButton, stopButton;
    private JTextField quantumField;
    private List<ProcessRR> processes = new ArrayList<>();
    private int currentTime = 0, timeQuantum;
    private double avgWaitingTime = 0, avgTurnaroundTime = 0;
    private CustomPanelRR ganttChartPanel;
    private List<EventRR> timeline = new ArrayList<>();

    public RoundRobin() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Algorithm: Round Robin", JLabel.LEFT), BorderLayout.WEST);
        cpuLabel = new JLabel("CPU: Idle", SwingConstants.CENTER);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel("Ready Queue: Empty", SwingConstants.RIGHT);
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
        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false);
        JLabel quantumLabel = new JLabel("Time Quantum:");
        quantumField = new JTextField("2", 5);

        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());

        buttonPanel.add(quantumLabel);
        buttonPanel.add(quantumField);
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
                if (data.length < 3)
                    continue;
                processes.add(new ProcessRR(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
            displayProcesses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file!", "Error", JOptionPane.ERROR_MESSAGE);
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
        stopButton.setEnabled(true);
        currentTime = 0;
        timeline.clear();
        ganttChartPanel.repaint();
        readyQueue.clear();

        timeQuantum = Integer.parseInt(quantumField.getText());
        List<ProcessRR> remainingProcesses = new ArrayList<>(processes);

        while (!remainingProcesses.isEmpty() || !readyQueue.isEmpty()) {
            while (!remainingProcesses.isEmpty() && remainingProcesses.get(0).arrivalTime <= currentTime) {
                readyQueue.add(remainingProcesses.remove(0));
            }

            if (readyQueue.isEmpty()) {
                timeline.add(new EventRR("Idle", currentTime, remainingProcesses.get(0).arrivalTime));
                currentTime = remainingProcesses.get(0).arrivalTime;
                continue;
            }

            ProcessRR executingProcess = readyQueue.poll();
            int executionTime = Math.min(timeQuantum, executingProcess.remainingTime);
            timeline.add(new EventRR(executingProcess.processID, currentTime, currentTime + executionTime));

            executingProcess.remainingTime -= executionTime;
            currentTime += executionTime;

            if (executingProcess.remainingTime > 0) {
                readyQueue.add(executingProcess);
            } else {
                executingProcess.completionTime = currentTime;
                executingProcess.turnaroundTime = executingProcess.completionTime - executingProcess.arrivalTime;
                executingProcess.waitingTime = executingProcess.turnaroundTime - executingProcess.burstTime;
            }
        }

        ganttChartPanel.setTimeline(timeline);
        stopSimulation();
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
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

        // Add the averages row
        tableModel.addRow(new Object[] { "Averages", "-", "-", "-", "-", avgWaitingTime, avgTurnaroundTime });
    }

}
