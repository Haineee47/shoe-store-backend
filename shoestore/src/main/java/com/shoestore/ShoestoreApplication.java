package com.shoestore;

import com.shoestore.config.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(JwtProperties.class)
@SpringBootApplication
public class ShoestoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShoestoreApplication.class, args);
	}

}
