package com.example.questionnaire_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Результат поиска анкет с информацией о пагинации")
public class SearchPreviews {

    @Schema(description = "Список превью анкет, соответствующих условиям поиска")
    private List<QuestionnairePreview> previews;

    @Schema(description = "Общее количество найденных анкет (для расчёта страниц)", example = "124")
    private Long total;

    @Schema(description = "Смещение (offset) или номер страницы в запросе", example = "0", minimum = "0")
    private Integer from;

    @Schema(description = "Количество элементов на одной странице", example = "20", minimum = "1")
    private Integer size;
}
