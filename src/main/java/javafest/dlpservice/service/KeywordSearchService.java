package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@Async
public class KeywordSearchService {

    private static final Logger logger = LoggerFactory.getLogger(KeywordSearchService.class);

    public boolean searchKeywordInFile(String directory, String fileName, String keyword) {
        Path filePath = Paths.get(directory, fileName);

        if (!Files.exists(filePath)) {
            logger.warn("File not found: " + filePath.toString());
            return false;
        }

        try {
            String content = new String(Files.readAllBytes(filePath));
            boolean found = content.contains(keyword);
            // logger.info("Keyword '{}' found in file '{}': {}", keyword, fileName, found);
            return found;
        } catch (IOException e) {
            // logger.error("Error reading file: " + filePath.toString(), e);
            return false;
        }
    }
}
