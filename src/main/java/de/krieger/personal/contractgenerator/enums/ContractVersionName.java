package de.krieger.personal.contractgenerator.enums;

import lombok.Getter;

@Getter
public enum ContractVersionName {

/*    KASSE("Kasse", "01_22", "KA", new VersionTemplateName[]{VersionTemplateName.WERKSTUDENT, VersionTemplateName.DEVELOPER, VersionTemplateName.PRAKTIKANT, VersionTemplateName.KEIN_TEMPLATE}),
    LAGER("Lager", "01_22", "LA",new VersionTemplateName[]{VersionTemplateName.WERKSTUDENT, VersionTemplateName.DEVELOPER, VersionTemplateName.PRAKTIKANT, VersionTemplateName.KEIN_TEMPLATE}),*/
    VERWALTUNG("Verwaltung", "08_22", "VW",new VersionTemplateName[]{
        //    VersionTemplateName.WERKSTUDENT,
        /*VersionTemplateName.DEVELOPER,
        VersionTemplateName.PRAKTIKANT, */
        VersionTemplateName.KEIN_TEMPLATE,
        VersionTemplateName.MITARBEITER_DEUTSCHLAND,
        VersionTemplateName.MITARBEITER_INTERNATIONAL}),
    KASSE("Kasse", "08_22", "KS",new VersionTemplateName[]{
        //    VersionTemplateName.WERKSTUDENT,
        /*VersionTemplateName.DEVELOPER,
        VersionTemplateName.PRAKTIKANT, */
        VersionTemplateName.KEIN_TEMPLATE});

    private final String versionName;
    private final VersionTemplateName[] versionTemplateNames;
    private final String created;
    private final String abbreviation;

    ContractVersionName(String versionName, String created, String abbreviation, VersionTemplateName[] versionTemplateNames) {
        this.versionName = versionName;
        this.created = created;
        this.abbreviation = abbreviation;
        this.versionTemplateNames = versionTemplateNames;
    }

}


