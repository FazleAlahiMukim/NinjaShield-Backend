package javafest.dlpservice.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "devices")
public class Device {
    @Id
    private String deviceId;
    private String userId;
    private String email;
    private String name;
    private int events;
}