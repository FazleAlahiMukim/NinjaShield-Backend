package javafest.dlpservice.dto;

import lombok.Data;

@Data
public class FileUploadRequest {
    private String fileName;
    private String fileType;
    private String fileContent;
    private String uploadUrl;
}
