import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Queue;
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
    private JButton startButton;
    private CustomPanelSJF ganttChartPanel;
    private List<EventSJF> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;
    private Image backgroundImage;

    public SJFNonPreemptive(CardLayout layout, JPanel mainPanel) {
        backgroundImage = new ImageIcon(CommonConstants.BG).getImage(); // Replace with your image file

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        this.layout = layout;
        this.mainPanel = mainPanel;

        JButton homeButton = createStyledButton(CommonConstants.homeDefault, CommonConstants.homeHover,
                CommonConstants.homeClicked);

        // Home Button Action: Go back to Lobby
        homeButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Algorithm: SJF Non-Preemptive", JLabel.LEFT), BorderLayout.WEST);
        cpuLabel = new JLabel("Algorithm: SJF Non-Preemptive", SwingConstants.CENTER);
        cpuLabel.setForeground(Color.WHITE);
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel(" ", SwingConstants.RIGHT);
        readyQueueLabel.setForeground(Color.WHITE);
        topPanel.add(readyQueueLabel, BorderLayout.EAST);
        topPanel.setOpaque(false);
        topPanel.setBackground(new Color(0, 0, 0, 0));

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
        startButton = createStyledButton(CommonConstants.startDefault, CommonConstants.startHover,
                CommonConstants.startClicked);

        startButton.addActionListener(e -> startSimulation());

        buttonPanel.add(startButton);
        totalExecutionTimeLabel = new JLabel(" ");
        totalExecutionTimeLabel.setForeground(Color.WHITE);
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

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

    // Helper method for button styling
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
                    processes.add(new ProcessSJF(data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2])));
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
        for (ProcessSJF p : processes) {
            tableModel.addRow(new Object[] { p.processID, p.burstTime, p.arrivalTime, "-", "-", "-", "-" });
        }
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        currentTime.set(0);
        timeline.clear();
        ganttChartPanel.setTimeline(new ArrayList<>());

        processes.sort(Comparator.comparingInt(p -> p.arrivalTime));
        Queue<ProcessSJF> readyQueue = new PriorityQueue<>(Comparator.comparingInt(p -> p.burstTime));

        List<ProcessSJF> remainingProcesses = new ArrayList<>(processes);

        simulationTimer = new Timer(500, e -> {
            // Update the Gantt Chart border title to include the running time
            ganttChartPanel.setBorder(
                    BorderFactory.createTitledBorder("Gantt Chart | Running Time: " + currentTime.get() + " ms"));

            // Build Ready Queue Display
            StringBuilder readyQueueText = new StringBuilder();
            for (ProcessSJF process : readyQueue) {
                readyQueueText.append(process.processID).append(" ");
            }

            // Set CPU status
            String cpuStatus = readyQueue.isEmpty() ? "Idle" : " " + readyQueue.peek().processID;

            // Update the label
            cpuLabel.setText("Algorithm: SJF Non-Preemptive | CPU: " + cpuStatus + " | Ready Queue: " + readyQueueText);

            // Add new arrivals to the ready queue
            Iterator<ProcessSJF> iterator = remainingProcesses.iterator();
            while (iterator.hasNext()) {
                ProcessSJF p = iterator.next();
                if (p.arrivalTime <= currentTime.get()) {
                    readyQueue.add(p);
                    iterator.remove();
                }
            }

            if (!readyQueue.isEmpty()) {
                ProcessSJF p = readyQueue.poll();
                int start = Math.max(currentTime.get(), p.arrivalTime);
                int finish = start + p.burstTime;

                timeline.add(new EventSJF(p.processID, start, finish));
                ganttChartPanel.setTimeline(new ArrayList<>(timeline));

                p.completionTime = finish;
                p.turnaroundTime = finish - p.arrivalTime;
                p.waitingTime = p.turnaroundTime - p.burstTime;
                currentTime.set(finish);

                updateTable();
            } else if (!remainingProcesses.isEmpty()) {
                // Jump time forward to the next arriving process
                currentTime.set(remainingProcesses.get(0).arrivalTime);
            }

            if (readyQueue.isEmpty() && remainingProcesses.isEmpty()) {
                simulationTimer.stop();
                updateTable();
                startButton.setEnabled(true);
                cpuLabel.setText("Algorithm: SJF Non-Preemptive | CPU: Idle | Ready Queue: Empty");
            }
        });

        simulationTimer.start();
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
    }

    private void updateTable() {
        double avgWaitingTime = processes.stream().mapToInt(p -> p.waitingTime).average().orElse(0);
        double avgTurnaroundTime = processes.stream().mapToInt(p -> p.turnaroundTime).average().orElse(0);

        tableModel.setRowCount(0);

        // Add process data rows
        for (ProcessSJF p : processes) {
            tableModel.addRow(new Object[] {
                    p.processID, p.burstTime, p.arrivalTime,
                    p.waitingTime, p.turnaroundTime, "", "" // Keep averages empty for normal rows
            });
        }

        // Add a final row for averages
        tableModel.addRow(new Object[] {
                "Averages", "", "", "", "",
                String.format("%.2f", avgWaitingTime),
                String.format("%.2f", avgTurnaroundTime)
        });
    }
}
