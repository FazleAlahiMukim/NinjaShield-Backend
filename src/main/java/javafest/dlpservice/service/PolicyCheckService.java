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
import javafest.dlpservice.dto.FileCategoryAction;
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
        return execute(destination, filepath, null, null);
    }

    public Action getActionForText(String destination, String text) {
        return execute(destination, null, text, null);
    }

    public Action getActionForWeb(String destination, String text, String fileName, String uploadUrl) {
        return execute(destination, fileName, text, uploadUrl);
    }

    private Action execute(String destination, String filepath, String text, String destinationValue) {
        if (!ruleService.getPresentDestinations().contains(destination)) {
            return null;
        }

        Rule rule = getViolatingRule(destination, filepath, text);
        if (rule == null) {
            Action result = isFileCategoryMatch(destination, filepath);
            if (result != null) {
                notificationService.showNotification(destination, result.getAction(), filepath);
                return result;
            }
            return null;
        }

        logEvent(rule, filepath, rule.getViolationOccurences(), destination, destinationValue);

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
        if (rules == null) {
            return null;
        }

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

    private Action isFileCategoryMatch(String destination, String filePath) {
        List<FileCategoryAction> fileCategorieActions = ruleService.getFileCategories(destination);
        if (fileCategorieActions == null) {
            return null;
        }

        for (FileCategoryAction fileCategoryAction : fileCategorieActions) {
            String fileCategory = fileCategoryAction.getFileCategory();

            if (fileCategory.equals("Images")) {
                if (filePath.endsWith(".jpg") || filePath.endsWith(".jpeg") || filePath.endsWith(".png") || filePath.endsWith(".gif")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } else if (fileCategory.equals("Audio files")) {
                if (filePath.endsWith(".mp3") || filePath.endsWith(".wav") || filePath.endsWith(".flac")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } else if (fileCategory.equals("Video files")) {
                if (filePath.endsWith(".mp4") || filePath.endsWith(".avi") || filePath.endsWith(".mkv")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } else if (fileCategory.equals("Executable files")) {
                if (filePath.endsWith(".exe") || filePath.endsWith(".msi") || filePath.endsWith(".bat")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } else if (fileCategory.equals("Compressed files")) {
                if (filePath.endsWith(".zip") || filePath.endsWith(".rar") || filePath.endsWith(".7z")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } else if (fileCategory.equals("Configuration files (.ini, .log, .env ...)")) {
                return new Action(fileCategoryAction.getAction());
            } else if (fileCategory.equals("Spreadsheets")) {
                if (filePath.endsWith(".xls") || filePath.endsWith(".xlsx") || filePath.endsWith(".ods")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } else if (fileCategory.equals("Presentations")) {
                if (filePath.endsWith(".ppt") || filePath.endsWith(".pptx") || filePath.endsWith(".odp")) {
                    return new Action(fileCategoryAction.getAction());
                }
            } 
        }
        return null;
    }

    private void logEvent(Rule rule, String filePath, int occurrences, String destinationType, String destinationValue) {
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
                                destinationValue, 
                                filePath, 
                                new Date());
        
        apiService.saveEvent(event);
    }
}
