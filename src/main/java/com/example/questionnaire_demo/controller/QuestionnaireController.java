package com.example.questionnaire_demo.controller;

import com.example.questionnaire_demo.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.example.questionnaire_demo.service.QuestionnaireService;

import java.io.IOException;

@RestController
@AllArgsConstructor
@RequestMapping("/questionnaires")
@Slf4j
@Tag(name = "Questionnaires", description = "Операции с анкетами пользователей")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    @PostMapping("")
    @Operation(summary = "Создать новую анкету", description = "Сохраняет анкету и возвращает её с присвоенным ID")
    @ApiResponse(responseCode = "200", description = "Анкета успешно создана",
            content = @Content(schema = @Schema(implementation = GetQuestionnaire.class)))
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
    public ResponseEntity<GetQuestionnaire> saveQuestionnaire(
            @Parameter(description = "ID пользователя", required = true, example = "12345")
            @RequestHeader Long userId,
            @Validated @RequestBody SaveQuestionnaire saveQuestionnaire){
        GetQuestionnaire response = questionnaireService.saveQuestionnaire(userId, saveQuestionnaire);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{questionnaireId}")
    @Operation(summary = "Получить анкету по ID", description = "Возвращает полную информацию об анкете")
    public ResponseEntity<GetQuestionnaire> getQuestionnaire(
            @Parameter(description = "ID анкеты", required = true, example = "67890")
            @PathVariable Long questionnaireId,
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId){
        GetQuestionnaire response = questionnaireService.getQuestionnaireById(questionnaireId, userId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{questionnaireId}")
    @Operation(summary = "Удалить анкету", description = "Удаляет анкету и все связанные файлы")
    @ApiResponse(responseCode = "204", description = "Анкета успешно удалена")
    @ApiResponse(responseCode = "404", description = "Анкета не найдена")
    public ResponseEntity<Void> deleteQuestionnaire(
            @Parameter(description = "ID анкеты", required = true)
            @PathVariable Long questionnaireId,
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId){
        questionnaireService.deleteQuestionnaire(questionnaireId, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{questionnaireId}")
    @Operation(summary = "Обновить анкету", description = "Полное обновление полей анкеты")
    public ResponseEntity<GetQuestionnaire> updateQuestionnaire(
            @Parameter(description = "ID анкеты", required = true)
            @PathVariable("questionnaireId") Long questionnaireId,
            @Validated @RequestBody UpdateQuestionnaire questionnaire,
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId){
        GetQuestionnaire response = questionnaireService.updateQuestionnaire(questionnaireId, questionnaire, userId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/images")
    @Operation(summary = "Загрузить изображения к анкете",
            description = "Принимает multipart/form-data с файлами")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Файлы загружены"),
            @ApiResponse(responseCode = "400", description = "Некорректный файл")
    })
    public ResponseEntity<Void> saveQuestionnaireImages(
            @Parameter(description = "ID анкеты", required = true)
            @PathVariable("id") Long questionnaireId,
            @Parameter(description = "Файлы изображений (multipart)")
            @ModelAttribute QuestionnaireImages image,
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId) {

        questionnaireService.saveQuestionnaireImages(questionnaireId, image, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/image")
    @Operation(summary = "Получить изображение по пути", description = "Возвращает файл изображения")
    public ResponseEntity<Resource> getImageByPath(
            @Parameter(description = "Путь к изображению", required = true, example = "abc123.jpg")
            @RequestParam String imagePath){
        Resource image = questionnaireService.getImageByPath(imagePath);
        return ResponseEntity.ok(image);
    }

    @DeleteMapping("/image")
    @Operation(summary = "Удалить изображение", description = "Удаляет файл по указанному пути")
    @ApiResponse(responseCode = "204", description = "Файл удалён")
    public ResponseEntity<Void> deleteImageByPath(
            @Parameter(description = "Путь к файлу", required = true)
            @RequestParam String filePath,
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId) throws IOException {
        questionnaireService.deleteImageByPath(filePath, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/previews")
    @Operation(summary = "Получить все превью анкет пользователя", description = "Список краткой информации по всем анкетам")
    public ResponseEntity<UserQuestionnairePreviews> getAllQuestionnairesPreviews(
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId){
        UserQuestionnairePreviews previews = questionnaireService.getAllQuestionnairesPreviews(userId);
        return ResponseEntity.ok(previews);
    }

    @GetMapping("/preview")
    @Operation(summary = "Получить превью по ID анкеты", description = "Краткая информация без полного контента")
    public ResponseEntity<QuestionnairePreview> getPreviewByQuestionnaireId(
            @Parameter(description = "ID анкеты", required = true)
            @RequestParam Long questionnaireId){
        QuestionnairePreview response = questionnaireService.getPreviewByQuestionnaireId(questionnaireId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    @Operation(summary = "Фильтрация анкет", description = "Поиск и фильтрация анкет по различным критериям")
    public ResponseEntity<SearchPreviews> filterQuestionnaires(
            @Parameter(description = "ID пользователя", required = true)
            @RequestHeader Long userId,
            @Parameter(description = "Текстовый поиск", example = "Java разработчик")
            @RequestParam(required = false) String text,
            @Parameter(description = "Есть ли образование", example = "true")
            @RequestParam(required = false) Boolean hasEdu,
            @Parameter(description = "Есть ли команда", example = "false")
            @RequestParam(required = false) Boolean hasTeam,
            @Parameter(description = "Минимальный опыт работы (лет)", example = "2")
            @RequestParam(required = false) Integer minWorkExp,
            @Parameter(description = "Номер страницы (пагинация)", example = "0")
            @RequestParam(required = false) Integer from,
            @Parameter(description = "Размер страницы", example = "10")
            @RequestParam(required = false) Integer size){
        SearchPreviews previews = questionnaireService.filterQuestionnaires(userId, text, hasEdu, hasTeam, minWorkExp, from, size);
        return ResponseEntity.ok(previews);
    }
}
