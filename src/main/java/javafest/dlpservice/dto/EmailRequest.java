package javafest.dlpservice.dto;

import java.util.List;

import lombok.Data;

@Data
public class EmailRequest {
    private String filePath;
    private List<String> recipients;
    private String body;
}
