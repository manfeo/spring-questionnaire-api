package com.example.questionnaire_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Список превью анкет пользователя")
public class UserQuestionnairePreviews {
    @Schema(description = "Список превью анкет пользователей")
    private List<QuestionnairePreview> questionnairePreviews;
}
