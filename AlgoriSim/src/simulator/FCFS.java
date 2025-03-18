import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
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
    private final Map<String, Color> processColors = new HashMap<>();
    private final List<Color> availableColors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timeline.isEmpty())
            return;

        int x = 30, y = 20, width = 50;

        for (int i = 0; i < timeline.size(); i++) {
            Event event = timeline.get(i);

            // Assign a unique color to each process
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
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton;
    private Timer timer;
    private List<Process> processes = new ArrayList<>();
    private int currentTime = 0, index = 0;
    private double avgWaitingTime = 0, avgTurnaroundTime = 0;
    private CustomPanel ganttChartPanel;
    private List<Event> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;

    public FCFS(CardLayout layout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        this.layout = layout;
        this.mainPanel = mainPanel;

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());

        // Home Button
        JButton homeButton = new JButton("← Home");
        homeButton.setFont(new Font("Arial", Font.BOLD, 14));
        homeButton.setPreferredSize(new Dimension(100, 30));

        // Home Button Action: Go back to Lobby
        homeButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        // Add Home Button to the Top Panel
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(new JLabel("Algorithm: FCFS", JLabel.CENTER), BorderLayout.CENTER);

        cpuLabel = new JLabel("CPU: Idle", SwingConstants.RIGHT);
        topPanel.add(cpuLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // === PROCESS TABLE ===
        String[] columnNames = {
                "Process ID", "Burst Time", "Arrival Time", "Priority Number",
                "Waiting Time", "Turnaround Time", "Avg Waiting Time", "Avg Turnaround Time"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        // === GANTT CHART PANEL ===
        ganttChartPanel = new CustomPanel();
        ganttChartPanel.setPreferredSize(new Dimension(700, 75));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        ganttChartPanel.setBackground(Color.WHITE);

        JScrollPane ganttScrollPane = new JScrollPane(ganttChartPanel);

        // Wrap Process Table + Gantt Chart in a new panel with BorderLayout
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
            processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
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

    private void startSimulation() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        index = 0;
        currentTime = 0;
        avgWaitingTime = 0;
        avgTurnaroundTime = 0;
        timeline.clear();
        ganttChartPanel.repaint();
        timer = new Timer(1000, e -> runFCFS());
        timer.start();
    }

    private void stopSimulation() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }

    private void runFCFS() {
        if (index >= processes.size()) {
            stopSimulation();
            return;
        }

        Process p = processes.get(index);
        p.startTime = Math.max(currentTime, p.arrivalTime);
        p.completionTime = p.startTime + p.burstTime;
        p.turnaroundTime = p.completionTime - p.arrivalTime;
        p.waitingTime = p.startTime - p.arrivalTime;
        currentTime = p.completionTime;

        avgWaitingTime += p.waitingTime;
        avgTurnaroundTime += p.turnaroundTime;

        updateUI(p);
        index++;

        if (index == processes.size()) {
            avgWaitingTime /= processes.size();
            avgTurnaroundTime /= processes.size();
            updateAverageTimes();
        }
    }

    private void updateAverageTimes() {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            tableModel.setValueAt(String.format("%.2f", avgWaitingTime), i, 6);
            tableModel.setValueAt(String.format("%.2f", avgTurnaroundTime), i, 7);
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
