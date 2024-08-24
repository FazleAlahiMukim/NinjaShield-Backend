package javafest.dlpservice.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import javafest.dlpservice.model.Device;

public interface DeviceRepository extends MongoRepository<Device, String> {

    List<Device> findByUserId(String userId);
    List<Device> findByEmail(String email);
}
