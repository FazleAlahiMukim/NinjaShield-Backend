package javafest.dlpservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;

import javafest.dlpservice.model.DefaultDataClass;

public interface DefaultDataClassRepository extends MongoRepository<DefaultDataClass, String> {

}
