package javafest.dlpservice.repository;


import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import javafest.dlpservice.model.Rule;

public interface RuleRepository extends MongoRepository<Rule, String> {

    List<Rule> findByDataId(String dataId);
    List<Rule> findByDataIdIn(List<String> dataIds);
}
