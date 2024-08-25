package javafest.dlpservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
class element {
    private String type;
    private List<String> text;
}

@Data
@NoArgsConstructor
@Document(collection = "rules")
public class Rule {
    @Id
    private String ruleId;
    private String dataId;
    private String name;
    private int occurrences;
    private List<element> elements;
}