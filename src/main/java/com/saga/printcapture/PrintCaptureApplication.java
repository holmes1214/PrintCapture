package com.saga.printcapture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.sagacn"})
@EnableTransactionManagement
@EnableScheduling
public class PrintCaptureApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(PrintCaptureApplication.class);
		ConfigurableApplicationContext context = application.run(PrintCaptureApplication.class, args);
	}
}