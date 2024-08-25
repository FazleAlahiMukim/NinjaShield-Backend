package javafest.dlpservice.dbservices;

import javafest.dlpservice.model.Device;
import javafest.dlpservice.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DemoDBService {

    @Autowired
    private DeviceRepository deviceRepository;

    public List<Device> getDevicesByUserId(String userId) {
        return deviceRepository.findByUserId(userId);
    }
}
