package javafest.dlpservice.model;

import java.util.Date;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "policies")
public class Policy {
    @Id
    private String policyId;
    private String userId;
    @JsonProperty("isActive")
    private boolean isActive;
    private String risk;
    private String action;
    private String name;
    private int events;
    private Date lastUpdated;
    private List<String> fileCategories;
    private List<String> dataClasses;
    private List<String> destinations;
}