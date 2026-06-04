package com.springinfra;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SpringInfraApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringInfraApplication.class, args);
	}

}
