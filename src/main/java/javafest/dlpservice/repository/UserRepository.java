package javafest.dlpservice.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import javafest.dlpservice.model.User;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
    boolean existsByUserIdAndEmail(String userId, String email);
}
