package javafest.dlpservice.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Event {
    private String deviceId;
    private String risk;
    private String action;
    private String policyName;
    private String ruleId;
    private int occurrences;
    private String fileName;
    private String destinationType;
    private String destination;
    private String source;
    private Date time;
}
