package javafest.dlpservice.model;

import java.util.Map;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "service_registry")
public class ServiceRegistry {

    @Id
    private String id;
    private String deviceID;
    private Map<String, ServiceStatus> serviceStatus;  // To hold multiple services like clipboard, onedrivemon, usbmon
    private Date lastUpdated;

    @Data
    @NoArgsConstructor
    public static class ServiceStatus {
        private boolean isActive;
        private Date lastChecked;
        private String serviceId;
    }
}
