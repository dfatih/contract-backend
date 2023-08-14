package de.krieger.personal.contractgenerator.enums;

public enum Language {
    GERMAN("Deutsch"),
    ENGLISH("Englisch");

    private final String languageName;

    Language(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageName() {
        return languageName;
    }
}
