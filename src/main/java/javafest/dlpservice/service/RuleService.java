package javafest.dlpservice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import javafest.dlpservice.dto.FileCategoryAction;
import javafest.dlpservice.dto.Policy;
import javafest.dlpservice.dto.Rule;

@Service
public class RuleService {

    private Map<String, List<Rule>> destinationRules = new HashMap<>();
    private Map<String, List<FileCategoryAction>> destinationFileCategories = new HashMap<>();
    
    private Set<String> presentDestinations = new HashSet<>();

    public void addRules(String destination, List<Rule> rules, Policy policy) {
        for (Rule rule : rules) {
            rule.setPolicy(policy);
        }
        
        if (destinationRules.containsKey(destination)) {
            destinationRules.get(destination).addAll(rules);
        } else {
            destinationRules.put(destination, rules);
        }

        if (destinationFileCategories.containsKey(destination)) {
            for (String fileCategory : policy.getFileCategories()) {
                destinationFileCategories.get(destination).add(new FileCategoryAction(fileCategory, policy.getAction()));
            }
        } else {
            List<FileCategoryAction> fileCategories = new ArrayList<>();
            for (String fileCategory : policy.getFileCategories()) {
                fileCategories.add(new FileCategoryAction(fileCategory, policy.getAction()));
            }
            destinationFileCategories.put(destination, fileCategories);
        }
    }

    public List<Rule> getRules(String destination) {
        return destinationRules.get(destination);
    }

    public List<FileCategoryAction> getFileCategories(String destination) {
        return destinationFileCategories.get(destination);
    }
    
    public void addDestination(String destination) {
        presentDestinations.add(destination);
    }

    public Set<String> getPresentDestinations() {
        return presentDestinations;
    }

    public void clear() {
        presentDestinations.clear();
        destinationRules.clear();
        destinationFileCategories.clear();
    }
}
