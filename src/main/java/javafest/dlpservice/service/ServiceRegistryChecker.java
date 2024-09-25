package javafest.dlpservice.service;

import javafest.dlpservice.model.ServiceRegistry;
import javafest.dlpservice.model.ServiceRegistry.ServiceStatus;
import javafest.dlpservice.repository.ServiceRegistryRepository;
import javafest.dlpservice.repository.ServiceListRepository;
import javafest.dlpservice.model.ServiceList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

// @Service
public class ServiceRegistryChecker {

    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryChecker.class);

    @Autowired
    private ServiceRegistryRepository serviceRegistryRepository;

    @Autowired
    private ServiceListRepository serviceListRepository;

    public void checkServiceRegistry(String deviceID) {
        // Retrieve the ServiceRegistry for the given deviceID
        Optional<ServiceRegistry> registryEntry = serviceRegistryRepository.findByDeviceID(deviceID);

        if (registryEntry.isPresent()) {
            ServiceRegistry serviceRegistry = registryEntry.get();

            // Get the serviceStatus map from the serviceRegistry
            Map<String, ServiceStatus> serviceStatusMap = serviceRegistry.getServiceStatus();

            // Iterate through the serviceStatus map dynamically
            for (Map.Entry<String, ServiceStatus> entry : serviceStatusMap.entrySet()) {
                String serviceKey = entry.getKey(); // e.g., clipboard, onedrivemon, usbmon
                ServiceStatus status = entry.getValue();

                // Fetch additional service details from service_list using serviceId
                Optional<ServiceList> serviceListEntry = serviceListRepository.findByServiceId(status.getServiceId());

                if (serviceListEntry.isPresent()) {
                    ServiceList service = serviceListEntry.get();

                    // Log service details
                    logger.info("Service: {}", service.getServiceName());
                    logger.info("Service Description: {}", service.getDescription());
                    logger.info("Service Active: {}", status.isActive());
                    logger.info("Last Checked: {}", status.getLastChecked());
                    logger.info("Service ID: {}", status.getServiceId());
                } else {
                    logger.warn("Unknown service for serviceId: {}", status.getServiceId());
                }
            }
        } else {
            logger.warn("No service registry found for deviceID: {}", deviceID);
        }
    }
}
