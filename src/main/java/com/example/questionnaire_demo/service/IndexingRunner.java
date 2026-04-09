package com.example.questionnaire_demo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class IndexingRunner implements CommandLineRunner {

    private final IndexingService indexingService;

    @Override
    @Async
    public void run(String... args) {
        log.info("Executing full re-indexing on application startup (async)");
        try {
            indexingService.indexQuestionnaire();
            log.info("Startup indexing completed successfully");
        } catch (InterruptedException e) {
            log.error("Indexing was interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            log.error("Indexing failed with error", e);
        }
    }
}
