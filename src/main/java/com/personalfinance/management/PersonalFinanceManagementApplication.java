package com.personalfinance.management;

import io.github.cdimascio.dotenv.Dotenv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalFinanceManagementApplication {

	private final static Logger log = LoggerFactory.getLogger(
			PersonalFinanceManagementApplication.class);

	public static void main(String[] args) {
		log.info("APP RUN");
		log.warn("WARN");

		try {
			Dotenv dotenv = Dotenv.load();

			System.setProperty("DB_URL", dotenv.get("DB_URL"));
			System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
			System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));

			SpringApplication.run(PersonalFinanceManagementApplication.class, args);
		} catch (Exception e) {
			log.error("ERROR", e);
		}
	}

}
