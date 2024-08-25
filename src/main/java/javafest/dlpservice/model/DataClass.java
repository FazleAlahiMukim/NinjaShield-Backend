package javafest.dlpservice.model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "data_class")
public class DataClass {
    @Id
    private String dataId;
    private String userId;
    @JsonProperty("isActive")
    private boolean isActive;
    private String name;
    private String description;
    private int events;
    private Date lastUpdated;
}