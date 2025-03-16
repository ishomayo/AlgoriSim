
import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel splashLabel = new JLabel("Welcome to AlgoriSim", SwingConstants.CENTER);
        splashLabel.setFont(new Font("Arial", Font.BOLD, 32));
        splashLabel.setForeground(Color.WHITE);

        JLabel loadingLabel = new JLabel("Loading...", SwingConstants.CENTER);
        loadingLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        loadingLabel.setForeground(Color.WHITE);

        panel.setBackground(Color.BLACK);
        panel.add(splashLabel, BorderLayout.CENTER);
        panel.add(loadingLabel, BorderLayout.SOUTH);

        setContentPane(panel);
        setSize(500, 300);
        setLocationRelativeTo(null);
    }

    public void showSplash() {
        setVisible(true);

        // Use SwingWorker to delay execution without freezing UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(2000); // Show for 3 seconds
                return null;
            }

            @Override
            protected void done() {
                dispose(); // Close splash screen
                Main.startApplication(); // Start the Main window after splash
            }
        };

        worker.execute();
    }
}