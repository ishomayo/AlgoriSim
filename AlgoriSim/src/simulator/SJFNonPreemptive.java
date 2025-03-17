import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.Timer;

class ProcessSJF {
    String processID;
    int arrivalTime, burstTime, completionTime, turnaroundTime, waitingTime;
    boolean isCompleted = false;

    public ProcessSJF(String processID, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }
}

class EventSJF {
    String processName;
    int startTime, finishTime;

    public EventSJF(String processName, int startTime, int finishTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}

class CustomPanelSJF extends JPanel {
    private List<EventSJF> timeline = new ArrayList<>();
    private final Map<String, Color> processColors = new HashMap<>();
    private final List<Color> availableColors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timeline.isEmpty()) return;

        int x = 30, y = 20, width = 30;

        for (int i = 0; i < timeline.size(); i++) {
            EventSJF event = timeline.get(i);

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

    public void setTimeline(List<EventSJF> timeline) {
        this.timeline = timeline;
        repaint();
    }
}

public class SJFNonPreemptive extends JPanel {
    private Timer simulationTimer;
    private List<ProcessSJF> processes = new ArrayList<>();
    private AtomicInteger currentTime = new AtomicInteger(0);
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton;
    private CustomPanelSJF ganttChartPanel;
    private List<EventSJF> timeline = new ArrayList<>();

    public SJFNonPreemptive(CardLayout layout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Algorithm: SJF Non-Preemptive", JLabel.LEFT), BorderLayout.WEST);
        cpuLabel = new JLabel("CPU: Idle", SwingConstants.CENTER);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel("Ready Queue: Empty", SwingConstants.RIGHT);
        topPanel.add(readyQueueLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = { "Process ID", "Burst Time", "Arrival Time",
                "Waiting Time", "Turnaround Time", "Avg Waiting Time", "Avg Turnaround Time" };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        ganttChartPanel = new CustomPanelSJF();
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
                processes.add(new ProcessSJF(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
            }
            displayProcesses();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayProcesses() {
        tableModel.setRowCount(0);
        for (ProcessSJF p : processes) {
            tableModel.addRow(new Object[] { p.processID, p.burstTime, p.arrivalTime, "-", "-", "-", "-" });
        }
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        currentTime.set(0);
        timeline.clear();
        ganttChartPanel.repaint();

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));

        for (ProcessSJF p : processes) {
            if (currentTime.get() < p.arrivalTime) {
                currentTime.set(p.arrivalTime);
            }
            int start = currentTime.get();
            int finish = start + p.burstTime;
            timeline.add(new EventSJF(p.processID, start, finish));

            p.completionTime = finish;
            p.turnaroundTime = finish - p.arrivalTime;
            p.waitingTime = p.turnaroundTime - p.burstTime;

            currentTime.set(finish);
        }

        ganttChartPanel.setTimeline(timeline);
        updateTable();
        stopButton.setEnabled(false);
        startButton.setEnabled(true);
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void updateTable() {
        double avgWaitingTime = processes.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double avgTurnaroundTime = processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);

        tableModel.setRowCount(0);
        for (ProcessSJF p : processes) {
            tableModel.addRow(new Object[] { p.processID, p.burstTime, p.arrivalTime,
                    p.waitingTime, p.turnaroundTime, avgWaitingTime, avgTurnaroundTime });
        }
    }
}
