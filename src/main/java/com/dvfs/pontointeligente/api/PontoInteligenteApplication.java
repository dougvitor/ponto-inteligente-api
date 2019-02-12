package com.dvfs.pontointeligente.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PontoInteligenteApplication implements CommandLineRunner {
	
	@Autowired
	private AcmeProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(PontoInteligenteApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		System.out.println(this.properties.getUri());
	}

}

