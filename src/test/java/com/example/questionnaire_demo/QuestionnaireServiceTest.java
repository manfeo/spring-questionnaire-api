package com.example.questionnaire_demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import com.example.questionnaire_demo.dto.GetQuestionnaire;
import com.example.questionnaire_demo.dto.QuestionnaireImages;
import com.example.questionnaire_demo.mapper.EntityMapper;
import com.example.questionnaire_demo.model.Questionnaire;
import com.example.questionnaire_demo.repository.QuestionnaireRepository;
import com.example.questionnaire_demo.service.QuestionnaireService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionnaireServiceTest {
    @Mock
    private QuestionnaireRepository questionnaireRepository;

    @Mock
    private EntityMapper entityMapper;

    @InjectMocks
    private QuestionnaireService questionnaireService;

    @Test
    void getQuestionnaireById_happyPath_returnsDto() {
        Long questionnaireId = 1L;
        Long userId = 42L;

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setId(questionnaireId);
        questionnaire.setUserId(userId);
        questionnaire.setWorkCategories("Java");
        questionnaire.setUploadedFiles(List.of());

        GetQuestionnaire expectedDto = new GetQuestionnaire();
        expectedDto.setId(questionnaireId);
        expectedDto.setUserId(userId);
        expectedDto.setWorkCategories("Java");
        expectedDto.setImages(List.of());

        when(questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId))
                .thenReturn(Optional.of(questionnaire));

        when(entityMapper.fromQuestionnaireToGetQuestionnaire(questionnaire))
                .thenReturn(expectedDto);

        GetQuestionnaire actualResult = questionnaireService.getQuestionnaireById(questionnaireId, userId);

        assertThat(actualResult).isNotNull();
        assertThat(actualResult.getId()).isEqualTo(questionnaireId);
        assertThat(actualResult.getUserId()).isEqualTo(userId);
        assertThat(actualResult.getWorkCategories()).isEqualTo("Java");
    }

    @Test
    void getQuestionnaireById_notFound_throwsException() {
        Long questionnaireId = 999L;
        Long userId = 42L;

        when(questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId))
                .thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () ->
                questionnaireService.getQuestionnaireById(questionnaireId, userId));

        verify(questionnaireRepository).findByIdAndUserIdWithFiles(questionnaireId, userId);

        verify(entityMapper, never()).fromQuestionnaireToGetQuestionnaire(any());
    }

    @Test
    void saveQuestionnaireImages_happyPath_savesFilesAndUpdatesEntity() throws IOException {
        Long questionnaireId = 100L;
        Long userId = 42L;

        Questionnaire existingQuestionnaire = new Questionnaire();
        existingQuestionnaire.setId(questionnaireId);
        existingQuestionnaire.setUserId(userId);
        existingQuestionnaire.setUploadedFiles(new ArrayList<>());

        when(questionnaireRepository.findByIdAndUserIdWithFiles(questionnaireId, userId))
                .thenReturn(Optional.of(existingQuestionnaire));

        MultipartFile file1 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("cat.jpg");
        doNothing().when(file1).transferTo(any(Path.class));

        MultipartFile file2 = mock(MultipartFile.class);
        when(file2.getOriginalFilename()).thenReturn("dog.png");
        doNothing().when(file2).transferTo(any(Path.class));

        QuestionnaireImages inputDto = new QuestionnaireImages();
        inputDto.setImage(new MultipartFile[]{file1, file2});

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)){
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenReturn(null);
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            questionnaireService.saveQuestionnaireImages(questionnaireId, inputDto, userId);

            verify(file1, times(1)).transferTo(any(Path.class));
            verify(file2, times(1)).transferTo(any(Path.class));

            verify(questionnaireRepository).save(any(Questionnaire.class));

            ArgumentCaptor<Questionnaire> captor = ArgumentCaptor.forClass(Questionnaire.class);
            verify(questionnaireRepository).save(captor.capture());
            assertThat(captor.getValue().getUploadedFiles()).hasSize(2);
        }
    }

    @Test
    void getImageByPath_maliciousPath_throwsForbidden(){
        String maliciousPath = "../../../etc/passwd";

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                questionnaireService.getImageByPath(maliciousPath));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    }

    @Test
    void getImageByPath_validPath_returnsResource() {
        String validPath = "uploads/photo.jpg";

        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.exists(any(Path.class))).thenReturn(true);

            Resource result = questionnaireService.getImageByPath(validPath);

            assertThat(result).isNotNull();
            assertThat(result).isInstanceOf(FileSystemResource.class);

            mockedFiles.verify(() -> Files.exists(any(Path.class)), times(1));
        }
    }
}
