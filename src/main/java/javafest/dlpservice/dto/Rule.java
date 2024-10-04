package javafest.dlpservice.dto;

import java.util.List;

import lombok.Data;

@Data
public class Rule {
    private String ruleId;
    private String dataId;
    private String name;
    private int occurrences;
    private List<Element> elements;
    private Policy policy;
    private int violationOccurences;
}