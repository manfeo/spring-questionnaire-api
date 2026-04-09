package com.example.questionnaire_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Данные для сохранения новой анкеты")
public class SaveQuestionnaire {
    @Schema(description = "Категория работ", example = "Строительство домов")
    @NotBlank(message = "workCategories required")
    private String workCategories;

    @Schema(description = "Дата окончания обучения", example = "2025-12-31", type = "string", format = "date")
    private LocalDate eduDateEnd;

    @Schema(description = "Есть ли у вас команда?", example = "true")
    @NotNull(message = "hasTeam required")
    private Boolean hasTeam;

    @Schema(description = "Информация о команде (состав, роли, задачи)", example = "Своя бригада рабочих")
    private String team;

    @Schema(description = "Есть ли профильное образование?", example = "true")
    private Boolean hasEdu;

    @Schema(description = "Название учебного заведения", example = "МГТУ им. Н.Э. Баумана")
    private String eduEst;

    @Schema(description = "Дата начала обучения", example = "2020-09-01", type = "string", format = "date")
    private LocalDate eduDateStart;

    @Schema(description = "Опыт работы в годах", example = "3", minimum = "0")
    @NotNull(message = "workExp required")
    private Integer workExp;

    @Schema(description = "Информация о себе (био, навыки, о проекте)", example = "В строительстве уже более 10 лет. Делаю качественно, мой рейтинг это подтверждает")
    @NotBlank(message = "selfInfo required")
    private String selfInfo;

    @Schema(description = "Расценки вашей работы", example = "по договорённости")
    @NotBlank(message = "prices required")
    private String prices;
}
