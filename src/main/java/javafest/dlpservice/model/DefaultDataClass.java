package javafest.dlpservice.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Document(collection = "default_data_class")
public class DefaultDataClass {
    @Id
    private String id;
    private String name;
    private String description;
    private List<Rule> rules;
}