import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {
        JPanel panel = new JPanel(new BorderLayout());

        // Load GIF
        ImageIcon gifIcon = new ImageIcon(CommonConstants.splash); // Replace with your actual GIF file
        Image gifImage = gifIcon.getImage().getScaledInstance(500, 300, Image.SCALE_DEFAULT);
        ImageIcon resizedGif = new ImageIcon(gifImage);

        // Label to hold resized GIF
        JLabel gifLabel = new JLabel(resizedGif);
        gifLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gifLabel.setVerticalAlignment(SwingConstants.CENTER);

        panel.add(gifLabel, BorderLayout.CENTER);

        setContentPane(panel);
        setSize(500, 300); // Force 500x300 size
        setLocationRelativeTo(null);
    }

    public void showSplash() {
        setVisible(true);

        // Use SwingWorker to delay execution without freezing UI
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(3000); // Show for 3 seconds
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

    public static void main(String[] args) {
        SplashScreen splash = new SplashScreen();
        splash.showSplash();
    }
}
