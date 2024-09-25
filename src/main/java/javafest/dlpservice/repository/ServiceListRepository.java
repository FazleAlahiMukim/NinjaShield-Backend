package javafest.dlpservice.repository;

import javafest.dlpservice.model.ServiceList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ServiceListRepository extends MongoRepository<ServiceList, String> {
    
    Optional<ServiceList> findByServiceId(String serviceId);
    
}
