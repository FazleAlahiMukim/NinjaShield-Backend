package javafest.dlpservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "rules")
public class Rule {

    @Data
    @NoArgsConstructor
    public static class Element {
        private String type;
        private List<String> text;
    }

    @Id
    private String ruleId;
    private String dataId;
    private String name;
    private int occurrences;
    private List<Element> elements;
}
