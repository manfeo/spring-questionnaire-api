package com.example.questionnaire_demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class QuestionnaireDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(QuestionnaireDemoApplication.class, args);
	}

}
