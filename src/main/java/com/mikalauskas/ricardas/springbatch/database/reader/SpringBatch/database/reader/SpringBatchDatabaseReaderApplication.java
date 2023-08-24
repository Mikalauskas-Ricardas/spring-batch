package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableBatchProcessing
public class SpringBatchDatabaseReaderApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBatchDatabaseReaderApplication.class, args);
	}

}
