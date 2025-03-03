package simulator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;

public class SimulationScreen extends JPanel {
    private JLabel algorithmLabel;
    private JLabel cpuLabel;
    private JLabel readyQueueLabel;
    private JTable processTable;
    private DefaultTableModel tableModel;
    private JButton startButton, stopButton, homeButton;
    private JLabel totalExecutionTimeLabel;
    private JPanel ganttChartPanel;

    public SimulationScreen(String algorithmName, CardLayout layout, JPanel mainPanel) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        // === TOP PANEL ===
        JPanel topPanel = new JPanel(new BorderLayout());

        // Algorithm Label (Top Left)
        algorithmLabel = new JLabel("Algorithm: " + algorithmName);
        algorithmLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(algorithmLabel, BorderLayout.WEST);

        // CPU Label (Top Center)
        cpuLabel = new JLabel("CPU: Idle", SwingConstants.CENTER);
        cpuLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(cpuLabel, BorderLayout.CENTER);

        // Ready Queue Label (Top Right)
        readyQueueLabel = new JLabel("Ready Queue: Empty", SwingConstants.RIGHT);
        readyQueueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(readyQueueLabel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // === HOME BUTTON PANEL ===
        JPanel homePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        homeButton = new JButton("Home");
        homeButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
        homePanel.add(homeButton);
        add(homePanel, BorderLayout.WEST);

        // === TABLE PANEL (CENTER) ===
        String[] columnNames = {"Process ID", "Burst Time", "Arrival Time", "Priority", "Status", "Waiting Time", "Turnaround Time"};
        tableModel = new DefaultTableModel(columnNames, 8);
        processTable = new JTable(tableModel);
        processTable.getTableHeader().setReorderingAllowed(false); // Prevent column reordering

        // Set custom renderer for the progress bar column
        processTable.getColumn("Status").setCellRenderer(new ProgressRenderer());

        JScrollPane tableScrollPane = new JScrollPane(processTable);
        tableScrollPane.setPreferredSize(new Dimension(700, 200));
        add(tableScrollPane, BorderLayout.CENTER);

        // === GANTT CHART PANEL ===
        ganttChartPanel = new JPanel();
        ganttChartPanel.setPreferredSize(new Dimension(700, 100));
        ganttChartPanel.setBorder(BorderFactory.createTitledBorder("Gantt Chart"));
        ganttChartPanel.setBackground(Color.WHITE);
        add(ganttChartPanel, BorderLayout.SOUTH);

        // === BOTTOM PANEL (START/STOP BUTTONS + EXECUTION TIME) ===
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

        startButton = new JButton("Start");
        stopButton = new JButton("Stop");
        stopButton.setEnabled(false); // Initially disabled

        totalExecutionTimeLabel = new JLabel("Total Execution Time: 0 ms");

        bottomPanel.add(startButton);
        bottomPanel.add(stopButton);
        bottomPanel.add(totalExecutionTimeLabel);

        add(bottomPanel, BorderLayout.PAGE_END);
    }

    // Method to add a process row to the table
    public void addProcess(String id, int burstTime, int arrivalTime, int priority) {
        Object[] row = {id, burstTime, arrivalTime, priority, new JProgressBar(0, burstTime), 0, 0};
        tableModel.addRow(row);
    }

    // Method to update the Ready Queue Label
    public void updateReadyQueue(String queue) {
        readyQueueLabel.setText("Ready Queue: " + queue);
    }

    // Method to update the CPU label
    public void updateCPU(String process) {
        cpuLabel.setText("CPU: " + process);
    }

    // Method to update execution time
    public void updateExecutionTime(int time) {
        totalExecutionTimeLabel.setText("Total Execution Time: " + time + " ms");
    }

    // Custom Renderer for Progress Bar in the Table
    private static class ProgressRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressRenderer() {
            setStringPainted(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            if (value instanceof JProgressBar) {
                return (JProgressBar) value;
            }
            return this;
        }
    }
}