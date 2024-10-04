package javafest.dlpservice.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

import javafest.dlpservice.dto.Policy;
import javafest.dlpservice.dto.Rule;

@Service
public class RuleService {

    private Map<String, List<Rule>> destinationRules = new HashMap<>();
    
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
    }

    public List<Rule> getRules(String destination) {
        return destinationRules.get(destination);
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
    }
}
