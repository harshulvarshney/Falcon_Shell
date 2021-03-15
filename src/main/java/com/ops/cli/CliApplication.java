package com.ops.cli;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author harshul.varshney
 *
 */
@Slf4j
@SpringBootApplication
public class CliApplication {

	public static void main(String[] args) {
		log.info("Starting Application:::::::");
		SpringApplication.run(CliApplication.class, args);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.info("@@@@@@@@@@@@@@@@@@@@@@@@@ APP IS SHUTTING DOWN @@@@@@@@@@@@@@@@@@@@@@@@@");
			}
		});
	}

}
