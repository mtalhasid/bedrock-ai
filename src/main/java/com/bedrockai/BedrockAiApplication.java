package com.bedrockai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class BedrockAiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BedrockAiApplication.class, args);
	}

}
