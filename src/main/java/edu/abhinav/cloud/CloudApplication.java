package edu.abhinav.cloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication (scanBasePackages = {"edu.abhinav.cloud.controller", "edu.abhinav.cloud.pojo", 
											"edu.abhinav.cloud.repository", "edu.abhinav.cloud.service",
											"edu.abhinav.cloud.config", "edu.abhinav.cloud.exceptions",
											"edu.abhinav.cloud.validations"})
public class CloudApplication {
	public static void main(String[] args) {
		SpringApplication.run(CloudApplication.class, args);
	}

}
