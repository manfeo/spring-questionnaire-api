package com.example.questionnaire_demo.service;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.massindexing.MassIndexer;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.stereotype.Service;
import com.example.questionnaire_demo.model.Questionnaire;

@Service
@RequiredArgsConstructor
@Log4j2
public class IndexingService {

    private final EntityManager entityManager;

    @Transactional
    public void indexQuestionnaire() throws InterruptedException {
        log.info("Started mass indexing for Questionnaire entity");

        SearchSession searchSession = Search.session(entityManager);
        MassIndexer indexer = searchSession.massIndexer(Questionnaire.class)
                .threadsToLoadObjects(4)
                .batchSizeToLoadObjects(50)
                .cacheMode(org.hibernate.CacheMode.IGNORE);

        indexer.startAndWait();
        log.info("Mass indexing for Questionnaire completed");
    }
}

