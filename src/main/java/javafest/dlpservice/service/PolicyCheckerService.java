package javafest.dlpservice.service;

import javafest.dlpservice.model.Policy;
import javafest.dlpservice.model.Rule;
import javafest.dlpservice.repository.PolicyRepository;
import javafest.dlpservice.repository.RuleRepository;
import javafest.dlpservice.repository.DataClassRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PolicyCheckerService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyCheckerService.class);

    private final PolicyRepository policyRepository;
    private final RuleRepository ruleRepository;
    private final DataClassRepository dataClassRepository;

    // Destination to service name mapping
    private static final Map<String, String> destinationServiceMap = new HashMap<>();

    // Active services tracking
    private Map<String, Boolean> activeServices = new HashMap<>();

    static {
        destinationServiceMap.put("Removable storage", "USBDetectorService.java");
        destinationServiceMap.put("Onedrive", "OneDriveMonitorService.java");
        destinationServiceMap.put("Email", "EmailMonitorService.java");
        destinationServiceMap.put("Printer", "PrinterJobService.java");
        destinationServiceMap.put("Web", "ExtensionService.java");
        destinationServiceMap.put("Screenshare", "ScreenShareService.java");
        destinationServiceMap.put("Screenshot", "ScreenshotService.java");
    }

    // This will store the destination types and associated keywords/regex for each policy
    private Map<String, Map<String, List<String>>> policyStore = new HashMap<>();

    public PolicyCheckerService(PolicyRepository policyRepository, RuleRepository ruleRepository, DataClassRepository dataClassRepository) {
        this.policyRepository = policyRepository;
        this.ruleRepository = ruleRepository;
        this.dataClassRepository = dataClassRepository;
    }

    // Runs every 30 seconds
    @Scheduled(fixedRate = 30000)
    public void recheckAndUpdatePolicies() {
        logger.info("Starting policy recheck and update process...");

        // Fetch all active policies
        List<Policy> activePolicies = policyRepository.findAll();
        logger.info("Found {} active policies.", activePolicies.size());

        // Clear the store before reloading
        policyStore.clear();
        logger.info("Cleared the policy store.");

        // Process each policy
        for (Policy policy : activePolicies) {
            logger.info("Processing policy: {}", policy.getName());
            storePolicyData(policy);
        }

        logger.info("Policy recheck and update process completed.");
        manageServices(); // Manage services based on the updated policies
    }

    private void storePolicyData(Policy policy) {
        Map<String, List<String>> destinationData = new HashMap<>();

        // Debugging: Show the destinations associated with the policy
        logger.info("Policy '{}' has the following destinations: {}", policy.getName(), policy.getDestinations());

        // Get the rules associated with the policy by looking at the dataClass
        List<String> dataClasses = policy.getDataClasses();
        logger.info("Policy '{}' has {} dataClasses.", policy.getName(), dataClasses.size());

        for (String dataId : dataClasses) {
            logger.info("Fetching rules for dataClass with dataId: {}", dataId);
            List<Rule> rules = ruleRepository.findByDataId(dataId);
            logger.info("Found {} rules for dataClass with dataId: {}", rules.size(), dataId);

            // Collect keywords and regex for each rule
            for (Rule rule : rules) {
                logger.info("Processing rule: {}", rule.getName());

                for (Rule.Element element : rule.getElements()) {
                    logger.info("Element type: {}, Text: {}", element.getType(), element.getText());

                    // For each destination, map it to the corresponding service name
                    for (String destination : policy.getDestinations()) {
                        String serviceName = destinationServiceMap.get(destination);
                        if (serviceName != null) {
                            logger.info("Mapped destination '{}' to service '{}'", destination, serviceName);
                            destinationData.put(serviceName, element.getText());
                        } else {
                            logger.warn("No service found for destination: {}", destination);
                        }
                    }
                }
            }
        }

        // Store destination data mapped to the policy's name
        policyStore.put(policy.getName(), destinationData);
        logger.info("Stored data for policy: {}", policy.getName());
    }

    // Manage the starting and stopping of services based on the updated policies
    private void manageServices() {
        // Loop through the services in the destinationServiceMap
        for (Map.Entry<String, String> entry : destinationServiceMap.entrySet()) {
            String destination = entry.getKey();
            String serviceName = entry.getValue();

            boolean serviceNeeded = isServiceNeeded(serviceName);

            if (serviceNeeded) {
                if (!activeServices.getOrDefault(serviceName, false)) {
                    // Start the service if it is needed but not already running
                    startService(serviceName);
                    activeServices.put(serviceName, true);
                }
            } else {
                if (activeServices.getOrDefault(serviceName, false)) {
                    // Stop the service if it is no longer needed
                    stopService(serviceName);
                    activeServices.put(serviceName, false);
                }
            }
        }
    }

    private boolean isServiceNeeded(String serviceName) {
        // Check if any policy requires the service to be running
        for (Map.Entry<String, Map<String, List<String>>> entry : policyStore.entrySet()) {
            if (entry.getValue().containsKey(serviceName)) {
                return true;
            }
        }
        return false;
    }

    // Mock methods to start and stop services (replace with actual service logic)
    private void startService(String serviceName) {
        logger.info("Starting service: {}", serviceName);
        // TODO: Add actual logic to start the service
    }

    private void stopService(String serviceName) {
        logger.info("Stopping service: {}", serviceName);
        // TODO: Add actual logic to stop the service
    }

    // Method to retrieve stored policy data (for testing purposes or future use)
    public Map<String, Map<String, List<String>>> getPolicyStore() {
        logger.info("Retrieving policy store data.");
        return policyStore;
    }
}
