package com.cloudq.cloudq;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.cloudq.cloudq.repository")  // Enables JPA repositories in the specified package
public class CloudqApplication {

	public static void main(String[] args) {
		SpringApplication.run(CloudqApplication.class, args);
	}

}
