package com.example.questionnaire_demo.service;

import com.example.questionnaire_demo.dto.*;
import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.predicate.dsl.PredicateFinalStep;
import org.hibernate.search.engine.search.predicate.dsl.SearchPredicateFactory;
import org.hibernate.search.mapper.orm.Search;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.example.questionnaire_demo.config.Constants;
import com.example.questionnaire_demo.mapper.EntityMapper;
import com.example.questionnaire_demo.model.Questionnaire;
import com.example.questionnaire_demo.model.UploadedFile;
import com.example.questionnaire_demo.repository.QuestionnaireRepository;
import com.example.questionnaire_demo.repository.UploadedFileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final UploadedFileRepository uploadedFileRepository;
    private final EntityMapper entityMapper;
    private final EntityManager entityManager;

    @Transactional
    public GetQuestionnaire saveQuestionnaire(Long userId, SaveQuestionnaire saveQuestionnaire){
        Questionnaire questionnaire = entityMapper.fromSavetoQuestionnaire(saveQuestionnaire);
        questionnaire.setUserId(userId);

        try {
            questionnaire = questionnaireRepository.save(questionnaire);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось сохранить анкету");
        }
        return entityMapper.fromQuestionnaireToGetQuestionnaire(questionnaire);
    }

    public GetQuestionnaire getQuestionnaireById(Long questionnaireId, Long userId){
        Questionnaire questionnaire = questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти анкету"));

        GetQuestionnaire getQuestionnaire = entityMapper.fromQuestionnaireToGetQuestionnaire(questionnaire);

        List<String> images = questionnaire.getUploadedFiles().stream()
                .map(UploadedFile::getStoredFileName)
                .toList();

        getQuestionnaire.setImages(images);

        return getQuestionnaire;
    }

    public Resource getImageByPath(String imagePath){
        checkIsPathTraversal(imagePath);

        Path filePath = Path.of(Constants.KEY_DEFAULT_IMAGES_PATH, imagePath);

        if (!Files.exists(filePath)) {
            return null;
        }
        return new FileSystemResource(filePath);
    }

    public void deleteImageByPath(String imagePath, Long userId) throws IOException {
        checkIsPathTraversal(imagePath);

        Path filePath = Path.of(Constants.KEY_DEFAULT_IMAGES_PATH, imagePath);

        UploadedFile uploadedFile = uploadedFileRepository.findByStoredFileNameAndUserId(imagePath, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти ваше изображение"));

        if (Files.deleteIfExists(filePath)) {
            try {
                uploadedFileRepository.delete(uploadedFile);
            } catch (Exception e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось удалить изображение");
            }
        }
        else {
            log.error(Constants.KEY_EXCEPTION_CANT_DELETE_IMAGE);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.KEY_EXCEPTION_CANT_DELETE_IMAGE);
        }
    }

    public void saveQuestionnaireImages(Long questionnaireId, QuestionnaireImages images, Long userId){
        MultipartFile[] announcementFiles = images.getImage();

        Questionnaire questionnaire = questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти вашу анкету"));

        List<UploadedFile> questionnaireImages = Arrays.stream(announcementFiles)
                .map(image -> {
                    try {
                        return saveUploadedFile(image, questionnaire);
                    } catch (IOException e) {
                        log.error(e.getMessage());
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось сохранить изображения");
                    }
                })
                .toList();
        questionnaire.getUploadedFiles().addAll(questionnaireImages);

        try {
            questionnaireRepository.save(questionnaire);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось сохранить изображения");
        }
    }

    public UserQuestionnairePreviews getAllQuestionnairesPreviews(Long userId){
        List<Questionnaire> previews = questionnaireRepository.findAllByUserId(userId);
        List<QuestionnairePreview> questionnairePreviews = previews.stream()
                .map(entityMapper::toQuestionnairePreview)
                .collect(Collectors.toList());
        return new UserQuestionnairePreviews(questionnairePreviews);
    }

    public QuestionnairePreview getPreviewByQuestionnaireId(Long questionnaireId){
        Questionnaire questionnaire = questionnaireRepository.findById(questionnaireId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ошибка при получении анкеты"));
        try {
            return entityMapper.toQuestionnairePreview(questionnaire);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Произошла ошибка со стороны сервера");
        }
    }

    public void deleteQuestionnaire(Long questionnaireId, Long userId){
        Questionnaire questionnaire = questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти вашу анкету"));

        List<String> localImages = questionnaire.getUploadedFiles().stream()
                .map(UploadedFile::getStoredFileName)
                .toList();

        localImages.forEach(file -> {
            try {
                Files.deleteIfExists(Path.of(file));
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось удалить изображение");
            }
        });
        try {
            questionnaireRepository.deleteById(questionnaireId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось удалить анкету");
        }
    }

    @Transactional
    public GetQuestionnaire updateQuestionnaire(Long questionnaireId, UpdateQuestionnaire updateQuestionnaire, Long userId){
        Questionnaire questionnaire = questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Не удалось найти вашу анкету"));

        Questionnaire updatedQuestionnaire = entityMapper.fromUpdateToQuestionnaire(updateQuestionnaire);
        updatedQuestionnaire.setId(questionnaireId);

        try {
            updatedQuestionnaire = questionnaireRepository.save(questionnaire);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Не удалось сохранить анкету");
        }
        return entityMapper.fromQuestionnaireToGetQuestionnaire(updatedQuestionnaire);
    }


    @Transactional(readOnly = true)
    public SearchPreviews filterQuestionnaires(Long userId, String text,
                                               Boolean hasEdu, Boolean hasTeam,
                                               Integer minWorkExp,
                                               @Min(0) Integer from,
                                               @Min(1) @Max(100) Integer size) {
        var searchQuery = Search.session(entityManager)
                .search(Questionnaire.class)
                .where(f -> buildSearchQuery(f, userId, text, hasEdu, hasTeam, minWorkExp))
                .sort(f -> f.score().desc())
                .fetch(from, size);

        var previews = searchQuery.hits().stream()
                .map(entityMapper::toQuestionnairePreview)
                .toList();

        return new SearchPreviews(
                previews,
                searchQuery.total().hitCount(),
                from,
                size
        );
    }

    private PredicateFinalStep buildSearchQuery(
            SearchPredicateFactory f,
            Long userId,
            String text,
            Boolean hasEdu,
            Boolean hasTeam,
            Integer minWorkExp) {

        var bool = f.bool();

        if (text != null && !text.isBlank()) {
            bool.must(f.match()
                            .field("workCategories").boost(2.0f)
                            .field("selfInfo").boost(1.5f)
                            .field("eduEst")
                            .field("team")
                    .matching(text));
        }

        //Пользователю не должны возвращаться его же анкеты
        bool.mustNot(f.match().field("userId").matching(userId));

        Optional.ofNullable(hasEdu).ifPresent(v ->
                bool.filter(f.match().field("hasEdu").matching(v)));

        Optional.ofNullable(hasTeam).ifPresent(v ->
                bool.filter(f.match().field("hasTeam").matching(v)));

        Optional.ofNullable(minWorkExp).ifPresent(exp ->
                bool.filter(f.range().field("workExp").atLeast(exp)));

        return bool;
    }

    private UploadedFile saveUploadedFile(MultipartFile anotherImage, Questionnaire questionnaire) throws IOException {
        String originalFileName = anotherImage.getOriginalFilename();
        String extension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID() + extension;
        String imagePath = Constants.KEY_DEFAULT_IMAGES_PATH;
        Files.createDirectories(Path.of(imagePath));
        Path finalImagePath = Path.of(imagePath, uniqueFileName);
        anotherImage.transferTo(finalImagePath);

        if (!Files.exists(finalImagePath)) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.KEY_EXCEPTION_CANT_SAVE_IMAGE);
        }

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setOriginalFileName(originalFileName);
        uploadedFile.setStoredFileName(uniqueFileName);
        uploadedFile.setUploadTimestamp(LocalDateTime.now());
        uploadedFile.setQuestionnaire(questionnaire);
        return uploadedFile;
    }

    private void checkIsPathTraversal(String imagePath){
        Path baseDir = Path.of(Constants.KEY_DEFAULT_IMAGES_PATH).normalize();

        Path filePath = baseDir.resolve(imagePath).normalize();

        if (!filePath.startsWith(baseDir)) {
            log.warn("Path traversal detected");
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Ошибка получения файла");
        }
    }
}
