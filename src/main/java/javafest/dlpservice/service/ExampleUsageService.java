package javafest.dlpservice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javafest.dlpservice.model.Rule;
import javafest.dlpservice.repository.RuleRepository;

@Service
public class ExampleUsageService {

    @Autowired
    private RuleRepository ruleRepository;

    @Autowired
    private KeywordSearchService keywordSearchService;

    @Autowired
    private RegexSearchService regexSearchService;

    @Scheduled(fixedRate = 10000)
    @Async
    public void processFiles() {
        String directory = "D:/therap/NinjaShield-Backend";
        String fileName = "ssn.txt";

        // Fetch rules from MongoDB
        List<Rule> rules = ruleRepository.findAll();

        // Process each rule
        for (Rule rule : rules) {
            boolean keywordFound = false;
            boolean regexFound = false;

            // Process each element in the rule
            for (Rule.Element element : rule.getElements()) {
                // Check if the element type is "keyword"
                if ("keyword".equalsIgnoreCase(element.getType())) {
                    // Use KeywordSearchService to check for keywords in the file
                    for (String keyword : element.getText()) {
                        if (keywordSearchService.searchKeywordInFile(directory, fileName, keyword)) {
                            keywordFound = true;
                            System.out.println("!!!!!!!!!!!!!!!Keyword found: " + rule.getName());
                        }
                    }
                }
                // Check if the element type is "regex"
                if ("regex".equalsIgnoreCase(element.getType())) {
                    // Use RegexSearchService to check for regex patterns in the file
                    for (String pattern : element.getText()) {
                        if (regexSearchService.searchRegexInFile(directory, fileName, pattern)) {
                            regexFound = true;
                            System.out.println("!!!!!!!!!!!!!!!!!Regex pattern matched: " + rule.getName());
                        }
                    }
                }
            }

            // Logging the overall results for the rule
            // if (keywordFound || regexFound) {
            //     System.out.println("Rule matched: " + rule.getName());
            // } else {
            //     System.out.println("No match found for rule: " + rule.getName());
            // }
        }
    }
}
