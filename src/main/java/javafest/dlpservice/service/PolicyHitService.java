package javafest.dlpservice.service;

import javafest.dlpservice.model.Policy;
import javafest.dlpservice.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

// @Service
public class PolicyHitService {

    @Autowired
    private PolicyRepository policyRepository;

    public void retrieveDataClassesByUserId(String userId) {
        // Fetch all policies for the given userId
        List<Policy> policies = policyRepository.findByUserId(userId);

        // Loop through each policy
        for (Policy policy : policies) {
            System.out.println("Policy Name: " + policy.getName());

            // Get the dataClasses for each policy
            List<String> dataClassIds = policy.getDataClasses();

            if (dataClassIds != null && !dataClassIds.isEmpty()) {
                System.out.println("Data Classes found for Policy '" + policy.getName() + "':");
                for (String dataClassId : dataClassIds) {
                    System.out.println("  DataClass ID: " + dataClassId);
                }
            } else {
                System.out.println("No Data Classes found for Policy '" + policy.getName() + "'");
            }
        }
    }
}
