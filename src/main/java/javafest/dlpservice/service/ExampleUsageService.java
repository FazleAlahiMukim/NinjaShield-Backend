package javafest.dlpservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class ExampleUsageService {

    @Autowired
    private KeywordSearchService keywordSearchService;

    @Autowired
    private RegexSearchService regexSearchService;

    @PostConstruct
    public void processFiles() {
        String directory = "D:/therap/NinjaShield-Backend";
        String fileName = "ssn.txt";

        // Search for a keyword
        boolean keywordFound = keywordSearchService.searchKeywordInFile(directory, fileName, "Health Insurance Number");

        // Search for a regex
        boolean regexFound = regexSearchService.searchRegexInFile(directory, fileName, "\\d{3}-\\d{2}-\\d{4}");

        // Handle results
        if (keywordFound) {
            System.out.println("Keyword found!");
        } else if (regexFound) {
            System.out.println("Regex pattern found!");
        } else {
            System.out.println("Nothing found.");
        }
    }
}
