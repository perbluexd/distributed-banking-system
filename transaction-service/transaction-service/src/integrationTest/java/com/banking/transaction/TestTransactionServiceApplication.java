package com.banking.transaction;

import org.springframework.boot.SpringApplication;
import org.testcontainers.utility.TestcontainersConfiguration;

public class TestTransactionServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(TransactionServiceApplication::main)
				.with(TestcontainersConfiguration.class)
				.run(args);
	}
}