package com.example.questionnaire_demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "DTO для загрузки изображений в анкету (Multipart Form Data)")
public class QuestionnaireImages {

    @Schema(description = "Массив файлов изображений", type = "string", format = "binary")
    private MultipartFile[] image;
}
