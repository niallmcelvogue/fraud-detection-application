package com.example.fraud_detection_application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.Clock;
import java.time.ZoneId;

@SpringBootApplication
public class FraudDetectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(FraudDetectionApplication.class, args);
	}

	@Bean
	Clock clock() {
		return Clock.system(ZoneId.of("Europe/London"));
	}

}
