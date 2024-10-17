package com.nv.cpmfcu;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgentDesktopCpmfcuApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgentDesktopCpmfcuApplication.class, args);
	}

}
