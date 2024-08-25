package javafest.dlpservice.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import javafest.dlpservice.model.Policy;

public interface PolicyRepository extends MongoRepository<Policy, String> {

    List<Policy> findByUserId(String userId);
}
