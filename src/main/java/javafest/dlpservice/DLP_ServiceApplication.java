package javafest.dlpservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import javafx.application.Platform;
import jakarta.annotation.PostConstruct;
import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableScheduling
public class DLP_ServiceApplication {
    private static CountDownLatch javafxLatch = new CountDownLatch(1);

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        initializeJavaFX();
        
        SpringApplication.run(DLP_ServiceApplication.class, args);
    }

    private static void initializeJavaFX() {
        try {
            Platform.startup(() -> {
                javafxLatch.countDown();
            });
            javafxLatch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (IllegalStateException e) {
            javafxLatch.countDown();
        }
    }

    @PostConstruct
    public void init() {
        Platform.setImplicitExit(false);
    }
}