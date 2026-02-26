package com.relatosPapel.buscador;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BuscadorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BuscadorApplication.class, args);
		String profile = System.getenv("PROFILE");
		System.setProperty("spring.profiles.active", profile != null ? profile : "default");
		// Railway's internal interface takes some time to start. We wait for it to be ready.
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		SpringApplication.run(BuscadorApplication.class, args);
	}

}
