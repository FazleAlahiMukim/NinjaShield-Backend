package javafest.dlpservice.dto;

import java.util.List;

import lombok.Data;

@Data
public class Element {
    private String type;
    private List<String> text;
}