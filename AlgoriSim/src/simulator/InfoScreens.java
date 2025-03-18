
import javax.swing.*;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class InfoScreens {
    private CardLayout layout;
    private JPanel mainPanel;

    // Constructor to initialize layout and mainPanel from Main.java
    public InfoScreens(CardLayout layout, JPanel mainPanel) {
        this.layout = layout;
        this.mainPanel = mainPanel;
    }

    // Credits screen
    class BackgroundPanel extends JPanel {
        private Image backgroundImage;
    
        public BackgroundPanel(String imagePath) {
            this.backgroundImage = new ImageIcon(imagePath).getImage();
            //setLayout(new BorderLayout()); // Allow adding components
        }
    
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
    
    // public void showCredits() {
    //     JPanel panel = new BackgroundPanel(CommonConstants.credits); // Change to your image path
    
    //     JButton backButton = createStyledButton(CommonConstants.backDefault, CommonConstants.backClicked, CommonConstants.backClicked);
    //     backButton.setBounds(400, 500, 150, 50);

    //     backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
    
    //     panel.add(backButton, BorderLayout.SOUTH);
    //     panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    //     mainPanel.add(panel, "Credits");
    // }

    public void showCredits() {
        JPanel panel = new BackgroundPanel(CommonConstants.credits); // Background panel
        panel.setLayout(new BorderLayout()); // Set layout to BorderLayout
    
        // Create and style backButton
        JButton backButton = createStyledButton(CommonConstants.backDefault, CommonConstants.backClicked, 
                CommonConstants.backClicked);
    
        // Center button horizontally at the bottom using FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Centered with padding
        buttonPanel.setOpaque(false); // Transparent background
        buttonPanel.add(backButton);
    
        // Action: Go back to Lobby
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
    
        // Add buttonPanel to the bottom of the panel
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        mainPanel.add(panel, "Credits");
    }
    
    // public void showHelp() {
    //     JPanel panel = new BackgroundPanel(CommonConstants.help); // Change to your image path
    
    //     JButton backButton = createStyledButton(CommonConstants.backDefault, CommonConstants.backClicked, CommonConstants.backClicked);
    //     backButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Add red border

    //     backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
    
    //     panel.add(backButton, BorderLayout.SOUTH);
    //     panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
    //     mainPanel.add(panel, "Help");
    // }

    public void showHelp() {
        JPanel panel = new BackgroundPanel(CommonConstants.help); // Background panel
        panel.setLayout(new BorderLayout()); // Set BorderLayout for positioning
    
        // Create and style backButton with red border
        JButton backButton = createStyledButton(CommonConstants.backDefault, CommonConstants.backClicked,
                CommonConstants.backClicked);
        backButton.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Add red border
    
        // Center button horizontally at the bottom using FlowLayout
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Centered with padding
        buttonPanel.setOpaque(false); // Transparent background
        buttonPanel.add(backButton);
    
        // Action: Go back to Lobby
        backButton.addActionListener(e -> layout.show(mainPanel, "Lobby"));
    
        // Add buttonPanel to the bottom of the panel
        panel.add(buttonPanel, BorderLayout.SOUTH);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    
        mainPanel.add(panel, "Help");
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
}
