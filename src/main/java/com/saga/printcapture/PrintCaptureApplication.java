package com.saga.printcapture;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.saga"})
public class PrintCaptureApplication {

	public static void main(String[] args) {
		SpringApplication application = new SpringApplication(PrintCaptureApplication.class);
		application.run(PrintCaptureApplication.class, args);
	}
}
