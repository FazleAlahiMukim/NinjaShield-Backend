package javafest.dlpservice.service;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javafest.dlpservice.dto.Rule;
import javafest.dlpservice.dto.Action;
import javafest.dlpservice.dto.Element;
import javafest.dlpservice.dto.Event;
import javafest.dlpservice.dto.Policy;
import javafest.dlpservice.utils.SearchUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PolicyCheckService {

    @Autowired
    private RuleService ruleService;

    @Autowired
    private ApiService apiService;

    @Autowired
    private NotificationService notificationService;

    private static final Logger logger = LoggerFactory.getLogger(PolicyCheckService.class);

    public Action getActionForFile(String destination, String filepath) {
        return execute(destination, filepath, null);
    }

    public Action getActionForText(String destination, String text) {
        return execute(destination, null, text);
    }

    private Action execute(String destination, String filepath, String text) {
        if (!ruleService.getPresentDestinations().contains(destination)) {
            return null;
        }

        Rule rule = getViolatingRule(destination, filepath, text);
        if (rule == null) {
            return null;
        }

        logEvent(rule, filepath, rule.getViolationOccurences(), destination);

        String action = rule.getPolicy().getAction();
        
        notificationService.showNotification(destination, action, filepath);
        
        return new Action(action);
    }

    private Rule getViolatingRule(String destination, String filepath, String text) {
        if (text == null) {
            File file = new File(filepath);
            if (!file.exists()) {
                logger.error("File does not exist");
                return null;
            }
            text = SearchUtil.extractText(file);
            if (text == null) {
                logger.error("Error extracting text from file");
                return null;
            }
        }

        List<Rule> rules = ruleService.getRules(destination);
        for (Rule rule : rules) {
            int occurrences = rule.getOccurrences();
            List<Element> elements = rule.getElements();

            int totalCount = 0;
            boolean doContinue = false;
            for (Element element : elements) {
                int count = 0;
                if (element.getType().equals("keyword")) {
                    count = SearchUtil.searchKeywords(text, element.getText());
                } else if (element.getType().equals("regex")) {
                    count = SearchUtil.searchRegex(text, element.getText());
                }
                if (count == 0) {
                    doContinue = true;
                    break;
                }
                totalCount += count;
            }

            if (doContinue)
                continue;

            if (totalCount >= occurrences) {
                logger.info("Rule violated: " + rule.getName());
                rule.setViolationOccurences(totalCount);
                return rule;
            }
        }
        return null;
    }

    private void logEvent(Rule rule, String filePath, int occurrences, String destinationType) {
        Policy policy = rule.getPolicy();
        String fileName = null;
        if (filePath != null)
            fileName = new File(filePath).getName();

        Event event = new Event(apiService.getDeviceId(), 
                                policy.getRisk(), 
                                policy.getAction(), 
                                policy.getName(), 
                                rule.getRuleId(),
                                occurrences, 
                                fileName, 
                                destinationType, 
                                null, 
                                filePath, 
                                new Date());
        
        apiService.saveEvent(event);
    }
}
