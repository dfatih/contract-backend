package de.krieger.personal.contractgenerator.enums;

import lombok.Getter;

@Getter
public enum FieldType {
    DATUM("Datum"),
    AUSWAHL("Auswahl"),
    TEXT("Text");

    private final String typeName;
    FieldType(String typeName) {
        this.typeName = typeName;
    }
}
