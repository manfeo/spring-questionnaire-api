package com.example.questionnaire_demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.questionnaire_demo.model.Questionnaire;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    List<Questionnaire> findAllByUserId(Long userId);
    Optional<Questionnaire> findByIdAndUserId(Long id, Long userId);

    @Query("SELECT q FROM Questionnaire q LEFT JOIN FETCH q.uploadedFiles WHERE q.id = :id and q.userId = :userId")
    Optional<Questionnaire> findByIdAndUserIdWithFiles(@Param("id") Long id, @Param("userId") Long userId);
}
