package javafest.dlpservice.utils;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;

import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.animation.TranslateTransition;

public class NotificationUtil {
    private final Stage notificationStage;
    private final AnchorPane root;
    private static Stage hiddenOwnerStage;
    private final int durationSeconds = 10;
    private String title;
    private String message;
    private String imagePath;
    
    public NotificationUtil() {
        if (hiddenOwnerStage == null) {
            hiddenOwnerStage = new Stage(StageStyle.UTILITY);
            hiddenOwnerStage.setOpacity(0);
            hiddenOwnerStage.setWidth(1);
            hiddenOwnerStage.setHeight(1);
            hiddenOwnerStage.setX(-100);
            hiddenOwnerStage.show();
        }

        notificationStage = new Stage();
        root = new AnchorPane();
        
        notificationStage.initOwner(hiddenOwnerStage);
        notificationStage.initStyle(StageStyle.TRANSPARENT);
        notificationStage.setAlwaysOnTop(true);
        
        root.setPrefSize(300, 110);
        root.getStyleClass().add("notification-pane");
        
        root.setStyle("-fx-background-color: white;" +
                     "-fx-border-color: #cccccc;" +
                     "-fx-border-width: 2px;" +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
        
        Scene scene = new Scene(root);
        scene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        notificationStage.setScene(scene);
    }
    
    public void show(String destination, String action, String filePath) {
        Platform.runLater(() -> {
            root.getChildren().clear();
            setLabel(destination, filePath);
            double titleLeftAnchor = 110.0;
            if (action.equals("warn") || destination.equals("Screenshare")) {
                title = "Warning";
                try {
                    Image image = new Image("file:" + "src/main/resources/icons/warn.png");
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(30);
                    imageView.setFitWidth(30);
                    AnchorPane.setTopAnchor(imageView, 15.0);
                    AnchorPane.setLeftAnchor(imageView, 100.0);
                    root.getChildren().add(imageView);
                    titleLeftAnchor = 130.0;
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }
            
            Label titleLabel = new Label(title);
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            AnchorPane.setTopAnchor(titleLabel, 20.0);
            AnchorPane.setLeftAnchor(titleLabel, titleLeftAnchor);
            
            Label messageLabel = new Label(message);
            messageLabel.setWrapText(true);
            AnchorPane.setTopAnchor(messageLabel, 50.0);
            AnchorPane.setLeftAnchor(messageLabel, 110.0);
            AnchorPane.setRightAnchor(messageLabel, 10.0);

            Label footNote = new Label("Contact admin for more information");
            footNote.setStyle("-fx-font-size: 10px; -fx-background-color: lightgray;");
            AnchorPane.setBottomAnchor(footNote, 0.0);
            AnchorPane.setLeftAnchor(footNote, 0.0);
            AnchorPane.setRightAnchor(footNote, 0.0);
            footNote.setAlignment(Pos.CENTER);
            
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Image image = new Image("file:" + imagePath);
                    ImageView imageView = new ImageView(image);
                    imageView.setFitHeight(80);
                    imageView.setFitWidth(80);
                    AnchorPane.setTopAnchor(imageView, 10.0);
                    AnchorPane.setLeftAnchor(imageView, 15.0);
                    root.getChildren().add(imageView);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }
            
            root.getChildren().addAll(titleLabel, messageLabel, footNote);
            
            positionNotification();
            
            notificationStage.setWidth(root.getPrefWidth());
            notificationStage.setHeight(root.getPrefHeight());

            TranslateTransition swipeIn = new TranslateTransition(Duration.millis(500), root);
            swipeIn.setFromX(300);
            swipeIn.setToX(0);
            swipeIn.play();
            
            notificationStage.show();

            swipeIn.setOnFinished(event -> {
                PauseTransition delay = new PauseTransition(Duration.seconds(durationSeconds));
                delay.setOnFinished(e -> {
                    TranslateTransition swipeOut = new TranslateTransition(Duration.millis(500), root);
                    swipeOut.setFromX(0);
                    swipeOut.setToX(300);
                    swipeOut.setOnFinished(ev -> cleanup());
                    swipeOut.play();
                });
                delay.play();
            });
        });
    }
    
    private void positionNotification() {
        Screen screen = Screen.getPrimary();
        double screenWidth = screen.getVisualBounds().getWidth();
        double screenHeight = screen.getVisualBounds().getHeight();
        
        notificationStage.setX(screenWidth - root.getPrefWidth() - 20);
        notificationStage.setY(screenHeight - root.getPrefHeight() - 20);
    }

    public double getYPosition() {
        return notificationStage.getY();
    }

    public void setYPosition(double y) {
        notificationStage.setY(y);
    }
    
    public void cleanup() {
        Platform.runLater(() -> {
            if (notificationStage != null) {
                notificationStage.close();
            }
            if (hiddenOwnerStage != null) {
                hiddenOwnerStage.close();
                hiddenOwnerStage = null;
            }
        });
    }

    private void setLabel(String destination, String filePath) {
        String fileName = "";
        if (filePath != null)
            fileName = new File(filePath).getName();

        if (destination.equals("Removable storage")) {
            title = "File transfer blocked";
            message = "Restricted data in file " + fileName;
            imagePath = "src/main/resources/icons/file.png";
        } else if (destination.equals("Email")) {
            title = "Email blocked";
            message = "Restricted data in email " + fileName;
            imagePath = "src/main/resources/icons/email.png";
        } else if (destination.equals("Printer")) {
            title = "File print blocked";
            message = "Restricted data in file " + fileName;
            imagePath = "src/main/resources/icons/printer.png";
        } else if (destination.equals("Screenshot")) {
            title = "Screenshot blocked";
            message = "Restricted data detected";
            imagePath = "src/main/resources/icons/screenshot.png";
        } else if (destination.equals("Web")) {
            title = "Web upload blocked";
            message = "Restricted data in file " + fileName;
            imagePath = "src/main/resources/icons/web.png";
        } else if (destination.equals("Screenshare")) {
            title = "Warning";
            message = "Restricted data detected";
            imagePath = "src/main/resources/icons/screenshare.png";
        } else {
            title = "Warning";
            message = "Restricted data detected";
            imagePath = "src/main/resources/icons/NinjaShield.png";
        }
    }
}