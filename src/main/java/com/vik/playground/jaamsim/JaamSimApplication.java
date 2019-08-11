package com.vik.playground.jaamsim;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class JaamSimApplication {

	public static void main(String[] args) {

		SpringApplicationBuilder builder = new SpringApplicationBuilder(JaamSimApplication.class);
		builder.headless(false).run(args);
	}

}
