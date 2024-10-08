package javafest.dlpservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javafest.dlpservice.dto.Action;
import javafest.dlpservice.dto.EmailRequest;
import javafest.dlpservice.dto.FilePathRequest;
import javafest.dlpservice.dto.FileUploadRequest;
import javafest.dlpservice.dto.TextRequest;
import javafest.dlpservice.service.PolicyCheckService;
import javafest.dlpservice.utils.SearchUtil;

import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class DLP_Controller {

    @Autowired
    private PolicyCheckService policyCheckService;

    private static final Logger logger = LoggerFactory.getLogger(DLP_Controller.class);
    
    @PostMapping("/webUpload")
    public String webUpload(@RequestBody FileUploadRequest request) {
        logger.info("Web upload detected for file: " + request.getFileName());
        
        String fileContent = request.getFileContent();

        if (request.getFileType().equals("pdf")) {
            byte[] pdfBytes = Base64.getDecoder().decode(fileContent);
            fileContent = SearchUtil.extractTextFromPdf(pdfBytes);
        } else if (request.getFileType().equals("docx")) {
            byte[] docxBytes = Base64.getDecoder().decode(fileContent);
            fileContent = SearchUtil.extractTextFromDocx(docxBytes);
        }

        if (fileContent == null) {
            logger.error("Failed to extract text from file");
            return "OK";
        }

        Action action = policyCheckService.getActionForWeb("Web", fileContent, request.getFileName(), request.getUploadUrl());

        if (action != null && action.getAction().equals("block")) {
            return "Cancel";
        }
        return "OK";
    }

    @PostMapping("/checkEmail")
    public String checkEmail(@RequestBody EmailRequest request) {
        String filePath = request.getFilePath();
        String body = request.getBody();
        Action action = null;

        if (filePath != null) {
            logger.info("Checking email for file: " + filePath);
            action = policyCheckService.getActionForFile("Email", filePath);
        } else if (body != null) {
            logger.info("Checking email for text: " + body);
            action = policyCheckService.getActionForText("Email", body);
        }

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }

    @PostMapping("/checkOnedrive")
    public String checkOnedrive(@RequestBody FilePathRequest request) {
        String filePath = request.getFilePath();
        logger.info("Checking OneDrive for file: " + filePath);
        Action action = policyCheckService.getActionForFile("Onedrive", filePath);

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }

    @PostMapping("/checkClipboard")
    public String checkClipboard(@RequestBody TextRequest request) {
        String text = request.getText();
        Action action = policyCheckService.getActionForText("Clipboard", text);

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }
    

    @PostMapping("/printJob")
    public String checkPrint(@RequestBody FilePathRequest request) {
        String filePath = request.getFilePath();
        logger.info("Checking print job for file: " + filePath);
        Action action = policyCheckService.getActionForFile("Printer", filePath);

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }

    @PostMapping("/printWord")
    public String checkPrintWord(@RequestBody FilePathRequest request) {
        String filePath = request.getFilePath();
        logger.info("Checking print for file: " + filePath);
        Action action = policyCheckService.getActionForFile("Printer", filePath);

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }

    @PostMapping("/screenshot")
    public String screenshot(@RequestBody TextRequest request) {
        logger.info("Checking screenshot for restricted text");
        String text = request.getText();
        Action action = policyCheckService.getActionForText("Screenshot", text);

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }

    @PostMapping("/screenshare")
    public String screenshare(@RequestBody TextRequest request) {
        logger.info("Checking screenshare for restricted text");
        String text = request.getText();
        Action action = policyCheckService.getActionForText("Screenshare", text);

        if (action != null && action.getAction().equals("block"))
            return "Cancel";
        return "OK";
    }

}
