package javafest.dlpservice.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OCRProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(OCRProcessingService.class);
    private final ITesseract tesseract;

    // Path to the captured image
    private final Path capturedImagePath = Paths.get("D:/therap/NinjaShield-Backend/captured", "captured.png");

    public OCRProcessingService() {
        logger.info("OCRProcessingService initialized");

        tesseract = new Tesseract();
        tesseract.setDatapath("D:/Apps/tesseractOCR/tessdata"); // Ensure this path is correct
    }

    @Async
    public void processCapturedImage() {
        logger.info("Starting OCR processing for captured image...");

        try {
            File imageFile = capturedImagePath.toFile();
            if (imageFile.exists()) {
                processImage(imageFile);
            } else {
                logger.warn("Captured image not found: " + capturedImagePath);
            }
        } catch (Exception e) {
            logger.error("Failed to process captured image", e);
        }
    }

    private void processImage(File imageFile) {
        logger.info("Processing image: " + imageFile.getName());

        try {
            String result = tesseract.doOCR(imageFile);
            logger.info("OCR result: " + result);
        } catch (TesseractException e) {
            logger.error("Error during OCR processing for image: " + imageFile.getName(), e);
        }
    }
}
