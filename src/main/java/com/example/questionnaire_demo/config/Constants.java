package com.example.questionnaire_demo.config;

public class Constants {
    public static final String KEY_DEFAULT_IMAGES_PATH = System.getenv().getOrDefault("IMAGES_PATH","src/main/resources/images/");
    public static final String KEY_DEFAULT_FILES_PATH = System.getenv().getOrDefault("FILES_PATH", "src/main/resources/files/");
    public static final String KEY_EXCEPTION_CANT_SAVE_IMAGE = "Не получилось сохранить изображение";
    public static final String KEY_EXCEPTION_CANT_DELETE_IMAGE = "не получилось удалить изображение";
}
