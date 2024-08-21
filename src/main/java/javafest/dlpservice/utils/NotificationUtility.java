package javafest.dlpservice.utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;

public class NotificationUtility {

    public void notifyUser(String message, String details) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Notification: " + message);
        } else {
            showSlidingNotification(message, details);
        }
    }

    private void showSlidingNotification(String message, String details) {
        // Create a JWindow instead of JOptionPane for custom behavior
        JWindow window = new JWindow();

        // Load and scale the custom icon
        URL iconURL = getClass().getResource("/icons/NinjaShield.png");
        ImageIcon largeIcon = new ImageIcon(iconURL);
        Image scaledImage = largeIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH); // Increased icon size
        ImageIcon customIcon = new ImageIcon(scaledImage);

        // Create content for the window
        JLabel iconLabel = new JLabel(customIcon);

        JLabel warningLabel = new JLabel("Warning!", SwingConstants.CENTER);
        warningLabel.setFont(new Font("Arial", Font.BOLD, 18)); // Font for warning message
        warningLabel.setForeground(Color.RED);

        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.BOLD, 20)); // Increased font size

        JLabel detailsLabel = new JLabel(details);
        detailsLabel.setFont(new Font("Arial", Font.PLAIN, 16)); // Increased font size
        detailsLabel.setForeground(Color.GRAY);

        JButton closeButton = new JButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 18)); // Increased font size
        closeButton.setForeground(Color.RED);

        // Hover effect for the close button
        closeButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                closeButton.setForeground(Color.WHITE);
                closeButton.setBackground(Color.RED);
                closeButton.setOpaque(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                closeButton.setForeground(Color.RED);
                closeButton.setBackground(null);
                closeButton.setOpaque(false);
            }
        });

        closeButton.addActionListener(e -> window.dispose());

        // Layout for the notification panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(217, 234, 247)); // Soft Blue background
        topPanel.add(iconLabel, BorderLayout.WEST);
        topPanel.add(warningLabel, BorderLayout.CENTER); // Added warning label to the center
        topPanel.add(closeButton, BorderLayout.EAST);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(new Color(217, 234, 247)); // Soft Blue background
        bottomPanel.add(detailsLabel, BorderLayout.CENTER);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        panel.setBackground(new Color(255, 255, 255)); // White background for the main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        window.add(panel);
        window.setSize(400, 200); // Increased overall size of the window

        // Get screen dimensions to position the window at the bottom right corner
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screenSize.getWidth() - window.getWidth() - 10;
        int y = (int) screenSize.getHeight();

        window.setLocation(x, y);

        // Slide-in animation
        Timer timer = new Timer(5, new ActionListener() {
            private int currentY = y;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentY > screenSize.getHeight() - window.getHeight() - 50) {
                    currentY -= 2;
                    window.setLocation(x, currentY);
                } else {
                    ((Timer) e.getSource()).stop();

                    // Auto-close after 5 seconds unless manually closed
                    Timer closeTimer = new Timer(5000, event -> window.dispose());
                    closeTimer.setRepeats(false);
                    closeTimer.start();
                }
            }
        });
        timer.start();

        window.setVisible(true);
    }
}
