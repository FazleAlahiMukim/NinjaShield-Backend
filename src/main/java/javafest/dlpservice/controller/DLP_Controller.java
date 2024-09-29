package javafest.dlpservice.controller;

import org.springframework.web.bind.annotation.*;

import javafest.dlpservice.dto.FilePathRequest;
import javafest.dlpservice.dto.TextRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DLP_Controller {

    private static final Logger logger = LoggerFactory.getLogger(DLP_Controller.class);


    @PostMapping("/blkupload")
    public Map<String, Boolean> checkFile(@RequestBody Map<String, String> request) {
        String fileContent = request.get("fileContent");
        boolean containsKeyword = fileContent.contains("secretKeyword");
        System.out.println("File contains 'secretKeyword': " + containsKeyword);
        Map<String, Boolean> response = new HashMap<>();
        response.put("containsKeyword", containsKeyword);
        return response;
    }

    @PostMapping("/checkEmail")
    public String checkEmail(@RequestBody FilePathRequest request) {
        String filePath = request.getFilePath();
        logger.info("Checking email for file: " + filePath);
        return "Cancel";
    }

    @PostMapping("/screenshot")
    public String screenshot(@RequestBody TextRequest request) {
        String text = request.getText();
        logger.info("Checking screenshot for text: " + text);
        if (text.contains("confidential"))
            return "Cancel";
        return "OK";
    }

    @PostMapping("/screenshare")
    public String screenshare(@RequestBody TextRequest request) {
        String text = request.getText();
        logger.info("Checking screenshot for text: " + text);
        if (text.contains("confidential"))
            return "Cancel";
        return "OK";
    }

}
