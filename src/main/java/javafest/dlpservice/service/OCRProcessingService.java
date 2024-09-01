package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

@Service
public class OCRProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(OCRProcessingService.class);
    private final ITesseract tesseract = new Tesseract();

    public OCRProcessingService() {
        tesseract.setDatapath("D:/Apps/tesseractOCR/tessdata");
    }

    @Scheduled(fixedRate = 10000)
    @Async
    public void monitorScreenSharing() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("C:/Python312/python.exe", 
                "D:/therap/screen monitoring/monitorscript.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("Python Script Output: " + line);
                if (line.contains("Screen sharing detected") || line.contains("Zoom Meeting") || line.contains("Google Meet")) {
                    logger.info("Online meeting detected, starting OCR processing...");
                    scanCurrentWindow();
                }
            }

            process.waitFor();
        } catch (Exception e) {
            logger.error("Failed to run Python script", e);
        }
    }

    public void scanCurrentWindow() {
        try {
            // Capture the current screen
            Robot robot = new Robot();
            Rectangle screenRect = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
            BufferedImage screenCapture = robot.createScreenCapture(screenRect);

            // Save the captured screen to a file
            File screenFile = new File("D:/therap/screen monitoring/current_screen.png");
            ImageIO.write(screenCapture, "png", screenFile);

            // Perform OCR on the captured image
            String result = tesseract.doOCR(screenFile);
            logger.info("OCR result: " + result);

            // Check if "test.txt" content is visible
            if (result.contains("Contents of test.txt")) {  // Replace with actual content of test.txt
                logger.info("Sensitive content detected on screen, blocking view...");

                // Implement logic to overlay a block on the screen
                blockSensitiveContent();
            }

        } catch (Exception e) {
            logger.error("Error during OCR processing", e);
        }
    }

    public void blockSensitiveContent() {
        // Logic to overlay a visual block on the screen
        logger.info("Overlaying block on sensitive content...");
        // This could be implemented using a GUI framework to cover parts of the screen
    }
}
