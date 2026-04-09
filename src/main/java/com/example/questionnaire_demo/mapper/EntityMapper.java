package com.example.questionnaire_demo.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import com.example.questionnaire_demo.dto.GetQuestionnaire;
import com.example.questionnaire_demo.dto.UpdateQuestionnaire;
import com.example.questionnaire_demo.dto.QuestionnairePreview;
import com.example.questionnaire_demo.dto.SaveQuestionnaire;
import com.example.questionnaire_demo.model.Questionnaire;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    Questionnaire fromSavetoQuestionnaire(SaveQuestionnaire saveQuestionnaire);
    @Mapping(target = "images", ignore = true)
    GetQuestionnaire fromQuestionnaireToGetQuestionnaire(Questionnaire questionnaire);
    @Mapping(target = "uploadedFiles", ignore = true)
    Questionnaire fromUpdateToQuestionnaire(UpdateQuestionnaire updateQuestionnaire);
    QuestionnairePreview toQuestionnairePreview(Questionnaire questionnaire);
}
