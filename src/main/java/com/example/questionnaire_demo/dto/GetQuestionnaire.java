package com.example.questionnaire_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Полная информация об анкете пользователя")
public class GetQuestionnaire {

    @Schema(description = "Уникальный идентификатор анкеты", example = "123")
    private Long id;

    @Schema(description = "ID владельца анкеты (пользователя)", example = "456")
    private Long userId;

    @Schema(description = "Категория работ", example = "Строительство домов")
    private String workCategories;

    @Schema(description = "Дата окончания обучения", example = "2025-12-31", type = "string", format = "date")
    private LocalDate eduDateEnd;

    @Schema(description = "Есть ли у вас команда?", example = "true")
    private Boolean hasTeam;

    @Schema(description = "Информация о команде (состав, роли, задачи)", example = "Своя бригада из 5 человек: 2 мастера, 2 подсобника, 1 прораб")
    private String team;

    @Schema(description = "Есть ли профильное образование?", example = "true")
    private Boolean hasEdu;

    @Schema(description = "Название учебного заведения", example = "МГТУ им. Н.Э. Баумана")
    private String eduEst;

    @Schema(description = "Дата начала обучения", example = "2020-09-01", type = "string", format = "date")
    private LocalDate eduDateStart;

    @Schema(description = "Опыт работы в годах", example = "3", minimum = "0")
    private Integer workExp;

    @Schema(description = "Информация о себе (био, навыки, о проекте)", example = "В строительстве уже более 10 лет. Специализируюсь на возведении частных домов под ключ. Работаю качественно и в срок.")
    private String selfInfo;

    @Schema(description = "Расценки вашей работы", example = "от 5000 руб./м2 или по договорённости")
    private String prices;

    @Schema(description = "Список URL загруженных изображений", example = "4caf7819-2a8f-4306-87ee-36fd2dc930e2.jpg")
    private List<String> images;
}
