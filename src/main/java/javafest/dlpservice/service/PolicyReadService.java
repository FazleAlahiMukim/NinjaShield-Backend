package javafest.dlpservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import javafest.dlpservice.dto.Policy;
import javafest.dlpservice.utils.ProcessUtil;
import reactor.core.publisher.Flux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class PolicyReadService {

    @Autowired
    private ApiService apiService;

    @Autowired
    private RuleService ruleService;

    @Autowired
    private USBDetectorService usbDetectorService;

    private String adminId;
    private List<Policy> policies;
    private static final Logger logger = LoggerFactory.getLogger(PolicyReadService.class);

    @PostConstruct
    public void init() {
        adminId = apiService.getAdmin().block();
        policies = new ArrayList<>();
    }

    @Scheduled(fixedRate = 10000)
    public void readAndUpdatePolicies() {
        if (adminId == null)    return;

        List<Policy> currentPolicies = apiService.getPolicies(adminId).collectList().block();
        if (!currentPolicies.equals(policies)) {
            policies = new ArrayList<>(currentPolicies);
            logger.info("Policies updated");
            updateServices(policies);
        }
    }

    private void updateServices(List<Policy> policies) {
        ruleService.clear();

        for (Policy policy : policies) {
            for (String destination : policy.getDestinations()) {
                ruleService.addDestination(destination);

                Flux.fromIterable(policy.getDataClasses())
                .flatMap(dataId -> apiService.getRules(dataId)
                    .collectList()
                    .doOnNext(rules -> ruleService.addRules(destination, rules, policy))
                )
                .subscribe(); 
            }
        }

        Set<String> presentDestinations = ruleService.getPresentDestinations();

        if (presentDestinations.contains("Removable storage")) {
            usbDetectorService.start();
        } else {
            usbDetectorService.stop();
        }

        if (presentDestinations.contains("Email")) {
            ProcessUtil.start("Email");
        } else {
            ProcessUtil.stop("Email");
        }

        if (presentDestinations.contains("Printer")) {
            ProcessUtil.start("Printer");
        } else {
            ProcessUtil.stop("Printer");
        }

        if (presentDestinations.contains("Screenshare")) {
            ProcessUtil.start("Screenshare");
        } else {
            ProcessUtil.stop("Screenshare");
        }

        if (presentDestinations.contains("Screenshot")) {
            ProcessUtil.start("Screenshot");
        } else {
            ProcessUtil.stop("Screenshot");
        }
    }
}
 