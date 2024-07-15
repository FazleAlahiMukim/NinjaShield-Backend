package javafest.dlpservice.controller;

import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DlpController {

    @PostMapping("/check")
    public Map<String, Boolean> checkFile(@RequestBody Map<String, String> request) {
        String fileContent = request.get("fileContent");
        boolean containsKeyword = fileContent.contains("secretKeyword");
        System.out.println("File contains 'secretKeyword': " + containsKeyword);
        Map<String, Boolean> response = new HashMap<>();
        response.put("containsKeyword", containsKeyword);
        return response;
    }
}
