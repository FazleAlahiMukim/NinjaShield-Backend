package javafest.dlpservice.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Policy {
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