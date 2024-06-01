package javafest.dlpservice;

// import java.nio.file.Paths;
// import java.util.concurrent.Executor;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// import org.springframework.context.annotation.Bean;
// import org.springframework.scheduling.annotation.EnableAsync;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
// import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.EnableScheduling;

// import javafest.dlpservice.service.USBMonitorService;

@SpringBootApplication
// @EnableAsync
@EnableScheduling
public class DLP_ServiceApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(DLP_ServiceApplication.class, args);
	}

	// @Bean
	// public Executor taskExecutor() {
	// 	ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	// 	executor.setCorePoolSize(5);
	// 	executor.setMaxPoolSize(10);
	// 	executor.setQueueCapacity(50);
	// 	executor.setThreadNamePrefix("DLPService-");
	// 	executor.initialize();
	// 	return executor;
	// }

	// @Component
	// public class AppRunner implements CommandLineRunner {

	// 	@Autowired
	// 	// private USBMonitorService usbMonitorService;

	// 	@Override
	// 	public void run(String... args) throws Exception {
	// 		// usbMonitorService.monitorDirectory(Paths.get("I:/"));

	// 	}
	// }

}
