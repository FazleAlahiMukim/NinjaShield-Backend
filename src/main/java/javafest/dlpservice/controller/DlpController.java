package javafest.dlpservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import javafest.dlpservice.dbservices.DemoDBService;
import javafest.dlpservice.model.Device;
import javafest.dlpservice.service.OCRProcessingService;
import javafest.dlpservice.utils.NotificationUtility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import java.util.List;


@RestController
@RequestMapping("/api")
public class DlpController {

    private static final Logger logger = LoggerFactory.getLogger(DlpController.class);

    @Autowired
    private NotificationUtility notificationUtility;

    @Autowired
    private DemoDBService demoDBService;

    @PostMapping("/blkupload")
    public Map<String, Boolean> checkFile(@RequestBody Map<String, String> request) {
        String fileContent = request.get("fileContent");
        boolean containsKeyword = fileContent.contains("secretKeyword");
        System.out.println("File contains 'secretKeyword': " + containsKeyword);
        Map<String, Boolean> response = new HashMap<>();
        response.put("containsKeyword", containsKeyword);
        return response;
    }

    @GetMapping("/blkemail")
    @Async
    public void showNotification() {
        // Use NotificationUtility to show a notification
        logger.info("Email sending blocked");

        notificationUtility.notifyUser("Email sending blocked!", "Email");
    }

    @GetMapping("/devices/{userId}")
    public List<Device> getDevicesByUserId(@PathVariable String userId) {
        return demoDBService.getDevicesByUserId(userId);
    }

}
