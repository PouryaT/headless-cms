package com.pouryat.headless_cms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class HeadlessCmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HeadlessCmsApplication.class, args);
	}

}
