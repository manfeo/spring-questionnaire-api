package com.example.questionnaire_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Краткая информация об анкете для отображения в списках и превью")
public class QuestionnairePreview {

    @Schema(description = "Уникальный идентификатор анкеты", example = "123")
    private Long id;

    @Schema(description = "Категория работ", example = "Строительство")
    private String workCategories;

    @Schema(description = "ID владельца анкеты", example = "456")
    private Long userId;

    @Schema(description = "Опыт работы в годах", example = "3", minimum = "0")
    private Integer workExp;

    @Schema(description = "Есть ли профильное образование?", example = "true")
    private Boolean hasEdu;

    @Schema(description = "Есть ли команда?", example = "false")
    private Boolean hasTeam;
}