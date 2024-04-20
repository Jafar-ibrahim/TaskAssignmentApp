package org.example.taskassignmentapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class })
public class TaskAssignmentAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskAssignmentAppApplication.class, args);
	}

}
