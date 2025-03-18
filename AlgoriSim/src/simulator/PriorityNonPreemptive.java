import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.*;
import java.util.*;
import java.util.List;

class ProcessPriorityNonPreemptive {
    String processID;
    int arrivalTime, burstTime, completionTime, turnaroundTime, waitingTime, priority;

    public ProcessPriorityNonPreemptive(String processID, int arrivalTime, int burstTime, int priority) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
    }
}

class EventPriorityNonPreemptive {
    String processName;
    int startTime, finishTime;

    public EventPriorityNonPreemptive(String processName, int startTime, int finishTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}

class CustomPanelPriorityNonPreemptive extends JPanel {
    private List<EventPriorityNonPreemptive> timeline = new ArrayList<>();
    private final Map<String, Color> processColors = new HashMap<>();
    private final List<Color> availableColors = Arrays.asList(
            Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA,
            Color.CYAN, Color.PINK, Color.YELLOW, Color.LIGHT_GRAY, Color.GRAY);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (timeline.isEmpty())
            return;

        int x = 30, y = 20, width = 40; // Increased width for visibility

        for (int i = 0; i < timeline.size(); i++) {
            EventPriorityNonPreemptive event = timeline.get(i);
            processColors.putIfAbsent(event.processName,
                    availableColors.get(processColors.size() % availableColors.size()));

            g.setColor(processColors.get(event.processName));
            g.fillRect(x, y, width, 30);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, width, 30);
            g.drawString(event.processName, x + 10, y + 20);
            g.drawString(Integer.toString(event.startTime), x - 10, y + 45);

            if (i == timeline.size() - 1) {
                g.drawString(Integer.toString(event.finishTime), x + 25, y + 45);
            }

            x += width;
        }

        setPreferredSize(new Dimension(x + 30, 75));
        revalidate();
    }

    public void setTimeline(List<EventPriorityNonPreemptive> timeline) {
        this.timeline = timeline;
        repaint();
    }
}

public class PriorityNonPreemptive extends JPanel {
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton;
    private List<ProcessPriorityNonPreemptive> processes = new ArrayList<>();
    private CustomPanelPriorityNonPreemptive ganttChartPanel;
    private List<EventPriorityNonPreemptive> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;
    private Image backgroundImage;

    public PriorityNonPreemptive(CardLayout layout, JPanel mainPanel) {
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
        topPanel.add(new JLabel(" ", JLabel.LEFT), BorderLayout.WEST);
        cpuLabel = new JLabel("Algorithm: Priority Scheduling Non-Preemptive", SwingConstants.CENTER);
        cpuLabel.setForeground(Color.WHITE);
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel(" ", SwingConstants.RIGHT);
        readyQueueLabel.setForeground(Color.WHITE);
        topPanel.add(readyQueueLabel, BorderLayout.EAST);
        topPanel.setOpaque(false);
        topPanel.setBackground(new Color(0, 0, 0, 0));
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {
                "Process ID", "Burst Time", "Arrival Time", "Priority",
                "Waiting Time", "Turnaround Time", "Average Waiting Time", "Average Turnaround Time"
        };

        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        ganttChartPanel = new CustomPanelPriorityNonPreemptive();
        ganttChartPanel.setPreferredSize(new Dimension(700, 100));
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
        buttonPanel.add(totalExecutionTimeLabel);
        buttonPanel.setOpaque(false);
        buttonPanel.setBackground(new Color(0, 0, 0, 0));
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
                if (data.length < 4)
                    continue;

                try {
                    processes.add(new ProcessPriorityNonPreemptive(
                            data[0], Integer.parseInt(data[1]), Integer.parseInt(data[2]), Integer.parseInt(data[3])));
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
        for (ProcessPriorityNonPreemptive p : processes) {
            tableModel.addRow(new Object[] { p.processID, p.burstTime, p.arrivalTime, p.priority, "-", "-" });
        }
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        timeline.clear();
        ganttChartPanel.repaint();

        priorityNonPreemptiveScheduling();
        updateTable();
    }

    private void priorityNonPreemptiveScheduling() {
        processes.sort(Comparator.comparingInt((ProcessPriorityNonPreemptive p) -> p.arrivalTime)
                .thenComparingInt(p -> p.priority));

        SwingWorker<Void, EventPriorityNonPreemptive> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                int currentTime = 0;
                for (ProcessPriorityNonPreemptive p : processes) {
                    if (currentTime < p.arrivalTime) {
                        currentTime = p.arrivalTime;
                    }

                    int startTime = currentTime;
                    int finishTime = currentTime + p.burstTime;
                    EventPriorityNonPreemptive event = new EventPriorityNonPreemptive(p.processID, startTime,
                            finishTime);
                    timeline.add(event);
                    publish(event);

                    p.completionTime = finishTime;
                    p.turnaroundTime = p.completionTime - p.arrivalTime;
                    p.waitingTime = p.turnaroundTime - p.burstTime;

                    currentTime = finishTime;
                    Thread.sleep(1000); // Simulate execution delay
                }
                return null;
            }

            @Override
            protected void process(List<EventPriorityNonPreemptive> chunks) {
                ganttChartPanel.setTimeline(new ArrayList<>(timeline));
                updateTable();
            }

            @Override
            protected void done() {
                ganttChartPanel.setBorder(BorderFactory.createTitledBorder(
                        "Gantt Chart | Running Time: " + timeline.get(timeline.size() - 1).finishTime + " ms"));

                startButton.setEnabled(true);
            }
        };

        startButton.setEnabled(false);
        timeline.clear();
        ganttChartPanel.repaint();
        worker.execute();
    }

    private void updateTable() {
        tableModel.setRowCount(0); // Clear the table

        double totalWT = 0, totalTAT = 0;

        // Add data for each process
        for (ProcessPriorityNonPreemptive p : processes) {
            totalWT += p.waitingTime;
            totalTAT += p.turnaroundTime;
            tableModel.addRow(new Object[] {
                    p.processID,
                    p.burstTime,
                    p.arrivalTime,
                    p.priority,
                    p.waitingTime,
                    p.turnaroundTime,
                    "", // Empty cell for Average Waiting Time
                    "" // Empty cell for Average Turnaround Time
            });
        }

        // Calculate averages
        double avgWT = totalWT / processes.size();
        double avgTAT = totalTAT / processes.size();

        // Format the averages to 2 decimal places
        String avgWTFormatted = String.format("%.2f", avgWT);
        String avgTATFormatted = String.format("%.2f", avgTAT);

        // Add the last row for averages
        tableModel.addRow(new Object[] {
                "Average",
                "", // Empty cell for Process ID
                "", // Empty cell for Burst Time
                "", // Empty cell for Arrival Time
                "", // Empty cell for Priority
                "", // Empty cell for Waiting Time
                avgWTFormatted, // Display formatted Average Waiting Time
                avgTATFormatted // Display formatted Average Turnaround Time
        });

        // Update Gantt chart border with the final running time
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder(
                "Gantt Chart | Running Time: " + timeline.get(timeline.size() - 1).finishTime + " ms"));
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
    }
}
