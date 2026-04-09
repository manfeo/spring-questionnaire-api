package questionnaireDemo;

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

import java.util.List;

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
        // === ARRANGE ===
        Long questionnaireId = 1L;
        Long userId = 42L;

        // Создаём тестовый DTO, который вернёт замоканный сервис
        GetQuestionnaire mockDto = new GetQuestionnaire(
                questionnaireId, "Java Developer", userId, null,
                null, false, null, true, "High", null, 3, "About me", "100$", List.of("img1.jpg")
        );

        // Программируем сервис: "при таком вызове верни этот DTO"
        when(questionnaireService.getQuestionnaireById(questionnaireId, userId))
                .thenReturn(mockDto);

        // === ACT & ASSERT ===
        mockMvc.perform(get("/questionnaires/{questionnaireId}", questionnaireId)
                        .header("userId", userId) // Добавляем хедер
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Проверяем статус 200
                .andExpect(jsonPath("$.id").value(questionnaireId)) // Проверяем поле id в JSON
                .andExpect(jsonPath("$.userId").value(userId)) // Проверяем поле userId
                .andExpect(jsonPath("$.workCategories").value("Java Developer")); // Проверяем работу
    }
}
