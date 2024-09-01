package javafest.dlpservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

@Service
public class RegexSearchService {

    private static final Logger logger = LoggerFactory.getLogger(RegexSearchService.class);

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
