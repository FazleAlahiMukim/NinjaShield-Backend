package javafest.dlpservice.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Component
public class KeyRegSearchUtil {

    private static final Logger logger = LoggerFactory.getLogger(KeyRegSearchUtil.class);

    // Search for a keyword in the file
    public boolean searchKeywordInFile(String directory, String fileName, String keyword) {
        Path filePath = Paths.get(directory, fileName);

        if (!Files.exists(filePath)) {
            logger.warn("File not found: " + filePath.toString());
            return false;
        }

        try {
            String content = new String(Files.readAllBytes(filePath));
            boolean found = content.contains(keyword);
            logger.info("Keyword '{}' found in file '{}': {}", keyword, fileName, found);
            return found;
        } catch (IOException e) {
            logger.error("Error reading file: " + filePath.toString(), e);
            return false;
        }
    }

    // Search for a regex pattern in the file
    public boolean searchRegexInFile(String directory, String fileName, String regex) {
        Path filePath = Paths.get(directory, fileName);

        if (!Files.exists(filePath)) {
            logger.warn("File not found: " + filePath.toString());
            return false;
        }

        try {
            String content = new String(Files.readAllBytes(filePath));
            boolean found = Pattern.compile(regex).matcher(content).find();
            logger.info("Regex '{}' found in file '{}': {}", regex, fileName, found);
            return found;
        } catch (IOException e) {
            logger.error("Error reading file: " + filePath.toString(), e);
            return false;
        }
    }
}
