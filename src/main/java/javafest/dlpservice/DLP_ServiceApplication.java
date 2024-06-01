package javafest.dlpservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling
public class DLP_ServiceApplication {

	public static void main(String[] args) {
		System.setProperty("java.awt.headless", "false");
		SpringApplication.run(DLP_ServiceApplication.class, args);
	}

	@Component
	public class AppRunner implements CommandLineRunner {

		@Override
		public void run(String... args) throws Exception {
			// start the services manually

		}
	}

}
