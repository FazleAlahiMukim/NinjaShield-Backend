package javafest.dlpservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import javafest.dlpservice.model.ServiceRegistry;
import java.util.Optional;

public interface ServiceRegistryRepository extends MongoRepository<ServiceRegistry, String> {
    
    Optional<ServiceRegistry> findByDeviceID(String deviceID);
    
}
