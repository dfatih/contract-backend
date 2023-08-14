package de.krieger.personal.contractgenerator.enums;

import lombok.Getter;

@Getter
public enum VersionTemplateName {
    WERKSTUDENT("Werkstudent", "WS"),
    /*DEVELOPER("Developer", "DE"),
    PRAKTIKANT("Praktikant", "PK"),*/
    KEIN_TEMPLATE("Kein Template", "KT"),
    MITARBEITER_DEUTSCHLAND("Mitarbeiter Deutschland", "MD"),
    MITARBEITER_INTERNATIONAL("Mitarbeiter International", "MI");

    private final String templateName;
    private final String abbreviation;

    VersionTemplateName(String templateName, String abbreviation) {
        this.templateName = templateName;
        this.abbreviation = abbreviation;
    }
}
