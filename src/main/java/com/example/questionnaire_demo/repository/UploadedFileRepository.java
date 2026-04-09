package com.example.questionnaire_demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.example.questionnaire_demo.model.UploadedFile;

import java.util.Optional;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {
    @Query("select u from UploadedFile u where u.storedFileName = :fileName and u.questionnaire.userId = :userId")
    Optional<UploadedFile> findByStoredFileNameAndUserId(@Param("fileName") String fileName, @Param("userId") Long userId);
}
