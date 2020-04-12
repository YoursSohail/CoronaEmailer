package com.yourssohail.coronamailer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CoronaMailerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoronaMailerApplication.class, args);
	}

}
