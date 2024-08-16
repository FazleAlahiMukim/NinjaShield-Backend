package javafest.dlpservice.service;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class OCRProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(OCRProcessingService.class);
    private final ITesseract tesseract;

    // Path to the directory and file
    private final Path directoryPath = Paths
            .get("D:/therap/NinjaShield-Backend/src/main/java/javafest/dlpservice/service");
    private final String fileName = "test.png";

    public OCRProcessingService() {
        logger.info("!!!!!!!!!!!!!!!!!!!!!!OCRProcessingService initialized!!!!!!!!!!!!!!!!!!!!");

        tesseract = new Tesseract();
        tesseract.setDatapath("D:/Apps/tesseractOCR/tessdata"); // Ensure this path is correct
    }

    // This method will now run every 1 second
    @Scheduled(fixedRate = 1000)
    public void processImagesInDirectory() {
        logger.info("Starting OCR processing for file: " + fileName);

        try {
            Path filePath = directoryPath.resolve(fileName);
            if (Files.exists(filePath)) {
                processImage(filePath.toFile());
            } else {
                logger.warn("File not found: " + filePath.toString());
            }
        } catch (Exception e) {
            logger.error("Failed to process file: " + fileName, e);
        }

        logger.info("Finished OCR processing for file: " + fileName);
    }

    private void processImage(File imageFile) {
        logger.info("Processing image: " + imageFile.getName());

        try {
            String result = tesseract.doOCR(imageFile);
            logger.info("OCR result for " + imageFile.getName() + ": " + result);
        } catch (TesseractException e) {
            logger.error("Error during OCR processing for image: " + imageFile.getName(), e);
        }
    }
}
