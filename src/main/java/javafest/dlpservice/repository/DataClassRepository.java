package javafest.dlpservice.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import javafest.dlpservice.model.DataClass;

public interface DataClassRepository extends MongoRepository<DataClass, String> {

    List<DataClass> findByUserId(String userId);

    boolean existsByUserIdAndDataId(String userId, String dataId);
}
