package javafest.dlpservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "service_list")
public class ServiceList {

    @Id
    private String serviceId;
    private String serviceName;
    private String description;
    private Config config;
    private boolean isCritical;

    @Data
    @NoArgsConstructor
    public static class Config {
        private int scanInterval;
        private int alertThreshold;
    }
}
