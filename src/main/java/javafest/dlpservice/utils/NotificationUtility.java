package javafest.dlpservice.utils;

import javax.swing.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

@Component
@Async
public class NotificationUtility {

    public void notifyUser(String title, String message, String iconDirectory) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Notification: " + title + " - " + message);
        } else {
            showSlidingNotification(title, message, iconDirectory);
        }
    }

    private void showSlidingNotification(String title, String message, String iconDirectory) {
        // Create a JWindow for custom notification
        JWindow window = new JWindow();
        window.setLayout(new BorderLayout());
        window.setAlwaysOnTop(true); // Ensure the window stays on top of all other windows

        // Load and scale the custom icon
        URL iconURL = getClass().getResource(iconDirectory);
        ImageIcon icon = new ImageIcon(iconURL);
        Image scaledIcon = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        JLabel iconLabel = new JLabel(new ImageIcon(scaledIcon));

        // Notification title (bold)
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center horizontally

        // Notification message (smaller)
        JLabel messageLabel = new JLabel(message);
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        messageLabel.setHorizontalAlignment(SwingConstants.CENTER); // Center horizontally

        // Create a panel to hold title and message, centered
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BorderLayout());
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(messageLabel, BorderLayout.CENTER);

        // Create a close button ("X")
        JButton closeButton = new JButton("X");
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.setContentAreaFilled(false);
        closeButton.setFont(new Font("Arial", Font.BOLD, 14));
        closeButton.setForeground(Color.RED);

        // Close button functionality
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
        });

        // Create a panel for close button
        JPanel closeButtonPanel = new JPanel();
        closeButtonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        closeButtonPanel.add(closeButton);

        // Create a main panel for the notification
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        mainPanel.add(iconLabel, BorderLayout.WEST);
        mainPanel.add(textPanel, BorderLayout.CENTER); // Centered title and message
        mainPanel.add(closeButtonPanel, BorderLayout.NORTH); // Add close button at the top

        window.add(mainPanel);
        window.setSize(300, 100);

        // Set window position to start at bottom-right corner off-screen
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) screenSize.getWidth() - window.getWidth() - 10;
        int startY = (int) screenSize.getHeight(); // Off-screen (start position)
        int endY = (int) screenSize.getHeight() - window.getHeight() - 10; // Final position

        window.setLocation(x, startY);

        // Slide-in animation (sliding up)
        Timer slideTimer = new Timer(5, new ActionListener() {
            private int currentY = startY;

            @Override
            public void actionPerformed(ActionEvent e) {
                if (currentY > endY) {
                    currentY -= 2; // Speed of sliding up
                    window.setLocation(x, currentY);
                } else {
                    ((Timer) e.getSource()).stop();
                }
            }
        });
        slideTimer.start();

        // Automatically close after 5 seconds unless manually closed
        Timer closeTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                window.dispose();
            }
        });
        closeTimer.setRepeats(false);
        closeTimer.start();

        window.setVisible(true);
    }
}
