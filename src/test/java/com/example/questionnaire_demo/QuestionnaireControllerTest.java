package com.example.questionnaire_demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.questionnaire_demo.controller.QuestionnaireController;
import com.example.questionnaire_demo.dto.GetQuestionnaire;
import com.example.questionnaire_demo.service.QuestionnaireService;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionnaireController.class)
public class QuestionnaireControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionnaireService questionnaireService;

    @Test
    void getQuestionnaire_found_returnsOkWithJson() throws Exception {
        Long questionnaireId = 1L;
        Long userId = 42L;

        GetQuestionnaire mockDto = new GetQuestionnaire(
                questionnaireId, userId, "Строительство домов", LocalDate.of(2025, Month.JANUARY, 31),
                true, "Бригада рабочих", false, null, null, 3, "About me", "100$", Collections.emptyList()
        );

        when(questionnaireService.getQuestionnaireById(questionnaireId, userId))
                .thenReturn(mockDto);

        mockMvc.perform(get("/questionnaires/{questionnaireId}", questionnaireId)
                        .header("userId", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(questionnaireId))
                .andExpect(jsonPath("$.userId").value(userId))
                .andExpect(jsonPath("$.workCategories").value("Строительство домов"));
    }
}
