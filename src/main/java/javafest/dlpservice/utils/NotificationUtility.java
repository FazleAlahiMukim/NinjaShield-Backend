package javafest.dlpservice.utils;

import javax.swing.*;
import java.awt.*;

public class NotificationUtility {

    public void notifyUser(String message) {
        if (GraphicsEnvironment.isHeadless()) {
            System.out.println("Notification: " + message);
        } else {
            // Load your large custom icon
            ImageIcon largeIcon = new ImageIcon(getClass().getResource("/icons/NinjaShield.png"));
            // Scale the icon to a smaller size, e.g., 64x64 pixels
            Image scaledImage = largeIcon.getImage().getScaledInstance(64, 64, Image.SCALE_SMOOTH);
            ImageIcon customIcon = new ImageIcon(scaledImage);

            // Show dialog with scaled custom icon
            JOptionPane.showMessageDialog(null, message, "Data Leak Monitor Alert", JOptionPane.INFORMATION_MESSAGE,
                    customIcon);
        }
    }
}
