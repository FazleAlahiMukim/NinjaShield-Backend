package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

@Service
public class ClipboardScanService {

    private static final Logger logger = LoggerFactory.getLogger(ClipboardScanService.class);
    private static final String KEYWORD = "Keyword"; // Define the keyword to search for

    @Scheduled(fixedRate = 2000) // Scans clipboard every 5 seconds
    @Async
    public void scanClipboard() {
        try {
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            String clipboardContent = (String) clipboard.getData(DataFlavor.stringFlavor);

            if (clipboardContent != null && clipboardContent.contains(KEYWORD)) {
                logger.info("!!!!!!!!!! KeyWord found in clipboard content !!!!!!!!!!");
                // You can add more actions here if needed
            } else {
                logger.info("KeyWord not found in clipboard.");
            }

        } catch (UnsupportedFlavorException | IOException e) {
            logger.error("Failed to access clipboard content", e);
        }
    }
}
