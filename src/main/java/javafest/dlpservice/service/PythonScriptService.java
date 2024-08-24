package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

@Service
public class PythonScriptService {

    private static final Logger logger = LoggerFactory.getLogger(PythonScriptService.class);
    private final OCRProcessingService ocrProcessingService;

    public PythonScriptService(OCRProcessingService ocrProcessingService) {
        this.ocrProcessingService = ocrProcessingService;
    }

    @Scheduled(fixedRate = 10000)
    @Async
    public void runPythonScript() {
        // logger.info("!!!!!!!!Running Python script to check for screen sharing!!!!!!!!!!");

        try {
            ProcessBuilder processBuilder = new ProcessBuilder("C:/Python312/python.exe",
                    "D:/therap/screen monitoring/monitorscript.py");
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                logger.info("<Service Notification:> Python Script Output: " + line);
                if (line.contains("<Service Notification:> Screen sharing detected")) {
                    logger.info("<Service Notification:> Screen sharing detected, starting OCR processing...");

                    // Trigger OCR processing on the captured image
                    ocrProcessingService.processCapturedImage();
                    break;
                }
            }

            process.waitFor();
        } catch (Exception e) {
            logger.error("<Service Notification:> Failed to run Python script", e);
        }
    }
}
