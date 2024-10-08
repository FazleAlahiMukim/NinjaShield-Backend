package javafest.dlpservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FileCategoryAction {
    private String fileCategory;
    private String action;
}