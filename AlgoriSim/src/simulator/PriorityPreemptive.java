import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseEvent;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.io.*;
import java.util.*;
import java.util.List;

class ProcessPriorityPreemptive {
    String processID;
    int arrivalTime, burstTime, remainingTime, completionTime, turnaroundTime, waitingTime, priority;
    boolean isCompleted = false;
    

    public ProcessPriorityPreemptive(String processID, int arrivalTime, int burstTime, int priority) {
        this.processID = processID;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingTime = burstTime;
        this.priority = priority;
    }
}

class EventPriorityPreemptive {
    String processName;
    int startTime, finishTime;

    public EventPriorityPreemptive(String processName, int startTime, int finishTime) {
        this.processName = processName;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
}

class CustomPanelPriorityPreemptive extends JPanel {
    private List<EventPriorityPreemptive> timeline = new ArrayList<>();
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
            EventPriorityPreemptive event = timeline.get(i);

            if (!processColors.containsKey(event.processName)) {
                processColors.put(event.processName, availableColors.get(processColors.size() % availableColors.size()));
            }

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

    public void setTimeline(List<EventPriorityPreemptive> timeline) {
        this.timeline = timeline;
        repaint();
    }
}

public class PriorityPreemptive extends JPanel {
    private JLabel cpuLabel, readyQueueLabel, totalExecutionTimeLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton;
    private List<ProcessPriorityPreemptive> processes = new ArrayList<>();
    private CustomPanelPriorityPreemptive ganttChartPanel;
    private List<EventPriorityPreemptive> timeline = new ArrayList<>();
    private CardLayout layout;
    private JPanel mainPanel;
    private Image backgroundImage;

    public PriorityPreemptive(CardLayout layout, JPanel mainPanel) {
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
        topPanel.add(new JLabel("Algorithm: Priority Scheduling Preemptive", JLabel.LEFT), BorderLayout.WEST);
        cpuLabel = new JLabel("CPU: Idle", SwingConstants.CENTER);
        topPanel.add(homeButton, BorderLayout.WEST);
        topPanel.setOpaque(false);
        cpuLabel.setForeground(Color.WHITE);
        topPanel.add(cpuLabel, BorderLayout.CENTER);
        readyQueueLabel = new JLabel("Ready Queue: Empty", SwingConstants.RIGHT);
        readyQueueLabel.setForeground(Color.WHITE);
        topPanel.add(readyQueueLabel, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        String[] columnNames = {
                "Process ID", "Burst Time", "Arrival Time", "Priority",
                "Waiting Time", "Turnaround Time"
        };
        tableModel = new DefaultTableModel(columnNames, 0);
        processTable = new JTable(tableModel);

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));

        ganttChartPanel = new CustomPanelPriorityPreemptive();
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
        stopButton = createStyledButton(CommonConstants.stopDefault, CommonConstants.stopHover,
        CommonConstants.stopClicked);
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> startSimulation());
        stopButton.addActionListener(e -> stopSimulation());

        buttonPanel.add(startButton);
        buttonPanel.add(stopButton);
        buttonPanel.setOpaque(false);
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
                if (data.length < 4) continue;
    
                try {
                    processes.add(new ProcessPriorityPreemptive(
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
        for (ProcessPriorityPreemptive p : processes) {
            tableModel.addRow(new Object[]{p.processID, p.burstTime, p.arrivalTime, p.priority, "-", "-"});
        }
    }

    private void startSimulation() {
        startButton.setEnabled(false);
        stopButton.setEnabled(true);
        timeline.clear();
        ganttChartPanel.repaint();

        for (ProcessPriorityPreemptive p : processes) {
            p.remainingTime = p.burstTime;
            p.isCompleted = false;
        }

        precomputePriorityPreemptive();
        updateTable();
    }

    private void precomputePriorityPreemptive() {
        List<ProcessPriorityPreemptive> queue = new ArrayList<>();
        int currentTime = 0;
        int completedProcesses = 0;

        while (completedProcesses < processes.size()) {
            for (ProcessPriorityPreemptive p : processes) {
                if (p.arrivalTime <= currentTime && !p.isCompleted && !queue.contains(p)) {
                    queue.add(p);
                }
            }

            queue.sort(Comparator.comparingInt((ProcessPriorityPreemptive p) -> p.priority)
                    .thenComparingInt(p -> p.arrivalTime));

            if (!queue.isEmpty()) {
                ProcessPriorityPreemptive current = queue.get(0);
                int executionTime = 1;
                timeline.add(new EventPriorityPreemptive(current.processID, currentTime, currentTime + executionTime));

                current.remainingTime -= executionTime;
                currentTime += executionTime;

                if (current.remainingTime == 0) {
                    current.isCompleted = true;
                    current.completionTime = currentTime;
                    current.turnaroundTime = current.completionTime - current.arrivalTime;
                    current.waitingTime = current.turnaroundTime - current.burstTime;
                    queue.remove(current);
                    completedProcesses++;
                }
            } else {
                currentTime++;
            }
        }
        ganttChartPanel.setTimeline(timeline);
        updateTable();
    }

    private void updateTable() {
        tableModel.setRowCount(0);
        for (ProcessPriorityPreemptive p : processes) {
            tableModel.addRow(new Object[]{p.processID, p.burstTime, p.arrivalTime, p.priority, p.waitingTime, p.turnaroundTime});
        }
        totalExecutionTimeLabel.setText("Total Execution Time: " + timeline.get(timeline.size() - 1).finishTime + " ms");
    }

    private void stopSimulation() {
        startButton.setEnabled(true);
        stopButton.setEnabled(false);
    }
}
