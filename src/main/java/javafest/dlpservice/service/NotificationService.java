package javafest.dlpservice.service;

import javafest.dlpservice.utils.NotificationUtil;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import javafx.application.Platform;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class NotificationService {
    
    private List<NotificationUtil> activeNotifications;
    
    @PostConstruct
    public void init() {
        Platform.runLater(() -> {
            activeNotifications = new ArrayList<>();
        });
    }
    
    public void showNotification(String destination, String action, String filePath) {
        if (action.equals("log")) {
            return;
        }
        
        Platform.runLater(() -> {
            if (activeNotifications.size() >= 4) {
                activeNotifications.remove(0);
            }
            shiftExistingNotificationsUp();

            NotificationUtil notification = new NotificationUtil();
            notification.show(destination, action, filePath);

            activeNotifications.add(notification);
        });
    }

    private void shiftExistingNotificationsUp() {
        double shiftAmount = 120.0;
        for (NotificationUtil notification : activeNotifications) {
            double currentY = notification.getYPosition();
            notification.setYPosition(currentY - shiftAmount);
        }
    }

    @PreDestroy
    public void destroy() {
        Platform.runLater(() -> {
            for (NotificationUtil notification : activeNotifications) {
                notification.cleanup();
            }
        });
    }
}