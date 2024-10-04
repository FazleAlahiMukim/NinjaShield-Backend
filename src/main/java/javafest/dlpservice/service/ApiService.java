package javafest.dlpservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javafest.dlpservice.dto.Event;
import javafest.dlpservice.dto.Policy;
import javafest.dlpservice.dto.Rule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import java.time.Duration;

@Service
public class ApiService {

    @Autowired
    private WebClient webClient;

    @Value("${device.id}")
    private String deviceId;

    public String getDeviceId() {
        return deviceId;
    }
    
    public Mono<String> getAdmin() {
        return webClient.get()
                        .uri(uriBuilder -> uriBuilder
                            .path("/api/dlp/admin")
                            .queryParam("deviceId", deviceId)
                            .build())
                        .retrieve()
                        .bodyToMono(String.class)
                        .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(10)))
                        .onErrorResume(e -> {
                            System.err.println("Error fetching admin: " + e.getMessage());
                            return Mono.empty();
                        });
    }

    public Flux<Policy> getPolicies(String adminId) {
        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/api/dlp/policies")
                        .queryParam("userId", adminId)
                        .build())
                    .retrieve()
                    .bodyToFlux(Policy.class)
                    .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(10)))
                    .onErrorResume(e -> {
                        System.err.println("Error fetching policies: " + e.getMessage());
                        return Flux.empty();
                    });
    }

    public Flux<Rule> getRules(String dataId) {
        return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                        .path("/api/dlp/rules")
                        .queryParam("dataId", dataId)
                        .build())
                    .retrieve()
                    .bodyToFlux(Rule.class)
                    .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(10)))
                    .onErrorResume(e -> {
                        System.err.println("Error fetching rules: " + e.getMessage());
                        return Flux.empty();
                    });
    }

    public void saveEvent(Event event) {
        webClient.post()
                .uri("/api/dlp/event")
                .bodyValue(event)
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(Long.MAX_VALUE, Duration.ofSeconds(10)))
                .onErrorResume(e -> {
                    System.err.println("Error saving event: " + e.getMessage());
                    return Mono.empty();
                })
                .subscribe();
    }
}
