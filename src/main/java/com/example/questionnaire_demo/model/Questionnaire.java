package com.example.questionnaire_demo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.FullTextField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.GenericField;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.Indexed;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questionnaires")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Indexed
public class Questionnaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @FullTextField(analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String workCategories;

    @GenericField
    @Column(nullable = false)
    private Boolean hasTeam;

    @FullTextField(analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    @Column(columnDefinition = "TEXT")
    private String team;

    @FullTextField(analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String selfInfo;

    @GenericField
    @Column(columnDefinition = "TEXT")
    private String prices;

    @GenericField
    @Column(nullable = false)
    private Boolean hasEdu;

    @GenericField
    @Column(nullable = false, columnDefinition = "TEXT")
    private Integer workExp;

    @FullTextField(analyzer = "autocomplete_indexing", searchAnalyzer = "autocomplete_search")
    private String eduEst;

    private LocalDate eduDateStart;
    private LocalDate eduDateEnd;

    @OneToMany(mappedBy = "questionnaire", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<UploadedFile> uploadedFiles = new ArrayList<>();
}
