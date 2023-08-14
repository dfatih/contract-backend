package de.krieger.personal.contractgenerator.service.impl;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.FieldType;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.*;
import de.krieger.personal.contractgenerator.repository.CompanyRepository;
import de.krieger.personal.contractgenerator.repository.ContractVersionRepository;
import de.krieger.personal.contractgenerator.repository.ParagraphRepository;
import de.krieger.personal.contractgenerator.service.DatabasePopulationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class DatabasePopulationServiceImpl implements DatabasePopulationService {

    @Autowired
    private ParagraphRepository paragraphRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private ContractVersionRepository contractVersionRepository;

    @Override
    public List<ContractParagraph> populateDatabase() {
        if (paragraphRepository.findAllByTemplate(true).isEmpty() || paragraphRepository.findAllByTemplate(true).size() == 0) {
            Company company1 = new Company();
            company1.setCompanyName("KIS GmbH & Co. KG");
            company1.setShortName("KIS");
            CompanyLocation companyLocation1 = new CompanyLocation();
            companyLocation1.setCompany(company1);
            companyLocation1.setAddress("Am Rondell 1, 12529 Schönefeld");
            companyLocation1.setName("Schönefeld");
            List<CompanyLocation> companyLocationList1 = new ArrayList<>();
            companyLocationList1.add(companyLocation1);
            company1.setCompanyLocations(companyLocationList1);
            companyRepository.save(company1);
            Company company2 = new Company();
            company2.setCompanyName("KOS GmbH & Co. KG");
            company2.setShortName("KOS");
            CompanyLocation companyLocation2 = new CompanyLocation();
            companyLocation2.setCompany(company2);
            companyLocation2.setAddress("Am Rondell 1, 12529 Schönefeld");
            companyLocation2.setName("Schönefeld");
            List<CompanyLocation> companyLocationList2 = new ArrayList<>();
            companyLocationList2.add(companyLocation2);
            company2.setCompanyLocations(companyLocationList2);
            companyRepository.save(company2);
            ContractVersion contractVersion1 = new ContractVersion();
            contractVersion1.setName("Verwaltung");
            contractVersion1.setAbbreviation("VW");
            contractVersion1.setCreated("01_22");
            List<VersionTemplate> versionTemplates = new ArrayList<>();
            VersionTemplate versionTemplate1 = new VersionTemplate();
            versionTemplate1.setContractVersion(contractVersion1);
            versionTemplate1.setTemplateName("Werkstudent");
            versionTemplate1.setAbbreviation("WS");
            versionTemplates.add(versionTemplate1);
            VersionTemplate versionTemplate2 = new VersionTemplate();
            versionTemplate2.setContractVersion(contractVersion1);
            versionTemplate2.setTemplateName("Kein Template");
            versionTemplate2.setAbbreviation("KT");
            versionTemplates.add(versionTemplate2);
            contractVersion1.setVersionTemplates(versionTemplates);
            contractVersionRepository.save(contractVersion1);
            List<ContractParagraph> templates = setRelations(createVerwaltungTemplates());
            templates.addAll(setRelations(createKasseTemplates()));
            return templates;
        }
        return new ArrayList<>();
    }

    @Override
    public List<ContractParagraph> updateTemplates() {
        if (!paragraphRepository.findAllByTemplate(true).isEmpty() && paragraphRepository.findAllByTemplate(true).size() != 0) {
            List<ContractParagraph> templates = createVerwaltungTemplates();
            templates.addAll(createKasseTemplates());
            List<ContractParagraph> templatesFromDb = paragraphRepository.findAllByTemplate(true);
            Collections.sort(templatesFromDb);
            List<ContractParagraph> templatesInDb = new ArrayList<>();
            List<ContractParagraph> paragraphsToUpdate = new ArrayList<>();
            for (ContractParagraph paragraph : templatesFromDb) {
                ContractParagraph contractParagraph = new ContractParagraph();
                contractParagraph.setParagraphNumber(paragraph.getParagraphNumber());
                contractParagraph.setParagraphTitle(paragraph.getParagraphTitle());
                contractParagraph.setParagraphContent(paragraph.getParagraphContent());
                contractParagraph.setSelectionGroups(paragraph.isSelectionGroups());
                List<ContentField> contentFields = new ArrayList<>();
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    for (ContentField contentField : paragraph.getContentFields()) {
                        ContentField newContentField = new ContentField();
                        newContentField.setFieldName(contentField.getFieldName());
                        newContentField.setFieldDescription(contentField.getFieldDescription());
                        newContentField.setFieldValue(contentField.getFieldValue());
                        newContentField.setFieldType(contentField.getFieldType());
                        contentFields.add(newContentField);
                    }
                    contractParagraph.setContentFields(contentFields);
                }
                List<OptionalContent> optionalContents = new ArrayList<>();
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        OptionalContent newOptionalContent = new OptionalContent();
                        newOptionalContent.setTitle(optionalContent.getTitle());
                        newOptionalContent.setShortName(optionalContent.getShortName());
                        newOptionalContent.setContent(optionalContent.getContent());
                        newOptionalContent.setModified(optionalContent.isModified());
                        newOptionalContent.setSelected(optionalContent.isSelected());
                        newOptionalContent.setContractVersionName(optionalContent.getContractVersionName());
                        newOptionalContent.setVersionTemplateNames(optionalContent.getVersionTemplateNames());
                        newOptionalContent.setSelectionGroup(optionalContent.getSelectionGroup());
                        List<OptionalContentField> optionalContentFields = new ArrayList<>();
                        if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                            for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                                OptionalContentField newOptionalContentField = new OptionalContentField();
                                newOptionalContentField.setFieldName(optionalContentField.getFieldName());
                                newOptionalContentField.setFieldDescription(optionalContentField.getFieldDescription());
                                optionalContentFields.add(newOptionalContentField);
                            }
                            newOptionalContent.setOptionalContentFields(optionalContentFields);
                        }
                        optionalContents.add(newOptionalContent);
                    }
                    contractParagraph.setOptionalContents(optionalContents);
                }
                contractParagraph.setTemplate(paragraph.isTemplate());
                contractParagraph.setClean(paragraph.isClean());
                contractParagraph.setLanguage(paragraph.getLanguage());
                contractParagraph.setContractVersionName(paragraph.getContractVersionName());
                contractParagraph.setVersionTemplateNames(paragraph.getVersionTemplateNames());
                templatesInDb.add(contractParagraph);
            }
            for (ContractParagraph template : templates) {
                for (ContractParagraph dbTemplate : templatesInDb) {
                    if (!template.equals(dbTemplate) && template.getParagraphNumber().equals(dbTemplate.getParagraphNumber()) && template.getContractVersionName().equals(dbTemplate.getContractVersionName())) {
                        paragraphRepository.delete(templatesFromDb.stream().filter(contractParagraph -> contractParagraph.getParagraphNumber().equals(dbTemplate.getParagraphNumber())).findAny().get());
                        paragraphsToUpdate.add(template);
                    }
                }
                if (templatesInDb.stream().noneMatch(contractParagraph -> contractParagraph.getParagraphNumber().equals(template.getParagraphNumber()) && contractParagraph.getContractVersionName().equals(template.getContractVersionName()))) {
                    paragraphsToUpdate.add(template);
                }
            }
            paragraphsToUpdate = setRelations(paragraphsToUpdate);
            return paragraphsToUpdate;
        }
        return new ArrayList<>();
    }

    private List<ContractParagraph> setRelations(List<ContractParagraph> contractParagraphList) {
        if (contractParagraphList.size() != 0) {
            for (ContractParagraph paragraph : contractParagraphList) {
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    for (ContentField contentField : paragraph.getContentFields()) {
                        contentField.setContractParagraph(paragraph);
                    }
                }
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        optionalContent.setContractParagraph(paragraph);
                        if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                            for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                                optionalContentField.setOptionalContent(optionalContent);
                            }
                        }
                    }
                }
            }
        }
        return contractParagraphList;
    }

    private List<ContractParagraph> createVerwaltungTemplates() {
        List<ContractParagraph> verwaltungParagraphList = new ArrayList<>();
        ContractParagraph verwaltungParagraph1 = new ContractParagraph();
        verwaltungParagraph1.setParagraphTitle("Tätigkeit, Befristung, Probezeit, Bedingungen");
        verwaltungParagraph1.setParagraphNumber("1");
        verwaltungParagraph1.setClean(true);
        verwaltungParagraph1.setTemplate(true);
        verwaltungParagraph1.setParagraphContent("(1) Die Einstellung als<b> [Tätigkeitsbezeichnung] </b>erfolgt ab<b> [Eintrittsdatum][OPT-1-0]</b>" +
                "[OPT-1-1]" +
                "[OPT-1-2]" +
                "[OPT-1-3]" +
                "[OPT-1-6].<br/>" +
                "|(2) Die ersten [Probezeit] Monate gelten als Probezeit. Während dieser Probezeit kann das Arbeitsverhältnis beidseitig mit der besonderen Frist des § 9 Abs. 1 gekündigt werden.<br/>" +
                "[OPT-1-4]" +
                "[OPT-1-5]");
        verwaltungParagraph1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1.setSelectionGroups(true);
        List<ContentField> verwaltungParagraph1ContentFields = new ArrayList<>();
        ContentField verwaltungParagraph1ContentField1 = new ContentField();
        verwaltungParagraph1ContentField1.setFieldName("Tätigkeitsbezeichnung");
        verwaltungParagraph1ContentField1.setFieldDescription("Bezeichnung der Tätigkeit");
        verwaltungParagraph1ContentField1.setFieldType(FieldType.TEXT);
        verwaltungParagraph1ContentFields.add(verwaltungParagraph1ContentField1);
        ContentField verwaltungParagraph1ContentField2 = new ContentField();
        verwaltungParagraph1ContentField2.setFieldName("Eintrittsdatum");
        verwaltungParagraph1ContentField2.setFieldDescription("Datum des Eitritts");
        verwaltungParagraph1ContentField2.setFieldType(FieldType.DATUM);
        verwaltungParagraph1ContentFields.add(verwaltungParagraph1ContentField2);
        ContentField verwaltungParagraph1ContentField3 = new ContentField();
        verwaltungParagraph1ContentField3.setFieldName("Probezeit");
        verwaltungParagraph1ContentField3.setFieldDescription("in Monaten, maximal 6");
        verwaltungParagraph1ContentField3.setFieldType(FieldType.TEXT);
        verwaltungParagraph1ContentFields.add(verwaltungParagraph1ContentField3);
        verwaltungParagraph1.setContentFields(verwaltungParagraph1ContentFields);
        List<OptionalContent> verwaltungParagraph1OptionalContents = new ArrayList<>();
        OptionalContent verwaltungParagraph1OptionalContent0 = new OptionalContent();
        verwaltungParagraph1OptionalContent0.setTitle("früheres Eintrittsdatum möglich");
        verwaltungParagraph1OptionalContent0.setShortName("OPT-1-0");
        verwaltungParagraph1OptionalContent0.setContent(" oder früher");
        verwaltungParagraph1OptionalContent0.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent0.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1OptionalContent0.setSelectionGroup(0);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent0);
        OptionalContent verwaltungParagraph1OptionalContent1 = new OptionalContent();
        verwaltungParagraph1OptionalContent1.setTitle("Aufschiebende Bedingung Betriebsratszustimmung");
        verwaltungParagraph1OptionalContent1.setShortName("OPT-1-1");
        verwaltungParagraph1OptionalContent1.setContent(" unter der aufschiebenden Bedingung, dass der für den Betrieb zuständige Betriebsrat der Einstellung " +
                "des Arbeitnehmers zustimmt; der Arbeitnehmer wurde insoweit darauf hingewiesen, dass der " +
                "Betriebsrat diese Zustimmung noch nicht erteilt hat. Der Arbeitgeber ist berechtigt, aber nicht " +
                "verpflichtet, gegen eine etwaige Zustimmungsverweigerung des Betriebsrats gerichtliche Schritte " +
                "einzuleiten. Der Arbeitgeber ist verpflichtet, dem Arbeitnehmer unverzüglich mitzuteilen, dass ein " +
                "Arbeitsverhältnis nicht zustande kommt, wenn der Betriebsrat die Zustimmung verweigert und das " +
                "Arbeitsgericht die Zustimmung nicht ersetzt oder der Arbeitgeber die Ersetzung nicht beantragt");
        verwaltungParagraph1OptionalContent1.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1OptionalContent1.setSelectionGroup(1);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent1);
        OptionalContent verwaltungParagraph1OptionalContent2 = new OptionalContent();
        verwaltungParagraph1OptionalContent2.setTitle("Aufschiebende Bedingung Ausländer");
        verwaltungParagraph1OptionalContent2.setShortName("OPT-1-2");
        verwaltungParagraph1OptionalContent2.setContent(" unter der aufschiebenden Bedingung, dass der Arbeitnehmer vor diesem Tag einen gültigen " +
                "Aufenthaltstitel, der ihn zur Erwerbstätigkeit berechtigt, vorlegt. Legt der Arbeitnehmer einen solchen " +
                "Aufenthaltstitel erst am oder nach diesem Tag, aber spätestens innerhalb von 3 Monaten vor, beginnt " +
                "das Arbeitsverhältnis an dem Tag nach der Vorlage des Aufenthaltstitels; ist dies ein Sonntag, dann " +
                "am folgenden Montag. Legt der Arbeitnehmer den Aufenthaltstitel erst nach mehr als 3 Monaten vor, " +
                "kommt kein Arbeitsverhältnis zustande");
        verwaltungParagraph1OptionalContent2.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1OptionalContent2.setSelectionGroup(1);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent2);
        OptionalContent verwaltungParagraph1OptionalContent3 = new OptionalContent();
        verwaltungParagraph1OptionalContent3.setTitle("Befristung");
        verwaltungParagraph1OptionalContent3.setShortName("OPT-1-3");
        verwaltungParagraph1OptionalContent3.setContent(" befristet bis zum<b> [Befristungsdatum]</b>. Mit Fristende<b> endet das Arbeitsverhältnis, ohne dass es " +
                "einer Kündigung bedarf</b>. Der Arbeitnehmer ist verpflichtet, sich spätestens drei Monate vor " +
                "Beendigung des Arbeitsvertrages bei der zuständigen Agentur für Arbeit arbeitsuchend zu melden. " +
                "Eine verspätete Meldung kann zu einer Reduzierung des Arbeitslosengeldanspruches führen. Auf " +
                "Grund verspäteter Meldungen können keine Schadensersatzforderungen gegen den Arbeitgeber " +
                "geltend gemacht werden");
        verwaltungParagraph1OptionalContent3.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent3.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1OptionalContent3.setSelectionGroup(1);
        List<OptionalContentField> verwaltungParagraph1OptionalContent3Fields = new ArrayList<>();
        OptionalContentField verwaltungParagraph1OptionalContent3Field1 = new OptionalContentField();
        verwaltungParagraph1OptionalContent3Field1.setFieldName("Befristungsdatum");
        verwaltungParagraph1OptionalContent3Field1.setFieldDescription("Enddatum des befristeten Vertrags");
        verwaltungParagraph1OptionalContent3Fields.add(verwaltungParagraph1OptionalContent3Field1);
        verwaltungParagraph1OptionalContent3.setOptionalContentFields(verwaltungParagraph1OptionalContent3Fields);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent3);
        OptionalContent verwaltungParagraph1OptionalContent6 = new OptionalContent();
        verwaltungParagraph1OptionalContent6.setTitle("Aufschiebende Bedingungen Krieg, Katastrophen");
        verwaltungParagraph1OptionalContent6.setShortName("OPT-1-6");
        verwaltungParagraph1OptionalContent6.setContent(" unter den<b> aufschiebenden Bedingungen</b>, <br/><br/>" +
                "<table><tr><td style=\"width:5%\">a)</td> <td style=\"text-align:justify;\">dass der Arbeitnehmer vor diesem Tag einen gültigen Aufenthaltstitel, der ihn zur Erwerbstätigkeit berechtigt, vorlegt. Legt der Arbeitnehmer einen solchen Aufenthaltstitel erst am oder nach diesem Tag, aber spätestens innerhalb von 3 Monaten vor, beginnt das Arbeitsverhältnis an dem Tag nach der Vorlage des Aufenthaltstitels; ist dies ein Sonntag, dann am folgenden Montag.</td></tr></table><br/>" +
                "und <br/><br/>" +
                "<table><tr><td style=\"width:5%\">b)</td> <td style=\"text-align:justify;\">dass der Arbeitnehmer seine Arbeitsleistung mit Vorlage des Aufenthaltstitels, spätestens aber innerhalb eines Monats nach diesem Tag vor Ort in der Betriebsstätte (§ 2 Abs. 1) tatsächlich anbietet. Eine Arbeitsunfähigkeit insbesondere aufgrund einer Erkrankung steht dem Angebot der Arbeitsleistung gleich, nicht jedoch eine verspätete Einreise nach Deutschland, gleich ob diese vom Arbeitnehmer verschuldet ist oder nicht. Dies gilt auch bei Reiseerschwerungen infolge von Krisen, beispielsweise aufgrund von Krieg oder Naturkatastrophen. </td></tr></table><br/>" +
                "Bietet der Arbeitnehmer seine Arbeitsleistung nicht oder nicht innerhalb eines Monats in der Betriebsstätte an oder legt er den Aufenthaltstitel nicht oder erst nach mehr als 3 Monaten vor, kommt kein Arbeitsverhältnis zustande");
        verwaltungParagraph1OptionalContent6.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent6.setVersionTemplateNames(VersionTemplateName.MITARBEITER_INTERNATIONAL.name());
        verwaltungParagraph1OptionalContent6.setSelectionGroup(1);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent6);
        OptionalContent verwaltungParagraph1OptionalContent4 = new OptionalContent();
        verwaltungParagraph1OptionalContent4.setTitle("Auflösende Bedingung Ausländer");
        verwaltungParagraph1OptionalContent4.setShortName("OPT-1-4");
        verwaltungParagraph1OptionalContent4.setContent("|(3) Das Arbeitsverhältnis steht<b> unter der auflösenden Bedingung</b>, dass der Arbeitnehmer sich in " +
                "Deutschland nicht nur aufhalten, sondern auch einer Erwerbstätigkeit nachgehen darf, mit der Folge, " +
                "dass das<b> Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf</b>, wenn der zur " +
                "Erwerbstätigkeit berechtigende Titel aufgehoben wird oder die darin genannte Frist abläuft, ohne dass " +
                "eine Verlängerung erteilt und diese der Arbeitgeberin spätestens am Fristablauftag nachgewiesen wird.<br/>");
        verwaltungParagraph1OptionalContent4.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent4.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1OptionalContent4.setSelectionGroup(2);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent4);
        OptionalContent verwaltungParagraph1OptionalContent5 = new OptionalContent();
        verwaltungParagraph1OptionalContent5.setTitle("Auflösende Bedingung Werkstudent");
        verwaltungParagraph1OptionalContent5.setShortName("OPT-1-5");
        verwaltungParagraph1OptionalContent5.setContent("|(3) Das Arbeitsverhältnis steht<b> unter der auflösenden Bedingung</b>, dass der Arbeitnehmer als " +
                "Vollzeitstudent immatrikuliert ist und seine letzte Prüfungsleistung noch nicht erbracht hat, mit der Folge, " +
                "dass das<b> Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf</b>, wenn das " +
                "Vollzeitstudium beendet ist. Der Arbeitnehmer verpflichtet sich, den Arbeitgeber sofort zu informieren, " +
                "wenn sich sein beruflicher Status ändert.<br/>");
        verwaltungParagraph1OptionalContent5.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph1OptionalContent5.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph1OptionalContent5.setSelectionGroup(2);
        verwaltungParagraph1OptionalContents.add(verwaltungParagraph1OptionalContent5);
        verwaltungParagraph1.setOptionalContents(verwaltungParagraph1OptionalContents);
        verwaltungParagraphList.add(verwaltungParagraph1);


        ContractParagraph verwaltungParagraph2 = new ContractParagraph();
        verwaltungParagraph2.setParagraphNumber("2");
        verwaltungParagraph2.setParagraphTitle("Arbeitsort, Aufgaben");
        verwaltungParagraph2.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph2.setSelectionGroups(true);
        verwaltungParagraph2.setClean(true);
        verwaltungParagraph2.setTemplate(true);
        verwaltungParagraph2.setParagraphContent("[OPT-2-1]" +
                "[OPT-2-2]<br/>" +
                "|(2) Der Arbeitgeber ist berechtigt, die vertraglich geschuldete Tätigkeit durch eine Stellenbeschreibung zu konkretisieren.<br/>" +
                "|(3) Der Arbeitnehmer erklärt sich ferner bereit, vorübergehend für die Dauer von [1 Monat / 2 Monaten] " +
                "aushilfsweise andere zumutbare Tätigkeiten, " +
                "auch an anderen Orten auszuüben. Dies gilt auch nach langjähriger unveränderter Tätigkeit.");
        List<ContentField> verwaltungParagraph2ContentFields = new ArrayList<>();
        ContentField verwaltungParagraph2ContentField2 = new ContentField();
        verwaltungParagraph2ContentField2.setFieldName("1 Monat / 2 Monaten");
        verwaltungParagraph2ContentField2.setFieldDescription("Dauer der aushilfsweise zumutbaren Tätigkeit");
        verwaltungParagraph2ContentField2.setFieldType(FieldType.AUSWAHL);
        verwaltungParagraph2ContentFields.add(verwaltungParagraph2ContentField2);
        verwaltungParagraph2.setContentFields(verwaltungParagraph2ContentFields);
        List<OptionalContent> verwaltungParagraph2OptionalContents = new ArrayList<>();
        OptionalContent verwaltungParagraph2OptionalContent1 = new OptionalContent();
        verwaltungParagraph2OptionalContent1.setTitle("Arbeitsort");
        verwaltungParagraph2OptionalContent1.setShortName("OPT-2-1");
        verwaltungParagraph2OptionalContent1.setContent("(1) Der Arbeitsort ist die Betriebsstätte [Arbeitsort].");
        verwaltungParagraph2OptionalContent1.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph2OptionalContent1.setVersionTemplateNames(VersionTemplateName.MITARBEITER_INTERNATIONAL.name()+VersionTemplateName.MITARBEITER_DEUTSCHLAND.name());
        verwaltungParagraph2OptionalContent1.setSelectionGroup(1);
        List<OptionalContentField> verwaltungParagraph2OptionalContent1Fields = new ArrayList<>();
        OptionalContentField verwaltungParagraph2OptionalContent1Field1 = new OptionalContentField();
        verwaltungParagraph2OptionalContent1Field1.setFieldName("Arbeitsort");
        verwaltungParagraph2OptionalContent1Field1.setFieldDescription("Betriebsstätte");
        verwaltungParagraph2OptionalContent1Fields.add(verwaltungParagraph2OptionalContent1Field1);
        verwaltungParagraph2OptionalContent1.setOptionalContentFields(verwaltungParagraph2OptionalContent1Fields);
        verwaltungParagraph2OptionalContents.add(verwaltungParagraph2OptionalContent1);
        OptionalContent verwaltungParagraph2OptionalContent2 = new OptionalContent();
        verwaltungParagraph2OptionalContent2.setTitle("Reisende");
        verwaltungParagraph2OptionalContent2.setShortName("OPT-2-2");
        verwaltungParagraph2OptionalContent2.setContent("(1) Die vertragliche Tätigkeit erfolgt bundesweit und " +
                "erfordert Dienstreisen in erheblichem Umfang. Diese zählen daher zu den Hauptleistungspflichten des " +
                "Arbeitnehmers. Der Arbeitnehmer erklärt sich zu umfangreichen Dienstreisen ausdrücklich bereit. " +
                "Reise- und Wegezeiten sind Arbeitszeit, die nicht gesondert vergütet wird.");
        verwaltungParagraph2OptionalContent2.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph2OptionalContent2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph2OptionalContent2.setSelectionGroup(1);
        verwaltungParagraph2OptionalContents.add(verwaltungParagraph2OptionalContent2);
        verwaltungParagraph2.setOptionalContents(verwaltungParagraph2OptionalContents);
        verwaltungParagraphList.add(verwaltungParagraph2);


        ContractParagraph verwaltungParagraph3 = new ContractParagraph();
        verwaltungParagraph3.setParagraphNumber("3");
        verwaltungParagraph3.setParagraphTitle("Vertragsbestandteile");
        verwaltungParagraph3.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph3.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph3.setSelectionGroups(false);
        verwaltungParagraph3.setClean(true);
        verwaltungParagraph3.setTemplate(true);
        verwaltungParagraph3.setParagraphContent("(1) Bestandteil des Arbeitsvertrages ist der vom Arbeitnehmer ausgefüllte Personalbogen. Mit der " +
                "Unterschrift unter diesen Vertrag erklärt der Arbeitnehmer, dass alle Angaben zu seiner Person " +
                "vollständig und richtig sind. Wissentlich falsche Angaben können die Anfechtung bzw. die fristlose " +
                "Kündigung des Arbeitsverhältnisses begründen.<br/>" +
                "|(2) Im Übrigen gelten die Allgemeinen Arbeitsbedingungen, die Betriebsordnung und Anweisungen in " +
                "ihren jeweils gültigen Fassungen.");
        verwaltungParagraphList.add(verwaltungParagraph3);


        ContractParagraph verwaltungParagraph4 = new ContractParagraph();
        verwaltungParagraph4.setParagraphNumber("4");
        verwaltungParagraph4.setParagraphTitle("Vergütung");
        verwaltungParagraph4.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph4.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph4.setSelectionGroups(false);
        verwaltungParagraph4.setClean(true);
        verwaltungParagraph4.setTemplate(true);
        verwaltungParagraph4.setParagraphContent("(1) Die Vergütung beträgt<b> [Gehalt] EUR </b>brutto/Monat. " +
                "Die Zahlung des Nettobetrages erfolgt durch Überweisung auf das vom Arbeitnehmer hierfür zu " +
                "benennende Konto bei einer Bank.<br/>" +
                "|(2) Mit der in diesem Vertrag vereinbarten sowie der aufgrund etwaiger weiterer Vereinbarungen " +
                "geleisteten Vergütung sind alle Tätigkeiten des Arbeitnehmers im Rahmen des Arbeitsverhältnisses " +
                "abgegolten. Dies gilt auch für eine etwaige Arbeit an Samstagen sowie Sonn- und Feiertagen sowie " +
                "die Leistung von Schichtarbeit.<br/>" +
                "|(3) Ebenso mit dieser Vergütung abgegolten sind Überstunden im Umfang von 10% der regelmäßigen " +
                "Arbeitszeit, soweit durch die Gesamtvergütung sichergestellt ist, dass jede tatsächlich geleistete " +
                "Arbeitsstunde mindestens mit dem jeweiligen gesetzlichen Mindestlohn vergütet wird. Von der " +
                "vorgenannten Abgeltungsregelung betroffen sind die Überstunden, die nach Ablauf eines " +
                "Kalenderjahres nicht durch Freizeit ausgeglichen wurden und als Habensaldo auf dem " +
                "Arbeitszeitkonto gemäß § 8 Abs. 4 verbleiben. Dies ist bei der Abrechnung des Arbeitszeitkontos zu " +
                "berücksichtigen.");
        List<ContentField> verwaltungParagraph4ContentFields = new ArrayList<>();
        ContentField verwaltungParagraph4ContentField1 = new ContentField();
        verwaltungParagraph4ContentField1.setFieldName("Gehalt");
        verwaltungParagraph4ContentField1.setFieldDescription("Vergütung in EUR brutto/Monat mit Kommastellen");
        verwaltungParagraph4ContentField1.setFieldType(FieldType.TEXT);
        verwaltungParagraph4ContentFields.add(verwaltungParagraph4ContentField1);
        verwaltungParagraph4.setContentFields(verwaltungParagraph4ContentFields);
        verwaltungParagraphList.add(verwaltungParagraph4);


        ContractParagraph verwaltungParagraph5 = new ContractParagraph();
        verwaltungParagraph5.setParagraphNumber("5");
        verwaltungParagraph5.setParagraphTitle("Freiwillige Leistungen");
        verwaltungParagraph5.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph5.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph5.setSelectionGroups(false);
        verwaltungParagraph5.setClean(true);
        verwaltungParagraph5.setTemplate(true);
        verwaltungParagraph5.setParagraphContent("(1) Sämtliche Leistungen, die vom Arbeitgeber erbracht werden, ohne dass diese " +
                "in diesem Vertrag oder einer sonstigen Vereinbarung verbindlich vereinbart worden sind, " +
                "sind freiwillige Leistungen des Arbeitgebers, auf die ein Anspruch nicht besteht und aus " +
                "denen auch bei wiederholter Zahlung eine betriebliche Übung nicht abgeleitet werden kann. " +
                "Dies gilt insbesondere für etwaige Gratifikationen und sonstige Sonderleistungen ohne " +
                "Vergütungscharakter.<br/>" +
                "|(2) Sollte das Unternehmen freiwillige Leistungen erbringen, sind Mitarbeiter ausgeschlossen während " +
                "der Elternzeit, des freiwilligen Wehr- oder Ersatzdienstes, der Teilnahme an Wehrübungen und " +
                "in allen Fällen, in denen das Anstellungsverhältnis ruht.<br/>" +
                "|(3) Freiwillige Leistungen werden auch bei Unterbrechungen des Arbeitsverhältnisses von " +
                "mehr als sechs Wochen innerhalb des Leistungsszeitraums, während derer kein Anspruch auf " +
                "Entgeltfortzahlung besteht, entsprechend der Dauer der Unterbrechung gekürzt.");
        verwaltungParagraphList.add(verwaltungParagraph5);

        ContractParagraph verwaltungParagraph6 = new ContractParagraph();
        verwaltungParagraph6.setParagraphNumber("6");
        verwaltungParagraph6.setParagraphTitle("Kollektivvereinbarungen (Betriebsvereinbarungen, Tarifverträge)");
        verwaltungParagraph6.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph6.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph6.setSelectionGroups(false);
        verwaltungParagraph6.setClean(true);
        verwaltungParagraph6.setTemplate(true);
        verwaltungParagraph6.setParagraphContent("(1) Im Betrieb geltende Kollektivvereinbarungen gehen den " +
                "Bestimmungen dieses Arbeitsvertrages in jedem Fall vor. Dies gilt zunächst für im " +
                "Betrieb derzeit bestehende und/oder zukünftig geschlossene Betriebsvereinbarungen " +
                "und Regelungsabreden. Die Parteien sind sich darüber einig, dass die jeweils gültigen " +
                "einschlägigen Betriebsvereinbarungen sowie die getroffenen Regelungsabreden Anwendung " +
                "finden und für die Dauer ihrer Geltung den Regelungen in diesem Vertrag auch dann " +
                "vorgehen, wenn die vertragliche Regelung im Einzelfall günstiger ist.<br/>" +
                "|(2) Derzeit findet kein Tarifvertrag auf das Arbeitsverhältnis Anwendung. " +
                "Falls zukünftig ein Tarifvertrag zwingend gelten sollte, treten an die Stelle " +
                "der entsprechenden Regelungen dieses Vertrages und der Betriebsvereinbarungen bzw. " +
                "Regelungsabreden oder Gesamtzusagen ausschließlich die tariflichen Regelungen. Der Mitarbeiter " +
                "kann sich auf etwa günstigere Regelungen aus diesem Vertrag, aus " +
                "Betriebsvereinbarungen oder Regelungsabreden während der Zeit der Tarifbindung " +
                "nicht berufen. Alle freiwilligen Leistungen werden für den Zeitraum der Tarifbindung " +
                "außer Kraft gesetzt, es sei denn, diesbezüglich wird eine andere Festlegung getroffen. " +
                "Dies gilt insbesondere für den Fall der zwingenden Geltung eines Lohn- bzw. " +
                "Gehaltstarifvertrages; für den Zeitraum einer solchen Tarifbindung des Arbeitgebers " +
                "(Dauer einer Allgemeinverbindlicherklärung bzw. Laufzeit des verbindlichen Tarifvertrages) " +
                "bemisst sich die Höhe der Vergütung ausschließlich nach den tariflichen Regelungen. " +
                "Das Günstigkeitsprinzip ist insoweit ausgeschlossen.<br/>" +
                "|(3) Im Falle der Anwendung eines Tarifvertrages müssen die tariflichen " +
                "Mindestleistungen innerhalb eines Kalenderjahres erbracht sein. " +
                "Mindervergütungen in einzelnen Monaten können mit übertariflichen Leistungen " +
                "in anderen Monaten verrechnet werden. Sollte das Unternehmen während einer " +
                "Tarifbindung über- oder außertarifliche Leistungen erbringen, sind diese auf " +
                "die tariflichen Leistungen, Tariferhöhungen, Höhergruppierungen anrechenbar.<br/>" +
                "|(4) Nach Ablauf einer Bindung an Kollektivvereinbarungen (Wegfall einer " +
                "Allgemeinverbindlicherklärung bzw. Ablauf des Tarifvertrages oder Ablauf einer " +
                "Betriebsvereinbarung oder Entfall einer Regelungsabrede) wird hiermit vereinbart, " +
                "dass die während der Anwendung kollektivrechtlicher Regelungen verdrängten Bestimmungen " +
                "dieses Arbeitsvertrages in vollem Umfang wieder aufleben. Das gleiche gilt für sonstige " +
                "Vereinbarungen aus und im Zusammenhang mit dem Arbeitsverhältnis, soweit diese durch " +
                "anwendbare kollektivrechtliche Regelungen ganz oder teilweise verdrängt waren. Eine " +
                "etwaige Nachwirkung von Tarifverträgen bzw. von Betriebsvereinbarungen und eine betriebliche " +
                "Übung werden hiermit ausgeschlossen. Insbesondere richtet sich die Vergütung des Arbeitnehmers " +
                "nach Ablauf einer Tarifbindung wieder ausschließlich nach § 4.");
        verwaltungParagraphList.add(verwaltungParagraph6);


        ContractParagraph verwaltungParagraph7 = new ContractParagraph();
        verwaltungParagraph7.setParagraphNumber("7");
        verwaltungParagraph7.setParagraphTitle("Arbeitszeit");
        verwaltungParagraph7.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph7.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph7.setSelectionGroups(false);
        verwaltungParagraph7.setClean(true);
        verwaltungParagraph7.setTemplate(true);
        verwaltungParagraph7.setParagraphContent("(1) Der Arbeitgeber und der Arbeitnehmer sind sich darüber einig, dass die regelmäßige [wöchentliche " +
                "/ monatliche / jährliche] Arbeitszeit<br/><br/>" +
                "<span style=\"display:block; text-align:center; margin:0 auto;\">[Arbeitsstunden] Stunden</span><br/>" +
                "beträgt und zwar unabhängig von den eventuell zukünftig geltenden tariflichen Bestimmungen.<br/>" +
                "|(2) Liegt diese Arbeitszeit über der regelmäßigen Arbeitszeit, die sich aus einem für den Arbeitgeber " +
                "verbindlichen Manteltarifvertrag ergibt und ist für den Arbeitgeber zugleich ein Lohn- bzw. " +
                "Gehaltstarifvertrag verbindlich, dann hat der Arbeitnehmer Anspruch darauf, dass sich die nach dem " +
                "Lohn- bzw. Gehaltstarifvertrag bemessene Mindestvergütung um den der Mehrarbeit entsprechenden " +
                "Anteil erhöht. Nach Ablauf der Tarifbindung (Wegfall einer Allgemeinverbindlichkeitserklärung bzw. " +
                "Ablauf des Tarifvertrages) gilt § 6 Abs. 4 dieses Vertrages.<br/>" +
                "|(3) Der Arbeitnehmer ist bei entsprechendem betrieblichem Bedarf bereit, Mehr- und Überstunden zu " +
                "leisten. Diese Bereitschaft bezieht sich auch auf die Erbringung von Arbeitsleistungen an " +
                "Wochenenden. Überstunden werden nur dann anerkannt, wenn sie vorher schriftlich mit dem " +
                "Arbeitgeber vereinbart bzw. durch diesen angeordnet wurden. Darüber hinaus ist der Arbeitnehmer " +
                "verpflichtet, Beginn und Ende der Überstunden und Mehrarbeit täglich schriftlich zu erfassen und " +
                "diese spätestens am Ende der Kalenderwoche dem Arbeitgeber vorzulegen, wenn im Betrieb generell " +
                "oder für einzelne Tage keine elektronische Zeiterfassung möglich ist; anderenfalls gelten die " +
                "Bestimmungen des § 8.<br/>" +
                "|(4) Die Verteilung der Arbeitszeit erfolgt nach den Vorgaben des Arbeitgebers flexibel entsprechend " +
                "den betrieblichen Erfordernissen.<br/>" +
                "|(5) Der Arbeitgeber ist berechtigt, dem Arbeitnehmer nach billigem Ermessen feste Pausenzeiten " +
                "vorzugeben, für die keine Vergütungspflicht besteht; dabei ist auch eine Vorgabe von Pausen " +
                "zulässig, die die Dauer der gesetzlichen Mindestpausen übersteigt. Soweit eine solche Vorgabe nicht " +
                "besteht, gelten die gesetzlichen Pausenbestimmungen. Der Arbeitnehmer ist verpflichtet, Pausen " +
                "entsprechend der geltenden Vorgabe einzulegen. Wenn und soweit eine Erfassung von individuellen " +
                "Pausenzeiten über das IT-System des Arbeitgebers möglich ist, ist der Arbeitnehmer zur individuellen " +
                "Erfassung seiner Pausenzeiten über dieses System verpflichtet; dies gilt für alle Pausenzeiten, auch " +
                "sog. „Raucherpausen“ und Arbeitsunterbrechungen aus vergleichbaren Gründen. Wenn eine " +
                "individuelle Pausenzeiterfassung über das IT-System nicht möglich ist, werden die vorgegebenen " +
                "Pausenzeiten im Rahmen der Zeiterfassung automatisch pauschaliert in Abzug gebracht, ohne dass " +
                "der Arbeitgeber Dauer und Lage der tatsächlichen Inanspruchnahme von Pausen erfasst oder prüft. " +
                "Eine Änderung der automatisch in Ansatz gebrachten Pausenzeiten, etwa auf der Grundlage " +
                "händischer Aufzeichnungen des Arbeitnehmers, erfolgt nicht.<br/>" +
                "|(6) Der Arbeitgeber ist berechtigt, Kurzarbeit anzuordnen, wenn ein erheblicher, auf wirtschaftlichen " +
                "Gründen oder einem unabwendbaren Ereignis beruhender Arbeitsausfall vorliegt und er dies bei der " +
                "Agentur für Arbeit anzeigt. Im Fall der Anordnung von Kurzarbeit ist der Arbeitnehmer mit der " +
                "vorübergehenden Verkürzung seiner individuellen Arbeitszeit sowie der dementsprechenden " +
                "Reduzierung seiner Vergütung einverstanden, wenn und soweit die Voraussetzungen für die " +
                "Gewährung von Kurzarbeitergeld erfüllt sind. Bei vollständigem Arbeitsausfall können die Arbeitszeit " +
                "und dementsprechend auch die Vergütung auf Null herabgesetzt werden (Kurzarbeit Null). Der " +
                "Arbeitgeber hat dem Arbeitnehmer gegenüber bei der Anordnung von Kurzarbeit eine " +
                "Ankündigungsfrist von zwei Wochen einzuhalten; diese Ankündigungsfrist kann durch eine " +
                "Betriebsvereinbarung abgekürzt werden.<br/>");
        List<ContentField> verwaltungParagraph7ContentFields = new ArrayList<>();
        ContentField verwaltungParagraph7ContentField1 = new ContentField();
        verwaltungParagraph7ContentField1.setFieldName("wöchentliche / monatliche / jährliche");
        verwaltungParagraph7ContentField1.setFieldDescription("zeitliche Einordnung für die Angabe der Arbeitszeit");
        verwaltungParagraph7ContentField1.setFieldType(FieldType.AUSWAHL);
        verwaltungParagraph7ContentFields.add(verwaltungParagraph7ContentField1);
        ContentField verwaltungParagraph7ContentField2 = new ContentField();
        verwaltungParagraph7ContentField2.setFieldName("Arbeitsstunden");
        verwaltungParagraph7ContentField2.setFieldDescription("Anzahl der Arbeitsstunden in der gewählten Einordnung");
        verwaltungParagraph7ContentField2.setFieldType(FieldType.TEXT);
        verwaltungParagraph7ContentFields.add(verwaltungParagraph7ContentField2);
        verwaltungParagraph7.setContentFields(verwaltungParagraph7ContentFields);
        verwaltungParagraphList.add(verwaltungParagraph7);


        ContractParagraph verwaltungParagraph8 = new ContractParagraph();
        verwaltungParagraph8.setParagraphNumber("8");
        verwaltungParagraph8.setParagraphTitle("Arbeitszeitkonto");
        verwaltungParagraph8.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph8.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph8.setSelectionGroups(false);
        verwaltungParagraph8.setClean(true);
        verwaltungParagraph8.setTemplate(true);
        verwaltungParagraph8.setParagraphContent("(1) Der Arbeitgeber führt für den Arbeitnehmer ein individuelles Arbeitszeitkonto. In diesem werden die " +
                "tatsächlich geleisteten Arbeitszeiten erfasst und mit der regelmäßigen Soll-Arbeitszeit nach § 7 " +
                "saldiert. Dabei kann das Arbeitszeitkonto bis zum Umfang von 200 Arbeitsstunden auch im Soll " +
                "belastet werden; der Arbeitnehmer ist in diesem Fall auch kalenderjahrübergreifend zur Nacharbeit " +
                "verpflichtet. Zeiten der Entgeltfortzahlung (insbesondere Krankheit, Urlaub) werden mit der " +
                "vereinbarten täglichen Arbeitszeit erfasst (Ist = Soll). Zeiten des Ruhens des Arbeitsverhältnisses " +
                "bleiben für das Arbeitszeitkonto neutral.<br/>" +
                "|(2) Die Zeiterfassung erfolgt – vorbehaltlich einer abweichenden Vorgabe gemäß Abs. 3 – durch " +
                "Ein- und Ausstempeln an den betrieblich vorgesehenen Terminals am jeweiligen Personaleingang. " +
                "Der Arbeitgeber ist hierbei berechtigt, bei der Erfassung der tatsächlich geleisteten Arbeitszeiten unter " +
                "Berücksichtigung der konkreten betrieblichen Anforderungen, insbesondere der innerbetrieblichen " +
                "Wege- und vergleichbarer Zeiten, in angemessenem Umfang Kappungsgrenzen und Schwellenwerte " +
                "einzuführen und/oder Rundungen vorzunehmen. Soweit für den Arbeitnehmer eine betriebliche " +
                "Arbeitszeiteinteilung oder Schichtplanung besteht, ist der Arbeitgeber ferner berechtigt, angemessene " +
                "automatische Kappungen der betrieblichen Anwesenheitszeiten vorzusehen.<br/>" +
                "|(3) Der Arbeitgeber ist berechtigt, im Betrieb insgesamt, für einzelne Abteilungen oder für einzelne " +
                "Arbeitsplätze (auch Flexoffice-Arbeitsplätze) eine Erfassung der tatsächlichen Arbeitszeiten über das " +
                "IT-System am Arbeitsplatz bzw. dafür vorgesehene Erfassungsterminals am jeweiligen Standort " +
                "vorzugeben. Wenn und soweit eine solche Vorgabe des Arbeitgebers besteht, ist der Arbeitnehmer " +
                "abweichend von Abs. 2 verpflichtet, Beginn und Ende der täglichen Arbeitszeit gemäß den " +
                "Vorgaben des Arbeitgebers zu erfassen. Eine Erfassung oder ein Ein- und Ausstempeln abweichend " +
                "von den Vorgaben des Arbeitgebers ist unzulässig. Unabhängig hiervon ist und bleibt der " +
                "Arbeitnehmer zur Registrierung des Kommens und Gehens beim Personaleingang verpflichtet. Das " +
                "gilt auch bei Inanspruchnahme von Pausen außerhalb des Hauses. Diese Registrierung hat keinen " +
                "Einfluss auf die Arbeitszeit- und Pausenzeiterfassung und wird auch nicht hierfür herangezogen.<br/>" +
                "|(4) Manuelle Korrekturen von erfassten Arbeitszeiten werden nur vorgenommen, wenn diese vorab " +
                "vom Vorgesetzten genehmigt worden sind.<br/>" +
                "|(5) Die nach den vorstehenden Absätzen ermittelte Anwesenheitszeit wird um die Pausenzeiten nach " +
                "§ 7 Abs. 5 gekürzt und als Netto-Arbeitszeit in das Arbeitszeitkonto übernommen.<br/>" +
                "|(6) Das Arbeitszeitkonto wird jeweils zum Ende eines Kalenderjahres abgerechnet. Bei unterjährigem " +
                "Ausscheiden des Arbeitnehmers erfolgt die Abrechnung spätestens bei Beendigung des " +
                "Arbeitsverhältnisses. Bei Abrechnung des Arbeitszeitkontos wird geprüft, ob der Arbeitnehmer im " +
                "zurückliegenden Kalenderjahr insgesamt eine Vergütung (einschließlich etwaiger " +
                "Ausgleichszahlungen auf die Mindestvergütung) erhalten hat, die mindestens der Summe der gemäß " +
                "den Absätzen 1 bis 5 erfassten Arbeitsstunden multipliziert mit dem jeweiligen gesetzlichen " +
                "Mindestlohn entspricht. Sollte dies nicht der Fall sein, so erfolgt zugunsten des Arbeitnehmers eine " +
                "entgeltliche Abgeltung so vieler zusätzlicher Arbeitsstunden aus dem Arbeitszeitkonto, dass der " +
                "gesetzliche Mindestlohnanspruch erfüllt ist. Im Falle des unterjährigen Ausscheidens gilt als " +
                "zurückliegendes Kalenderjahr das laufende Kalenderjahr bis zum Zeitpunkt des Ausscheidens.<br/>" +
                "|(7) Ein nach Abrechnung und etwaiger Abgeltung gemäß Abs. 6 verbleibender Habensaldo des " +
                "Arbeitszeitkontos wird um die gemäß § 4 Abs. 3 im zurückliegenden Abrechnungszeitraum bereits mit " +
                "der laufenden Vergütung abgegoltenen Überstunden gekürzt; der Aufbau eines Sollsaldos durch diese " +
                "Kürzung ist ausgeschlossen. Ein hiernach noch verbleidender Soll- oder Habensaldo wird als " +
                "Anfangssaldo in das Arbeitszeitkonto des nachfolgenden Abrechnungszeitraums übertragen.<br/>" +
                "|(8) Bei Guthaben des Arbeitszeitkontos handelt es sich um reine Zeitguthaben, die durch Freizeit " +
                "abzubauen sind. Der Arbeitgeber kann einen Freizeitausgleich im Rahmen des § 106 GewO auch " +
                "einseitig anordnen. Während des Laufs des Arbeitsverhältnisses besteht außer in den Fällen des " +
                "Abs. 6 kein Anspruch auf Abgeltung von Zeitguthaben durch zusätzliche Vergütung. " +
                "Für eine Abgeltung von Arbeitsstunden nach Abs. 6 werden ebenso wie für eine Abgeltung von " +
                "restlichen Zeitguthaben bei Beendigung des Arbeitsverhältnisses die betrieblichen Grundsätze zur " +
                "Berechnung des Urlaubsentgelts zugrunde gelegt; ein Zuschlag wird nicht geleistet.<br/>" +
                "|(9) Betriebsvereinbarungen über Arbeitszeitkonten gehen den vorstehenden Bestimmungen vor (§ 6).<br/>");
        verwaltungParagraphList.add(verwaltungParagraph8);


        ContractParagraph verwaltungParagraph9 = new ContractParagraph();
        verwaltungParagraph9.setParagraphNumber("9");
        verwaltungParagraph9.setParagraphTitle("Beendigung des Arbeitsverhältnisses");
        verwaltungParagraph9.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph9.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph9.setSelectionGroups(false);
        verwaltungParagraph9.setClean(true);
        verwaltungParagraph9.setTemplate(true);
        verwaltungParagraph9.setParagraphContent("(1) Das Arbeitsverhältnis ist jederzeit ordentlich kündbar; dies gilt auch während einer etwaigen " +
                "Befristung. Während einer vereinbarten Probezeit (§ 1 Abs. 2) beträgt die Kündigungsfrist 2 Wochen. " +
                "Im Übrigen gelten die Kündigungsfristen des § 622 BGB. Das Arbeitsverhältnis kann auch schon vor " +
                "seinem Beginn gekündigt werden. Die Kündigungsfrist beginnt in diesem Fall mit dem Zugang der " +
                "Kündigung.<br/>" +
                "|(2) Eine fristlose Kündigung des Arbeitsverhältnisses ist bei Vorliegen eines wichtigen Grundes " +
                "jederzeit möglich. Sollte eine fristlose Kündigung unwirksam sein, so gilt diese als fristgemäße " +
                "Kündigung zum nächsten zulässigen Kündigungstermin.<br/>" +
                "|(3) Jede Kündigung bedarf der Schriftform (§ 623 BGB). Will der Arbeitnehnmer geltend machen, " +
                "dass eine Kündigung sozial ungerechtfertigt oder aus anderen Gründen rechtsunwirksam ist, so muss er " +
                "innerhalb von drei Wochen nach Zugang der schriftlichen Kündigung Klage beim Arbeitsgericht auf " +
                "Feststellung erheben, dass das Arbeitsverhältnis durch die Kündigung nicht aufgelöst ist " +
                "(§ 4 S. 1 KSchG). Wird die Rechtsunwirksamkeit einer Kündigung nicht rechtzeitig geltend " +
                "gemacht, so gilt die Kündigung als von Anfang an rechtswirksam (§ 7 KSchG).<br/>" +
                "|(4) Das Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf, mit Ablauf des Monats, in dem " +
                "der Arbeitnehmer das gesetzlich geregelte Regelrentenalter erreicht. Bis zu diesem Termin kann das " +
                "Arbeitsverhältnis gemäß Abs. 1 von jeder Partei unter Einhaltung der dort geregelten " +
                "Kündigungsfristen gekündigt werden. Soweit sich die Regelaltersgrenze ändert, kommen die " +
                "geänderten gesetzlichen Vorschriften zur Anwendung.<br/>" +
                "|(5) Das Arbeitsverhältnis endet ebenfalls zu dem Zeitpunkt, ab dem der Arbeitnehmer eine " +
                "unbefristete Rente wegen voller Erwerbsminderung erhält, frühestens aber an dem Tag, an dem der " +
                "entsprechende Rentenbescheid dem Arbeitnehmer zugeht und der Arbeitnehmer gegen diesen " +
                "Bescheid nicht Widerspruch einlegt. Legt der Arbeitnehmer Widerspruch gegen den Bescheid ein, so " +
                "endet das Arbeitsverhältnis, sobald rechtskräftig über die Gewährung der Erwerbsminderungsrente " +
                "entschieden ist. Der Arbeitnehmer verpflichtet sich, den Arbeitgeber über den Zugang eines " +
                "entsprechenden Rentenbescheides zu informieren. Während des Bezugs einer befristeten Rente " +
                "wegen voller Erwerbsminderung ruht das Arbeitsverhältnis.<br/>" +
                "|(6) Der Arbeitnehmer kann während der Kündigungsfrist unter Fortzahlung der Vergütung und unter " +
                "Anrechnung auf Urlaubsansprüche sowie auf ein etwaiges Zeitguthaben aus dem Arbeitszeitkonto von " +
                "der Arbeitsverpflichtung freigestellt werden.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph9);


        ContractParagraph verwaltungParagraph10 = new ContractParagraph();
        verwaltungParagraph10.setParagraphNumber("10");
        verwaltungParagraph10.setParagraphTitle("Allgemeine Pflichten");
        verwaltungParagraph10.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph10.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph10.setSelectionGroups(false);
        verwaltungParagraph10.setClean(true);
        verwaltungParagraph10.setTemplate(true);
        verwaltungParagraph10.setParagraphContent("(1) Der Arbeitnehmer ist verpflichtet, den Arbeitsanweisungen der zuständigen Vorgesetzten Folge zu " +
                "leisten. Er wird die ihm übertragenen Arbeiten sorgfältig und gewissenhaft ausführen.<br/>" +
                "|(2) Der Arbeitnehmer darf Fahrzeuge auf dem Betriebsgelände nur in den vom Arbeitgeber angewiesenen Bereichen " +
                "und nur mit dessen ausdrücklicher Genehmigung abstellen. Das Abstellen geschieht in jedem Fall " +
                "ausschließlich auf Gefahr des Arbeitnehmers.<br/>" +
                "|(3) Der Arbeitnehmer hat auf ein gepflegtes äußeres Erscheinungsbild zu achten, insbesondere die " +
                "berufs- oder betriebsübliche Kleidung zu tragen. Im Übrigen gilt die jeweils gültige Betriebsordnung.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph10);


        ContractParagraph verwaltungParagraph11 = new ContractParagraph();
        verwaltungParagraph11.setParagraphNumber("11");
        verwaltungParagraph11.setParagraphTitle("Weitere Tätigkeiten, Wettbewerb");
        verwaltungParagraph11.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph11.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph11.setSelectionGroups(false);
        verwaltungParagraph11.setClean(true);
        verwaltungParagraph11.setTemplate(true);
        verwaltungParagraph11.setParagraphContent("(1) Während des Bestandes dieses Arbeitsvertrages ist dem Arbeitnehmer jegliche Tätigkeit für ein " +
                "Unternehmen untersagt, das mit dem Arbeitgeber im Wettbewerb steht. Das gleiche gilt für eine " +
                "Beteiligung an einem solchen Unternehmen, soweit diese nicht in einer reinen Kapitalanlage ohne " +
                "gesellschaftsrechtliche Einflussnahmemöglichkeit besteht.<br/>" +
                "|(2) Eine anderweitige Erwerbstätigkeit ist ihm nur mit ausdrücklicher Zustimmung des Arbeitgebers " +
                "gestattet, wobei der Arbeitgeber diese Zustimmung erteilen wird, soweit berechtigte Belange des " +
                "Arbeitgebers nicht erheblich beeinträchtigt werden. Tritt eine solche Beeinträchtigung später auf, so " +
                "kann der Arbeitgeber die Zustimmung widerrufen.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph11);


        ContractParagraph verwaltungParagraph12 = new ContractParagraph();
        verwaltungParagraph12.setParagraphNumber("12");
        verwaltungParagraph12.setParagraphTitle("Krankheit und Arbeitsverhinderung");
        verwaltungParagraph12.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph12.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph12.setSelectionGroups(false);
        verwaltungParagraph12.setClean(true);
        verwaltungParagraph12.setTemplate(true);
        verwaltungParagraph12.setParagraphContent("(1) Der Arbeitnehmer verpflichtet sich, jede krankheitsbedingte Arbeitsunfähigkeit und " +
                "Arbeitsverhinderung aus anderen Gründen sowie deren voraussichtliche Dauer dem Vorgesetzten " +
                "unverzüglich nach Erkennbarkeit, spätestens am ersten Tag der Abwesenheit zu Dienstbeginn, " +
                "telefonisch zu melden. Dauert die Arbeitsunfähigkeit bzw. Arbeitsverhinderung länger als ursprünglich " +
                "mitgeteilt, gelten die Pflichten entsprechend.<br/>" +
                "|(2) Eine krankheitsbedingte Arbeitsunfähigkeit ist vom ersten Tag an durch ein ärztliches Attest zu " +
                "belegen. Das Gleiche gilt, wenn die Arbeitsunfähigkeit länger dauert als ursprünglich angegeben.<br/>" +
                "|(3) Bei einer akut aufgetretenen Pflegesituation im Sinne von § 2 PflegeZG ist der Arbeitnehmer " +
                "verpflichtet, die Pflegebedürftigkeit des nahen Angehörigen und die Erforderlichkeit der Pflege durch " +
                "ein ärztliches Attest nachzuweisen. Das Attest muss spätestens am dritten Arbeitstag vorgelegt " +
                "werden.<br/>" +
                "|(4) Der Arbeitnehmer ist verpflichtet, einen Arbeitsunfall unverzüglich anzuzeigen.<br/>" +
                "|(5) Der Arbeitnehmer ist bereit, sich im Falle von durch Tatsachen begründeten Zweifeln an seiner " +
                "Arbeitsfähigkeit oder an dem Bestand einer zur Arbeitsunfähigkeit führenden Erkrankung auf " +
                "Verlangen des Arbeitgebers einer vertrauensärztlichen Untersuchung zu unterziehen.<br/>" +
                "|(6) Der Arbeitgeber ist zur Entgeltfortzahlung nur in den gesetzlich zwingend normierten Fällen " +
                "verpflichtet. Die Bestimmung des § 616 BGB wird – soweit rechtlich zulässig – ausgeschlossen. Dies " +
                "gilt insbesondere bei Arbeitsverhinderung wegen Kindpflege und Pflege nach dem Pflegezeitgesetz.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph12);


        ContractParagraph verwaltungParagraph13 = new ContractParagraph();
        verwaltungParagraph13.setParagraphNumber("13");
        verwaltungParagraph13.setParagraphTitle("Abtretung von Schadensersatzforderungen");
        verwaltungParagraph13.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph13.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph13.setSelectionGroups(false);
        verwaltungParagraph13.setClean(true);
        verwaltungParagraph13.setTemplate(true);
        verwaltungParagraph13.setParagraphContent("Schadensersatzansprüche, die der Arbeitnehmer bei Unfall oder Krankheit gegen Dritte erwirkt, " +
                "werden hiermit an den Arbeitgeber bis zur Höhe der Beträge abgetreten, die der Arbeitgeber aufgrund " +
                "gesetzlicher, tariflicher oder vertraglicher Bestimmungen für die Dauer der Arbeitsunfähigkeit " +
                "gewähren muss. Dazu hat der Arbeitnehmer unverzüglich dem Arbeitgeber die zur Geltendmachung " +
                "der Schadenersatzansprüche erforderlichen Angaben zu machen.<br/><br/>");
        verwaltungParagraphList.add(verwaltungParagraph13);


        ContractParagraph verwaltungParagraph14 = new ContractParagraph();
        verwaltungParagraph14.setParagraphNumber("14");
        verwaltungParagraph14.setParagraphTitle("Verpfändung des Arbeitseinkommens");
        verwaltungParagraph14.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph14.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph14.setSelectionGroups(false);
        verwaltungParagraph14.setClean(true);
        verwaltungParagraph14.setTemplate(true);
        verwaltungParagraph14.setParagraphContent("Der Arbeitnehmer darf seine Vergütungsansprüche an Dritte nur nach vorheriger schriftlicher<br/>" +
                "Zustimmung durch den Arbeitgeber verpfänden oder abtreten.<br/><br/>");
        verwaltungParagraphList.add(verwaltungParagraph14);


        ContractParagraph verwaltungParagraph15 = new ContractParagraph();
        verwaltungParagraph15.setParagraphNumber("15");
        verwaltungParagraph15.setParagraphTitle("Fortbildung");
        verwaltungParagraph15.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph15.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph15.setSelectionGroups(false);
        verwaltungParagraph15.setClean(true);
        verwaltungParagraph15.setTemplate(true);
        verwaltungParagraph15.setParagraphContent("(1) Der Arbeitnehmer verpflichtet sich, die vom Arbeitgeber direkt oder indirekt gebotenen " +
                "Fortbildungsmöglichkeiten zu nutzen. Er ist bereit, auch Seminare und Schulungen außerhalb des " +
                "Betriebes und an anderen Orten zu besuchen, selbst wenn damit eine mehrtägige Ortsabwesenheit " +
                "verbunden ist. Dauert die Fortbildungsmaßnahme einschließlich eventueller, vom Arbeitnehmer selbst " +
                "organisierter Wegezeiten länger als die vertraglich abzuleistende Arbeitszeit, so zählt die darüber " +
                "hinausgehende Zeit nicht als Mehrarbeit.<br/>" +
                "|(2) Die Teilnahme an Schulungen und Seminaren ist generell durch die Vergütung nach § 4 " +
                "umfassend abgegolten.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph15);


        ContractParagraph verwaltungParagraph16 = new ContractParagraph();
        verwaltungParagraph16.setParagraphNumber("16");
        verwaltungParagraph16.setParagraphTitle("Gesetzlicher Urlaub");
        verwaltungParagraph16.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph16.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph16.setSelectionGroups(false);
        verwaltungParagraph16.setClean(true);
        verwaltungParagraph16.setTemplate(true);
        verwaltungParagraph16.setParagraphContent("(1) Die Dauer des Urlaubs und die Bezahlung richten sich nach dem Bundesurlaubsgesetz. Der " +
                "Anspruch auf Urlaub vermindert sich – soweit gesetzlich zulässig – zeitanteilig für Tage innerhalb des " +
                "jeweiligen Urlaubsjahres, an denen weder Arbeitspflicht noch Entgeltanspruch bestehen; dies gilt " +
                "insbesondere bei unbezahltem Sonderurlaub und Kurzarbeit, nicht aber im Falle einer Erkrankung des " +
                "Arbeitnehmers.<br/>" +
                "|(2) Der Urlaub wird im Rahmen der betrieblichen Möglichkeiten und unter Berücksichtigung der " +
                "persönlichen Wünsche des Arbeitnehmers gewährt.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph16);


        ContractParagraph verwaltungParagraph16A = new ContractParagraph();
        verwaltungParagraph16A.setParagraphNumber("16a");
        verwaltungParagraph16A.setParagraphTitle("Freiwillig gewährter Urlaub");
        verwaltungParagraph16A.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph16A.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph16A.setSelectionGroups(false);
        verwaltungParagraph16A.setClean(true);
        verwaltungParagraph16A.setTemplate(true);
        verwaltungParagraph16A.setParagraphContent("(1) Gewährt das Unternehmen über den gesetzlichen Urlaub hinaus zusätzlichen Urlaub, handelt es " +
                "sich um eine freiwillige Leistung, auf die auch nach wiederholter Gewährung kein Rechtsanspruch " +
                "entsteht. Auch das Entstehen einer betrieblichen Übung wird ausdrücklich ausgeschlossen.<br/>" +
                "|(2) Gewährt das Unternehmen auf Basis der Freiwilligkeit generell zusätzlichen Urlaub, haben nur " +
                "diejenigen Arbeitnehmer Anspruch auf freiwilligen Urlaub, die im Urlaubsjahr ganzjährig in einem " +
                "Arbeitsverhältnis gestanden haben. Im Ein- und Austrittsjahr besteht kein Anspruch auf freiwilligen " +
                "Urlaub. § 16 Abs. 1 S. 2 dieses Vertrages gilt auch für den freiwilligen Urlaub entsprechend.<br/>" +
                "|(3) Der genommene Urlaub wird zunächst auf den gesetzlichen und sodann auf den etwa freiwillig " +
                "gewährten Urlaub angerechnet.<br/>" +
                "|(4) Nicht in Anspruch genommener freiwillig gewährter Urlaub verfällt grundsätzlich mit Ablauf des " +
                "31.03. des Folgejahres, ohne dass es eines ausdrücklichen Hinweises des Arbeitsgebers im Einzelfall " +
                "bedarf. Dies gilt auch bei einer langandauernden Erkrankung. Für bei Ausscheiden aus dem " +
                "Arbeitsverhältnis noch bestehenden freiwillig gewährten Urlaub erfolgt keine Abgeltung oder ein " +
                "sonstiger Ausgleich.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph16A);

        ContractParagraph verwaltungParagraph17 = new ContractParagraph();
        verwaltungParagraph17.setParagraphNumber("17");
        verwaltungParagraph17.setParagraphTitle("Vertraulichkeit");
        verwaltungParagraph17.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph17.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph17.setSelectionGroups(false);
        verwaltungParagraph17.setClean(true);
        verwaltungParagraph17.setTemplate(true);
        verwaltungParagraph17.setParagraphContent("(1) Der Arbeitnehmer ist verpflichtet, insbesondere auch während der Zeit nach Beendigung dieses " +
                "Arbeitsvertrages alle vertraulichen Angelegenheiten, Betriebs- und Geschäftsgeheimnisse des " +
                "Arbeitgebers und verbundener Unternehmen, welche ihm bei Ausübung seiner Tätigkeiten für den " +
                "Arbeitgeber zur Kenntnis gelangen oder die vom Arbeitgeber als vertraulich bezeichnet werden, streng " +
                "geheim zu halten. Als vertrauliche Angelegenheiten in diesem Sinne gelten auch " +
                "Geschäftsgeheimnisse im Sinne des GeschGehG, deren Offenlegung nicht nach § 3 Abs. 2 " +
                "GeschGehG erlaubt ist, insbesondere Verfahren, Daten, Know-how, Marketing-Pläne, " +
                "Geschäftsplanungen, Budgets, Lizenzen, Preise, Kosten und Kunden- und Lieferantenlisten. " +
                "In Zweifelsfällen ist der Arbeitnehmer verpflichtet, eine Weisung des Arbeitgebers einzuholen, ob eine " +
                "bestimmte Tatsache als vertraulich zu behandeln ist.<br/>" +
                "|(2) Der Arbeitnehmer sichert zu, dass er insbesondere sämtliche ihm in Ausübung des " +
                "Arbeitsverhältnisses übergebenen oder bekannt gewordenen Daten und Dokumente über die " +
                "Angelegenheiten des Unternehmens, seiner Mitarbeiter, Lieferanten, Kunden und sonstigen Kontakte " +
                "zeitlich unbegrenzt, insbesondere auch über die Dauer des Vertragsverhältnisses hinaus, streng " +
                "vertraulich behandelt und geheim hält. Er versichert, dass er derartige Daten und Dokumente Dritten " +
                "nicht zugänglich machen oder sonst zum eigenen oder fremden Nutzen preisgeben wird, außer in " +
                "Erfüllung seiner vertraglichen Pflichten.<br/>" +
                "|(3) In besonderer Weise sind Daten von Kunden vertraulich zu behandeln und vor dem Zugriff Dritter " +
                "zu schützen. Auch eine Kommunikation mit Kunden über soziale Netzwerke oder unverschlüsselte " +
                "Messenger-Dienste, insbesondere WhatsApp, ist untersagt, soweit diese dem Arbeitnehmer nicht vom " +
                "Arbeitgeber generell oder für Einzelfälle gestattet worden ist.<br/>" +
                "|(4) Ausgenommen von den Verschwiegenheitsverpflichtungen sind Angaben gegenüber Behörden " +
                "oder aufgrund gesetzlicher Verpflichtung, soweit diese erforderlich sind, sowie Offenlegungen zum " +
                "Schutz eines berechtigten Interesses gemäß § 5 GeschGehG; der Arbeitnehmer ist aber verpflichtet " +
                "zunächst zu versuchen, den Schutz dieser berechtigen Interessen durch eine zumutbare interne " +
                "Meldung und Abhilfe zu erreichen und sich nicht ohne Weiteres an externe Dritte zu wenden.<br/>" +
                "<br/>" +
                "(5)<b> Alle Angaben, die dieses Arbeitsverhältnis betreffen, sind vertraulich zu behandeln und " +
                "dürfen Dritten nicht zugänglich gemacht werden.</b><br/>");
        verwaltungParagraphList.add(verwaltungParagraph17);


        ContractParagraph verwaltungParagraph18 = new ContractParagraph();
        verwaltungParagraph18.setParagraphNumber("18");
        verwaltungParagraph18.setParagraphTitle("Zuverlässigkeit");
        verwaltungParagraph18.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph18.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph18.setSelectionGroups(false);
        verwaltungParagraph18.setClean(true);
        verwaltungParagraph18.setTemplate(true);
        verwaltungParagraph18.setParagraphContent("(1) Der Arbeitnehmer versichert, dass bei Vertragsabschluss keine einschlägigen Vorstrafen vorliegen " +
                "oder Verfahren anhängig sind, die Zweifel an seiner beruflichen Gewissenhaftigkeit, Zuverlässigkeit " +
                "und seinem Verantwortungsgefühl begründen können.<br/>" +
                "|(2) Ein aktueller Eintrag in einem Führungszeugnis, der Zweifel an der Eignung und/oder " +
                "Zuverlässigkeit des Arbeitnehmers begründet, kann Anlass für arbeitsrechtliche Konsequenzen des " +
                "Arbeitgebers bis hin zu einer ordentlichen oder sogar außerordentlichen Kündigung sein.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph18);


        ContractParagraph verwaltungParagraph19 = new ContractParagraph();
        verwaltungParagraph19.setParagraphNumber("19");
        verwaltungParagraph19.setParagraphTitle("Rückgabe von Betriebsmitteln und Firmenunterlagen");
        verwaltungParagraph19.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph19.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph19.setSelectionGroups(false);
        verwaltungParagraph19.setClean(true);
        verwaltungParagraph19.setTemplate(true);
        verwaltungParagraph19.setParagraphContent("Beim Ausscheiden des Arbeitnehmers aus dem Betrieb sind alle dem Arbeitgeber gehörenden " +
                "Betriebsmittel, Unterlagen, schriftliche und digitale Aufzeichnungen, Werkzeuge, Zugangschips bzw. " +
                "Zugangskarten, Schlüssel etc. herauszugeben. Wird der Arbeitnehmer während des Arbeitsverhältnisses " +
                "freigestellt, so kann der Arbeitgeber die Herausgabe bereits ab Beginn der Freistellung verlangen. " +
                "Dem Arbeitgeber steht ein Zurückbehaltungsrecht an sämtlichen Leistungen bis zur Erfüllung fälliger " +
                "Herausgabeansprüche zu.<br/><br/>");
        verwaltungParagraphList.add(verwaltungParagraph19);


        ContractParagraph verwaltungParagraph20 = new ContractParagraph();
        verwaltungParagraph20.setParagraphNumber("20");
        verwaltungParagraph20.setParagraphTitle("Schriftform, Rechtsgültigkeit");
        verwaltungParagraph20.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph20.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph20.setSelectionGroups(false);
        verwaltungParagraph20.setClean(true);
        verwaltungParagraph20.setTemplate(true);
        verwaltungParagraph20.setParagraphContent("(1) Änderungen und Ergänzungen dieses Vertrages " +
                "bedürfen, soweit sie nicht auf einer individuellen Abrede beruhen, der Schriftform. " +
                "Gleiches gilt auch für die Aufhebung dieses Schriftformerfordernisses. Den Parteien " +
                "ist bewusst, dass ein Erwachsen von Ansprüchen aus einer betrieblichen Übung daher " +
                "ausgeschlossen ist.<br/>" +
                "|(2) Für diesen Arbeitsvertrag gilt ausschließlich deutsches Recht als vereinbart.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph20);


        ContractParagraph verwaltungParagraph21 = new ContractParagraph();
        verwaltungParagraph21.setParagraphNumber("21");
        verwaltungParagraph21.setParagraphTitle("Verfall von Ansprüchen");
        verwaltungParagraph21.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph21.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph21.setSelectionGroups(false);
        verwaltungParagraph21.setClean(true);
        verwaltungParagraph21.setTemplate(true);
        verwaltungParagraph21.setParagraphContent("(1) Alle wechselseitigen Ansprüche aus dem Arbeitsvertrag und solche, die damit in Verbindung " +
                "stehen sowie Ansprüche aus Anlass der Beendigung des Arbeitsverhältnisses verfallen, wenn sie nicht " +
                "innerhalb von drei Monaten nach Fälligkeit gegenüber der anderen Vertragspartei in Textform geltend " +
                "gemacht worden sind. War die Fälligkeit des Anspruches für den Arbeitnehmer auch bei größter " +
                "Sorgfalt nicht erkennbar, so kann der Arbeitnehmer Ansprüche abweichend noch innerhalb von drei " +
                "Monaten nach dem Zeitpunkt geltend machen, an dem der Arbeitnehmer Kenntnis von der Fälligkeit " +
                "haben musste.<br/>" +
                "|(2) Lehnt die andere Vertragspartei den rechtzeitig geltend gemachten Anspruch ab oder erklärt sie " +
                "sich nicht innerhalb einer Erklärungsfrist von vier Wochen nach der Geltendmachung des Anspruchs, " +
                "so verfällt dieser dennoch, wenn er nicht innerhalb einer Frist von drei Monaten nach Ablehnung oder " +
                "dem Ablauf der Erklärungsfrist gerichtlich geltend gemacht wird.<br/>" +
                "|(3) Die Verfallfristen gelten nicht für wechselseitige Ansprüche aus einer Haftung wegen Vorsatzes, " +
                "für Ansprüche wegen Schäden aus der Verletzung des Lebens, des Körpers oder der Gesundheit, " +
                "für Ansprüche auf verbindliche Mindestlöhne, andere nach staatlichem Recht zwingende Mindestarbeitsbedingungen " +
                "und nicht für sonstige Ansprüche, die kraft Gesetzes der Regelung durch eine Ausschlussfrist entzogen sind. " +
                "Sie gelten ebenfalls nicht für wechselseitige Ansprüche auf Erstattung von Lohn- und Kirchensteuer, " +
                "Solidaritätszuschlag sowie Sozialversicherungsbeiträgen, die durch Nachberechnung entstanden sind.<br/>");
        verwaltungParagraphList.add(verwaltungParagraph21);


        ContractParagraph verwaltungParagraph22 = new ContractParagraph();
        verwaltungParagraph22.setParagraphNumber("22");
        verwaltungParagraph22.setParagraphTitle("– entfällt –");
        verwaltungParagraph22.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph22.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph22.setSelectionGroups(false);
        verwaltungParagraph22.setClean(true);
        verwaltungParagraph22.setTemplate(true);
        verwaltungParagraphList.add(verwaltungParagraph22);


        ContractParagraph verwaltungParagraph23 = new ContractParagraph();
        verwaltungParagraph23.setParagraphNumber("23");
        verwaltungParagraph23.setParagraphTitle("– entfällt –");
        verwaltungParagraph23.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph23.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph23.setSelectionGroups(false);
        verwaltungParagraph23.setClean(true);
        verwaltungParagraph23.setTemplate(true);
        verwaltungParagraphList.add(verwaltungParagraph23);


        ContractParagraph verwaltungParagraph24 = new ContractParagraph();
        verwaltungParagraph24.setParagraphNumber("24");
        verwaltungParagraph24.setParagraphTitle("Personalvollmachten");
        verwaltungParagraph24.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph24.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph24.setSelectionGroups(false);
        verwaltungParagraph24.setClean(true);
        verwaltungParagraph24.setTemplate(true);
        verwaltungParagraph24.setParagraphContent("Dem Arbeitnehmer ist bekannt, dass jeder Geschäftsführer und jeder Prokurist des Arbeitgebers und " +
                "seines Komplementärs einzeln und unabhängig von der Reichweite seines Vertretungsrechts im " +
                "Allgemeinen sowie die weiteren Mitglieder der Geschäftsleitung jeweils einzeln durch den Arbeitgeber " +
                "bevollmächtigt sind, alle Rechtshandlungen betreffend das Arbeitsverhältnis für den Arbeitgeber " +
                "vorzunehmen. Diese Personalvollmacht erstreckt sich insbesondere auf Einstellungen sowie den " +
                "Ausspruch und die Entgegennahme von Kündigungen. Der Arbeitnehmer wird laufend über " +
                "betriebliche Aushänge über die Personen informiert, die als weitere Mitglieder der Geschäftsleitung " +
                "gelten; Geschäftsführer und Prokuristen werden durch das Handelsregister allgemein bekannt " +
                "gemacht.<br/><br/>");
        verwaltungParagraphList.add(verwaltungParagraph24);


        ContractParagraph verwaltungParagraph25 = new ContractParagraph();
        verwaltungParagraph25.setParagraphNumber("25");
        verwaltungParagraph25.setParagraphTitle("Sondervereinbarung");
        verwaltungParagraph25.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25.setSelectionGroups(false);
        verwaltungParagraph25.setClean(true);
        verwaltungParagraph25.setTemplate(true);
        verwaltungParagraph25.setParagraphContent("(1) Als [technisches Eintrittsdatum / Eintrittsdatum] gilt der [Eintrittsdatum][OPT-25-0][OPT-25-1].<br/>" +
                "|(2) Der Arbeitgeber gewährt zur Zeit als freiwillige Leistung [Anzahl Sonderurlaubstage] Arbeitstage (5-Tage-Woche) " +
                "Erholungsurlaub gemäß § 16a dieses Vertrages.<br/>" +
                "[OPT-25-1a]" +
                "[OPT-25-1b]" +
                "[OPT-25-2]" +
                "[OPT-25-2b]" +
                "[OPT-25-2c]" +
                "[OPT-25-3]" +
                "[OPT-25-4a]" +
                "[OPT-25-4b]" +
                "[OPT-25-5]" +
                "[OPT-25-6a]" +
                "[OPT-25-6b]" +
                "[OPT-25-6c]" +
                "[OPT-25-6d]" +
                "[OPT-25-7a]" +
                "[OPT-25-7b]" +
                "[OPT-25-8]" +
                "[OPT-25-9]" +
                "[OPT-25-10a]" +
                "[OPT-25-10b]" +
                "[OPT-25-11a]" +
                "[OPT-25-11b]");
        List<ContentField> verwaltungParagraph25ContentFields = new ArrayList<>();
        ContentField verwaltungParagraph25ContentField0 = new ContentField();
        verwaltungParagraph25ContentField0.setFieldName("technisches Eintrittsdatum / Eintrittsdatum");
        verwaltungParagraph25ContentField0.setFieldDescription("Art des Eintrittsdatums");
        verwaltungParagraph25ContentField0.setFieldType(FieldType.AUSWAHL);
        verwaltungParagraph25ContentFields.add(verwaltungParagraph25ContentField0);
        ContentField verwaltungParagraph25ContentField1 = new ContentField();
        verwaltungParagraph25ContentField1.setFieldName("Eintrittsdatum");
        verwaltungParagraph25ContentField1.setFieldDescription("Datum des Eitritts");
        verwaltungParagraph25ContentField1.setFieldType(FieldType.DATUM);
        verwaltungParagraph25ContentFields.add(verwaltungParagraph25ContentField1);
        ContentField verwaltungParagraph25ContentField2 = new ContentField();
        verwaltungParagraph25ContentField2.setFieldName("Anzahl Sonderurlaubstage");
        verwaltungParagraph25ContentField2.setFieldDescription("Anzahl der Sonderurlaubstage");
        verwaltungParagraph25ContentField2.setFieldType(FieldType.TEXT);
        verwaltungParagraph25ContentFields.add(verwaltungParagraph25ContentField2);
        verwaltungParagraph25.setContentFields(verwaltungParagraph25ContentFields);
        List<OptionalContent> verwaltungParagraph25OptionalContents = new ArrayList<>();
        OptionalContent verwaltungParagraph25OptionalContent1 = new OptionalContent();
        verwaltungParagraph25OptionalContent1.setTitle("Verweis auf § 1 beim Eintrittsdatum");
        verwaltungParagraph25OptionalContent1.setShortName("OPT-25-1");
        verwaltungParagraph25OptionalContent1.setContent(", bzw. siehe § 1");
        verwaltungParagraph25OptionalContent1.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent1);
        OptionalContent verwaltungParagraph25OptionalContent1a = new OptionalContent();
        verwaltungParagraph25OptionalContent1a.setTitle("Urlaub im Eintrittsjahr");
        verwaltungParagraph25OptionalContent1a.setShortName("OPT-25-1a");
        verwaltungParagraph25OptionalContent1a.setContent("|(n) Im Kalenderjahr des Eintritts gewährt der Arbeitgeber insgesamt [Urlaubstage Eintrittsjahr] " +
                "Werktage Erholungsurlaub (gesetzlicher Urlaub und freiwilliger Urlaub).<br/>");
        verwaltungParagraph25OptionalContent1a.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent1a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent1aFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent1Field1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent1Field1.setFieldName("Urlaubstage Eintrittsjahr");
        verwaltungParagraph25OptionalContent1Field1.setFieldDescription("gesetzlicher und freiwilliger Urlaub im Eintrittsjahr");
        verwaltungParagraph25OptionalContent1aFields.add(verwaltungParagraph25OptionalContent1Field1);
        verwaltungParagraph25OptionalContent1a.setOptionalContentFields(verwaltungParagraph25OptionalContent1aFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent1a);

        OptionalContent verwaltungParagraph25OptionalContent1b = new OptionalContent();
        verwaltungParagraph25OptionalContent1b.setTitle("Betriebsurlaub");
        verwaltungParagraph25OptionalContent1b.setShortName("OPT-25-1b");
        verwaltungParagraph25OptionalContent1b.setContent("|(n) Der Arbeitgeber ist berechtigt, bei betrieblichen Erfordernissen in angemessenem Umfang " +
                "Betriebsferien anzuordnen, die wie der sonstige Urlaub auf die dem Arbeitnehmer zustehenden " +
                "Urlaubstage anzurechnen sind.<br/>");
        verwaltungParagraph25OptionalContent1b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent1b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent1b);

        OptionalContent verwaltungParagraph25OptionalContent2 = new OptionalContent();
        verwaltungParagraph25OptionalContent2.setTitle("Sonderleistungen");
        verwaltungParagraph25OptionalContent2.setShortName("OPT-25-2");
        verwaltungParagraph25OptionalContent2.setContent("|(n) Der Arbeitnehmer erhält zusätzlich zu der Vergütung nach § 4 folgende Leistungszulagen / " +
                "Prämien:<br/>" +
                "    [Prämie/-n]<br/><br/>" +
                "Besteht das Arbeitsverhältnis bei Beginn des 13. Beschäftigungsmonats ungekündigt fort, " +
                "so zahlt der Arbeitgeber an den Arbeitnehmer einmalig eine Treueprämie in Höhe von" +
                "<b> [Treueprämie] EUR </b>brutto. Die Prämie dient ausschließlich der Honorierung der Betriebstreue. " +
                "Daher besteht kein, auch kein anteiliger Anspruch des Arbeitnehmers auf die Treueprämie, wenn das " +
                "Arbeitsverhältnis innerhalb der ersten zwölf Beschäftigungsmonate von einer der Parteien gekündigt wird; " +
                "auf den Anlass und den Grund der Kündigung kommt es hierbei nicht an. Auch ein Anspruch auf Schadensersatz " +
                "bei Nichtentstehung des Anspruchs infolge einer Kündigung besteht nicht. Eine Kündigung, die später durch die " +
                "Parteien oder ein Gericht für unwirksam erklärt wird, gilt nicht als Kündigung im Sinne dieser Klausel. " +
                "Die entstandene Treueprämie wird mit dem nächsten regulären Abrechnungslauf des Arbeitgebers gezahlt, " +
                "der auf die Vollendung des zwölften Beschäftigungsmonats folgt.<br/>");
        verwaltungParagraph25OptionalContent2.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent2Fields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent3Field1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent3Field1.setFieldName("Prämie/-n");
        verwaltungParagraph25OptionalContent3Field1.setFieldDescription("Auflistung aller Prämien");
        verwaltungParagraph25OptionalContent2Fields.add(verwaltungParagraph25OptionalContent3Field1);
        OptionalContentField verwaltungParagraph25OptionalContent3Field2 = new OptionalContentField();
        verwaltungParagraph25OptionalContent3Field2.setFieldName("Treueprämie");
        verwaltungParagraph25OptionalContent3Field2.setFieldDescription("Treueprämie im 13. Monat (in Euro mit Nachkommastellen)");
        verwaltungParagraph25OptionalContent2Fields.add(verwaltungParagraph25OptionalContent3Field2);
        verwaltungParagraph25OptionalContent2.setOptionalContentFields(verwaltungParagraph25OptionalContent2Fields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent2);
        OptionalContent verwaltungParagraph25OptionalContent2b = new OptionalContent();
        verwaltungParagraph25OptionalContent2b.setTitle("Lage der Arbeitszeit");
        verwaltungParagraph25OptionalContent2b.setShortName("OPT-25-2b");
        verwaltungParagraph25OptionalContent2b.setContent("|(n) Abweichend von § 7 Abs. 4 und 5 sowie § 8 gilt bis auf Weiteres unter dem Vorbehalt des jederzeitigen Widerrufs folgendes: " +
                "Die Lage der Arbeitszeit wird eigenverantwortlich vom Arbeitnehmer unter Beachtung der Grenzen des Arbeitszeitgesetzes bestimmt. " +
                "Ferner ist der Arbeitnehmer von der Teilnahme an der betrieblichen Zeiterfassung entbunden und gehalten, " +
                "diese selbst zu überwachen und zu dokumentieren. Der Arbeitgeber führt für den Arbeitnehmer kein Arbeitszeitkonto. " +
                "Der Widerruf dieser Ausnahmeregelung bedarf keines besonderen Grundes, das Direktionsrecht des Arbeitgebers wird durch diese Bestimmung nicht eingeschränkt. " +
                "Der Arbeitgeber wird diese Ausnahmeregelung unter anderem widerrufen, wenn der Arbeitnehmer sich im Zusammenhang mit der Bestimmung, " +
                "Überwachung oder Dokumentation seiner Arbeitszeit pflichtwidrig verhält, " +
                "eine gesetzliche oder behördliche Anordnung dies erfordert oder dies betrieblich erforderlich ist.<br/>");
        verwaltungParagraph25OptionalContent2b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent2b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent2b);

        OptionalContent verwaltungParagraph25OptionalContent2c = new OptionalContent();
        verwaltungParagraph25OptionalContent2c.setTitle("abweichende Kündigungsfrist");
        verwaltungParagraph25OptionalContent2c.setShortName("OPT-25-2c");
        verwaltungParagraph25OptionalContent2c.setContent("|(n) Die Kündigungsfrist beträgt abweichend von § 9 nach erfolgreich absolvierter Probezeit für beide Seiten 3 Monate zum Monatsende.<br/>");
        verwaltungParagraph25OptionalContent2c.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent2c.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent2c);

        OptionalContent verwaltungParagraph25OptionalContent3 = new OptionalContent();
        verwaltungParagraph25OptionalContent3.setTitle("erfolgsabhängige Prämie");
        verwaltungParagraph25OptionalContent3.setShortName("OPT-25-3");
        verwaltungParagraph25OptionalContent3.setContent("|(n) Der Arbeitnehmer erhält zusätzlich zu der Vergütung nach § 4 als weitere Vergütung bei Erreichen " +
                "der jeweiligen Voraussetzungen und/oder Ziele eine Prämie gemäß der gesonderten Anlage zu " +
                "diesem Vertrag.<br/>");
        verwaltungParagraph25OptionalContent3.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent3.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent3);

        OptionalContent verwaltungParagraph25OptionalContent4a = new OptionalContent();
        verwaltungParagraph25OptionalContent4a.setTitle("Dienstwagen mit Privatnutzung");
        verwaltungParagraph25OptionalContent4a.setShortName("OPT-25-4a");
        verwaltungParagraph25OptionalContent4a.setContent("|(n) Der Arbeitgeber stellt dem Arbeitnehmer einen Dienstwagen zur Verfügung, den der Arbeitnehmer " +
                "auch zu privaten Zwecken zu nutzen darf. Einzelheiten über die Gestellung sowie Art und Umfag der " +
                "Nutzung werden in einer gesonderten Nutzungsvereinbarung festgelegt.<br/>");
        verwaltungParagraph25OptionalContent4a.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent4a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent4a);

        OptionalContent verwaltungParagraph25OptionalContent4b = new OptionalContent();
        verwaltungParagraph25OptionalContent4b.setTitle("Dienstwagen ohne Privatnutzung");
        verwaltungParagraph25OptionalContent4b.setShortName("OPT-25-4b");
        verwaltungParagraph25OptionalContent4b.setContent("|(n) Der Arbeitgeber stellt dem Arbeitnehmer ausschließlich zu dienstlichen Zwecken einen " +
                "Dienstwagen zur Verfügung. Jegliche Privatnutzung hieran ist ausgeschlossen. Dies gilt auch für Wege zwischen Wohnung zur Arbeitsstätte. Einzelheiten über die Gestellung sowie die Nutzung " +
                "werden in einer gesonderten Nutzungsvereinbarung festgelegt.<br/>");
        verwaltungParagraph25OptionalContent4b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent4b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent4b);

        OptionalContent verwaltungParagraph25OptionalContent5 = new OptionalContent();
        verwaltungParagraph25OptionalContent5.setTitle("Vermögenswirksame Leistungen");
        verwaltungParagraph25OptionalContent5.setShortName("OPT-25-5");
        verwaltungParagraph25OptionalContent5.setContent("|(n) Das Unternehmen gewährt auf Basis der Freiwilligkeit im Sinne von § 5 dieses Vertrages nach " +
                "Vollendung des ersten Jahres Betriebszugehörigkeit nach Eintritt [Vermögenswirksame Leistungen] EUR vermögenswirksame " +
                "Leistungen.<br/>");
        verwaltungParagraph25OptionalContent5.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent5.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent5Fields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent5Field1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent5Field1.setFieldName("Vermögenswirksame Leistungen");
        verwaltungParagraph25OptionalContent5Field1.setFieldDescription("vermögenswirksame Leistungen");
        verwaltungParagraph25OptionalContent5Fields.add(verwaltungParagraph25OptionalContent5Field1);
        verwaltungParagraph25OptionalContent5.setOptionalContentFields(verwaltungParagraph25OptionalContent5Fields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent5);

        OptionalContent verwaltungParagraph25OptionalContent6a = new OptionalContent();
        verwaltungParagraph25OptionalContent6a.setTitle("Arbeitszeit/Schicht");
        verwaltungParagraph25OptionalContent6a.setShortName("OPT-25-6a");
        verwaltungParagraph25OptionalContent6a.setContent("|(n) Dem Arbeitnehmer ist bekannt, dass die Arbeit im Schicht- und Wechselschichtdienst im Zeitraum " +
                "von [frühestmöglicher Arbeits-/Schichtbeginn] Uhr bis [spätestmögliches Arbeits-/Schichtende] Uhr zu " +
                "leisten ist.<br/>");
        verwaltungParagraph25OptionalContent6a.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent6a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent6aFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent6aField1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent6aField1.setFieldName("frühestmöglicher Arbeits-/Schichtbeginn");
        verwaltungParagraph25OptionalContent6aField1.setFieldDescription("Angabe frühestmöglicher Arbeits-/Schichtbeginn als Uhrzeit");
        verwaltungParagraph25OptionalContent6aFields.add(verwaltungParagraph25OptionalContent6aField1);
        OptionalContentField verwaltungParagraph25OptionalContent6aField2 = new OptionalContentField();
        verwaltungParagraph25OptionalContent6aField2.setFieldName("spätestmögliches Arbeits-/Schichtende");
        verwaltungParagraph25OptionalContent6aField2.setFieldDescription("Angabe spätestmögliches Arbeits-/Schichtende als Uhrzeit");
        verwaltungParagraph25OptionalContent6aFields.add(verwaltungParagraph25OptionalContent6aField2);
        verwaltungParagraph25OptionalContent6a.setOptionalContentFields(verwaltungParagraph25OptionalContent6aFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent6a);

        OptionalContent verwaltungParagraph25OptionalContent6b = new OptionalContent();
        verwaltungParagraph25OptionalContent6b.setTitle("Arbeitszeit Regel");
        verwaltungParagraph25OptionalContent6b.setShortName("OPT-25-6b");
        verwaltungParagraph25OptionalContent6b.setContent("|(n) In der Regel beträgt die Arbeitszeit täglich ([Wochentage]) [Wochenarbeitsstunden] Stunden. Die " +
                "Monatsarbeitszeit beträgt mindestens [Mindestmonatsarbeitsstundenanzahl] Stunden.<br/>");
        verwaltungParagraph25OptionalContent6b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent6b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent6bFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent6bField1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent6bField1.setFieldName("Wochentage");
        verwaltungParagraph25OptionalContent6bField1.setFieldDescription("Anzahl der Arbeitstage in der Woche");
        verwaltungParagraph25OptionalContent6bFields.add(verwaltungParagraph25OptionalContent6bField1);
        OptionalContentField verwaltungParagraph25OptionalContent6bField2 = new OptionalContentField();
        verwaltungParagraph25OptionalContent6bField2.setFieldName("Wochenarbeitsstunden");
        verwaltungParagraph25OptionalContent6bField2.setFieldDescription("Anzahl der Arbeitsstunden in der Woche");
        verwaltungParagraph25OptionalContent6bFields.add(verwaltungParagraph25OptionalContent6bField2);
        OptionalContentField verwaltungParagraph25OptionalContent6bField3 = new OptionalContentField();
        verwaltungParagraph25OptionalContent6bField3.setFieldName("Mindestmonatsarbeitsstundenanzahl");
        verwaltungParagraph25OptionalContent6bField3.setFieldDescription("Anzahl der Arbeitsstunden im Monat die mindestens geleistet werden müssen");
        verwaltungParagraph25OptionalContent6bFields.add(verwaltungParagraph25OptionalContent6bField3);
        verwaltungParagraph25OptionalContent6b.setOptionalContentFields(verwaltungParagraph25OptionalContent6bFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent6b);

        OptionalContent verwaltungParagraph25OptionalContent6c = new OptionalContent();
        verwaltungParagraph25OptionalContent6c.setTitle("Vertrauensarbeitszeit");
        verwaltungParagraph25OptionalContent6c.setShortName("OPT-25-6c");
        verwaltungParagraph25OptionalContent6c.setContent("|(n) Abweichend von § 7 Abs. 4 und 5 sowie § 8 gilt bis auf Weiteres unter dem Vorbehalt des " +
                "jederzeitigen Widerrufs folgendes: Die Lage der Arbeitszeit wird eigenverantwortlich vom " +
                "Arbeitnehmer unter Beachtung der Grenzen des Arbeitszeitgesetzes bestimmt. Ferner ist der " +
                "Arbeitnehmer von der Teilnahme an der betrieblichen Zeiterfassung entbunden und gehalten, diese " +
                "selbst zu überwachen und zu dokumentieren. Der Arbeitgeber führt für den Arbeitnehmer kein " +
                "Arbeitszeitkonto. Der Widerruf dieser Ausnahmeregelung bedarf keines besonderen Grundes, das " +
                "Direktionsrecht des Arbeitgebers wird durch diese Bestimmung nicht eingeschränkt. Der Arbeitgeber " +
                "wird diese Ausnahmeregelung unter anderem widerrufen, wenn der Arbeitnehmer sich im " +
                "Zusammenhang mit der Bestimmung, Überwachung oder Dokumentation seiner Arbeitszeit " +
                "pflichtwidrig verhält, eine gesetzliche oder behördliche Anordnung dies erfordert oder dies betrieblich " +
                "erforderlich ist.<br/>");
        verwaltungParagraph25OptionalContent6c.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent6c.setVersionTemplateNames(VersionTemplateName.MITARBEITER_INTERNATIONAL.name()+ VersionTemplateName.MITARBEITER_DEUTSCHLAND.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent6c);

        OptionalContent verwaltungParagraph25OptionalContent6d = new OptionalContent();
        verwaltungParagraph25OptionalContent6d.setTitle("Arbeitszeit Werkstudent");
        verwaltungParagraph25OptionalContent6d.setShortName("OPT-25-6d");
        verwaltungParagraph25OptionalContent6d.setContent("|(n) Um den Status als Werkstudent zu erfüllen, wird vereinbart, dass der Werkstudent während der " +
                "Studienzeit maximal 20 Stunden pro Woche arbeitet. Diese Arbeitszeit kann auf bis zu 40 Stunden " +
                "pro Woche in den Semesterferien erhöht werden (hierzu Bedarf es einer Bescheinigung über die " +
                "Dauer der Semsterferien).<br/>");
        verwaltungParagraph25OptionalContent6d.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent6d.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent6d);

        OptionalContent verwaltungParagraph25OptionalContent7a = new OptionalContent();
        verwaltungParagraph25OptionalContent7a.setTitle("Kündigungsfrist nach Probezeit");
        verwaltungParagraph25OptionalContent7a.setShortName("OPT-25-7a");
        verwaltungParagraph25OptionalContent7a.setContent("|(n) Die Kündigungsfrist beträgt abweichend von § 9 nach erfolgreich absolvierter Probezeit für beide " +
                "Seiten [Kündigungsfrist] Monate zum Monatsende.<br/>");
        verwaltungParagraph25OptionalContent7a.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent7a.setVersionTemplateNames(VersionTemplateName.MITARBEITER_INTERNATIONAL.name()+VersionTemplateName.MITARBEITER_DEUTSCHLAND.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent7aFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent7aField1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent7aField1.setFieldName("Kündigungsfrist");
        verwaltungParagraph25OptionalContent7aField1.setFieldDescription("Angabe Kündigungsfrist in Monaten");
        verwaltungParagraph25OptionalContent7aFields.add(verwaltungParagraph25OptionalContent7aField1);
        verwaltungParagraph25OptionalContent7a.setOptionalContentFields(verwaltungParagraph25OptionalContent7aFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent7a);

        OptionalContent verwaltungParagraph25OptionalContent7b = new OptionalContent();
        verwaltungParagraph25OptionalContent7b.setTitle("Kündigungsfrist ab Beginn");
        verwaltungParagraph25OptionalContent7b.setShortName("OPT-25-7b");
        verwaltungParagraph25OptionalContent7b.setContent("|(n) Die Kündigungsfrist beträgt abweichend von § 9 für beide Seiten [Kündigungsfrist] Monate zum " +
                "Monatsende.<br/>");
        verwaltungParagraph25OptionalContent7b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent7b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent7bFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent7bField1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent7bField1.setFieldName("Kündigungsfrist");
        verwaltungParagraph25OptionalContent7bField1.setFieldDescription("Angabe Kündigungsfrist in Monaten");
        verwaltungParagraph25OptionalContent7bFields.add(verwaltungParagraph25OptionalContent7bField1);
        verwaltungParagraph25OptionalContent7b.setOptionalContentFields(verwaltungParagraph25OptionalContent7bFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent7b);

        OptionalContent verwaltungParagraph25OptionalContent8 = new OptionalContent();
        verwaltungParagraph25OptionalContent8.setTitle("Handynutzung");
        verwaltungParagraph25OptionalContent8.setShortName("OPT-25-8");
        verwaltungParagraph25OptionalContent8.setContent("|(n) Während der Arbeitszeit ist die Nutzung von privaten Handys zu privaten Zwecken untersagt.<br/>");
        verwaltungParagraph25OptionalContent8.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent8.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent8);

        OptionalContent verwaltungParagraph25OptionalContent9 = new OptionalContent();
        verwaltungParagraph25OptionalContent9.setTitle("Leitender Angestellter");
        verwaltungParagraph25OptionalContent9.setShortName("OPT-25-9");
        verwaltungParagraph25OptionalContent9.setContent("|(n) Der Arbeitnehmer ist nach übereinstimmender Auffassung der Vertragspartner als leitender " +
                "Angestellter im Sinne des § 5 Abs. 3 BetrVG einzustufen.<br/>");
        verwaltungParagraph25OptionalContent9.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent9.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent9);

        OptionalContent verwaltungParagraph25OptionalContent10a = new OptionalContent();
        verwaltungParagraph25OptionalContent10a.setTitle("im Falle einer Ablösung eines bestehenden Vertrages (gleicher Arbeitgeber):");
        verwaltungParagraph25OptionalContent10a.setShortName("OPT-25-10a");
        verwaltungParagraph25OptionalContent10a.setContent("|(n) Durch diesen Vertrag werden alle bisherigen mündlichen und schriftlichen Vereinbarungen " +
                "einschließlich etwaiger betrieblicher Übungen und nachwirkender Betriebsvereinbarungen insgesamt " +
                "abgelöst und damit gegenstandslos; dies gilt nicht für die unmittelbar und zwingend geltenden" +
                "Betriebsvereinbarungen und gültige Regelungsabreden. Die Parteien wollen die Bedingungen des " +
                "Arbeitsverhältnisses insgesamt neu festlegen.<br/>");
        verwaltungParagraph25OptionalContent10a.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent10a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent10a);

        OptionalContent verwaltungParagraph25OptionalContent10b = new OptionalContent();
        verwaltungParagraph25OptionalContent10b.setTitle("im Falle einer Übernahme von anderem Arbeitgeber aus der Gruppe:");
        verwaltungParagraph25OptionalContent10b.setShortName("OPT-25-10b");
        verwaltungParagraph25OptionalContent10b.setContent("|(n) Mit Abschluss dieses Vertrages heben die Parteien zugleich das bisher bestehende " +
                "Arbeitsverhältnis zwischen dem Arbeitnehmer und der [bisheriger Arbeitgeber] zum Stichtag des " +
                "Beginn des Arbeitsverhältnisses nach diesem Vertrag auf. Der Arbeitgeber handelt insoweit zugleich " +
                "in Vollmacht für den bisherigen Arbeitgeber. Die Parteien stellen klar, dass sich das Arbeitsverhältnis " +
                "ausschließlich nach den Bestimmungen dieses Vertrages richtet. Ein Besitzstand aus dem " +
                "bisherigen Arbeitsverhältnis wird nur und nur insoweit anerkannt, wenn und soweit dies in diesem " +
                "Vertrag ausdrücklich zugestanden wird.<br/>");
        verwaltungParagraph25OptionalContent10b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent10b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent10bFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent10bField1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent10bField1.setFieldName("bisheriger Arbeitgeber");
        verwaltungParagraph25OptionalContent10bField1.setFieldDescription("Angabe des bisherigen Arbeitgebers");
        verwaltungParagraph25OptionalContent10bFields.add(verwaltungParagraph25OptionalContent10bField1);
        verwaltungParagraph25OptionalContent10b.setOptionalContentFields(verwaltungParagraph25OptionalContent10bFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent10b);

        OptionalContent verwaltungParagraph25OptionalContent11a = new OptionalContent();
        verwaltungParagraph25OptionalContent11a.setTitle("Anlagen allgemein");
        verwaltungParagraph25OptionalContent11a.setShortName("OPT-25-11a");
        verwaltungParagraph25OptionalContent11a.setContent("|(n) Die Anlage zum Arbeitsvertrag [Bezeichnung Anlage] ist fester Bestandteil dieses Vertrages.<br/>");
        verwaltungParagraph25OptionalContent11a.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent11a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> verwaltungParagraph25OptionalContent11aFields = new ArrayList<>();
        OptionalContentField verwaltungParagraph25OptionalContent11aField1 = new OptionalContentField();
        verwaltungParagraph25OptionalContent11aField1.setFieldName("Bezeichnung Anlage");
        verwaltungParagraph25OptionalContent11aField1.setFieldDescription("genaue Bezeichnung der Anlage");
        verwaltungParagraph25OptionalContent11aFields.add(verwaltungParagraph25OptionalContent11aField1);
        verwaltungParagraph25OptionalContent11a.setOptionalContentFields(verwaltungParagraph25OptionalContent11aFields);
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent11a);

        OptionalContent verwaltungParagraph25OptionalContent11b = new OptionalContent();
        verwaltungParagraph25OptionalContent11b.setTitle("Anlage gesonderte Vergütungsvereinbarung");
        verwaltungParagraph25OptionalContent11b.setShortName("OPT-25-11b");
        verwaltungParagraph25OptionalContent11b.setContent("|(n) Die gesondert abgeschlossene Vergütungsvereinbarung ist fester Bestandteil dieses Vertrages " +
                "und ergänzt dessen Bestimmungen.<br/>");
        verwaltungParagraph25OptionalContent11b.setContractVersionName(ContractVersionName.VERWALTUNG);
        verwaltungParagraph25OptionalContent11b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        verwaltungParagraph25OptionalContents.add(verwaltungParagraph25OptionalContent11b);
        verwaltungParagraph25.setOptionalContents(verwaltungParagraph25OptionalContents);
        verwaltungParagraphList.add(verwaltungParagraph25);
        return verwaltungParagraphList;
    }

    private List<ContractParagraph> createKasseTemplates() {
        List<ContractParagraph> kasseParagraphList = new ArrayList<>();
        ContractParagraph kasseParagraph1 = new ContractParagraph();
        kasseParagraph1.setParagraphTitle("Tätigkeit, Befristung, Probezeit, Bedingungen");
        kasseParagraph1.setParagraphNumber("1");
        kasseParagraph1.setClean(true);
        kasseParagraph1.setTemplate(true);
        kasseParagraph1.setParagraphContent("(1) Die Einstellung als<b> [Tätigkeitsbezeichnung] </b>erfolgt ab<b> [Eintrittsdatum]</b>" +
                "[OPT-1-1]" +
                "[OPT-1-2]" +
                "[OPT-1-3]" +
                "[OPT-1-6].<br/><br/>" +
                "|(2) Die ersten [Probezeit] Monate gelten als Probezeit. Während dieser Probezeit kann das Arbeitsverhältnis beidseitig mit der besonderen Frist des § 9 Abs. 1 gekündigt werden.<br/>" +
                "[OPT-1-4]" +
                "[OPT-1-5]");
        kasseParagraph1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1.setSelectionGroups(true);
        List<ContentField> kasseParagraph1ContentFields = new ArrayList<>();
        ContentField kasseParagraph1ContentField1 = new ContentField();
        kasseParagraph1ContentField1.setFieldName("Tätigkeitsbezeichnung");
        kasseParagraph1ContentField1.setFieldDescription("Bezeichnung der Tätigkeit");
        kasseParagraph1ContentField1.setFieldType(FieldType.TEXT);
        kasseParagraph1ContentFields.add(kasseParagraph1ContentField1);
        ContentField kasseParagraph1ContentField2 = new ContentField();
        kasseParagraph1ContentField2.setFieldName("Eintrittsdatum");
        kasseParagraph1ContentField2.setFieldDescription("Datum des Eitritts");
        kasseParagraph1ContentField2.setFieldType(FieldType.DATUM);
        kasseParagraph1ContentFields.add(kasseParagraph1ContentField2);
        ContentField kasseParagraph1ContentField3 = new ContentField();
        kasseParagraph1ContentField3.setFieldName("Probezeit");
        kasseParagraph1ContentField3.setFieldDescription("in Monaten, maximal 6");
        kasseParagraph1ContentField3.setFieldType(FieldType.TEXT);
        kasseParagraph1ContentFields.add(kasseParagraph1ContentField3);
        kasseParagraph1.setContentFields(kasseParagraph1ContentFields);
        List<OptionalContent> kasseParagraph1OptionalContents = new ArrayList<>();
        OptionalContent kasseParagraph1OptionalContent1 = new OptionalContent();
        kasseParagraph1OptionalContent1.setTitle("Aufschiebende Bedingung Betriebsratszustimmung");
        kasseParagraph1OptionalContent1.setShortName("OPT-1-1");
        kasseParagraph1OptionalContent1.setContent(" unter der aufschiebenden Bedingung, dass der für den Betrieb zuständige Betriebsrat der Einstellung " +
                "des Arbeitnehmers zustimmt; der Arbeitnehmer wurde insoweit darauf hingewiesen, dass der " +
                "Betriebsrat diese Zustimmung noch nicht erteilt hat. Der Arbeitgeber ist berechtigt, aber nicht " +
                "verpflichtet, gegen eine etwaige Zustimmungsverweigerung des Betriebsrats gerichtliche Schritte " +
                "einzuleiten. Der Arbeitgeber ist verpflichtet, dem Arbeitnehmer unverzüglich mitzuteilen, dass ein " +
                "Arbeitsverhältnis nicht zustande kommt, wenn der Betriebsrat die Zustimmung verweigert und das " +
                "Arbeitsgericht die Zustimmung nicht ersetzt oder der Arbeitgeber die Ersetzung nicht beantragt");
        kasseParagraph1OptionalContent1.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1OptionalContent1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1OptionalContent1.setSelectionGroup(1);
        kasseParagraph1OptionalContents.add(kasseParagraph1OptionalContent1);
        OptionalContent kasseParagraph1OptionalContent2 = new OptionalContent();
        kasseParagraph1OptionalContent2.setTitle("Aufschiebende Bedingung Ausländer");
        kasseParagraph1OptionalContent2.setShortName("OPT-1-2");
        kasseParagraph1OptionalContent2.setContent(" unter der aufschiebenden Bedingung, dass der Arbeitnehmer vor diesem Tag einen gültigen " +
                "Aufenthaltstitel, der ihn zur Erwerbstätigkeit berechtigt, vorlegt. Legt der Arbeitnehmer einen solchen " +
                "Aufenthaltstitel erst am oder nach diesem Tag, aber spätestens innerhalb von 3 Monaten vor, beginnt " +
                "das Arbeitsverhältnis an dem Tag nach der Vorlage des Aufenthaltstitels; ist dies ein Sonntag, dann " +
                "am folgenden Montag. Legt der Arbeitnehmer den Aufenthaltstitel erst nach mehr als 3 Monaten vor, " +
                "kommt kein Arbeitsverhältnis zustande");
        kasseParagraph1OptionalContent2.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1OptionalContent2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1OptionalContent2.setSelectionGroup(1);
        kasseParagraph1OptionalContents.add(kasseParagraph1OptionalContent2);
        OptionalContent kasseParagraph1OptionalContent3 = new OptionalContent();
        kasseParagraph1OptionalContent3.setTitle("Befristung");
        kasseParagraph1OptionalContent3.setShortName("OPT-1-3");
        kasseParagraph1OptionalContent3.setContent(" befristet bis zum<b> [Befristungsdatum]</b>. Mit Fristende<b> endet das Arbeitsverhältnis, ohne dass es " +
                "einer Kündigung bedarf</b>. Der Arbeitnehmer ist verpflichtet, sich spätestens drei Monate vor " +
                "Beendigung des Arbeitsvertrages bei der zuständigen Agentur für Arbeit arbeitsuchend zu melden. " +
                "Eine verspätete Meldung kann zu einer Reduzierung des Arbeitslosengeldanspruches führen. Auf " +
                "Grund verspäteter Meldungen können keine Schadensersatzforderungen gegen den Arbeitgeber " +
                "geltend gemacht werden");
        kasseParagraph1OptionalContent3.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1OptionalContent3.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1OptionalContent3.setSelectionGroup(1);
        List<OptionalContentField> kasseParagraph1OptionalContent3Fields = new ArrayList<>();
        OptionalContentField kasseParagraph1OptionalContent3Field1 = new OptionalContentField();
        kasseParagraph1OptionalContent3Field1.setFieldName("Befristungsdatum");
        kasseParagraph1OptionalContent3Field1.setFieldDescription("Enddatum des befristeten Vertrags");
        kasseParagraph1OptionalContent3Fields.add(kasseParagraph1OptionalContent3Field1);
        kasseParagraph1OptionalContent3.setOptionalContentFields(kasseParagraph1OptionalContent3Fields);
        kasseParagraph1OptionalContents.add(kasseParagraph1OptionalContent3);
        OptionalContent kasseParagraph1OptionalContent6 = new OptionalContent();
        kasseParagraph1OptionalContent6.setTitle("Aufschiebende Bedingungen Krieg, Katastrophen");
        kasseParagraph1OptionalContent6.setShortName("OPT-1-6");
        kasseParagraph1OptionalContent6.setContent(" unter den<b> aufschiebenden Bedingungen</b>, <br/><br/>" +
                "a) dass der Arbeitnehmer vor diesem Tag einen gültigen Aufenthaltstitel, der ihn zur Erwerbstätigkeit berechtigt, vorlegt. Legt der Arbeitnehmer einen solchen Aufenthaltstitel erst am oder nach diesem Tag, aber spätestens innerhalb von 3 Monaten vor, beginnt das Arbeitsverhältnis an dem Tag nach der Vorlage des Aufenthaltstitels; ist dies ein Sonntag, dann am folgenden Montag. \n" +
                "und <br/><br/>" +
                "b) dass der Arbeitnehmer seine Arbeitsleistung mit Vorlage des Aufenthaltstitels, spätestens aber innerhalb eines Monats nach diesem Tag vor Ort in der Betriebsstätte (§ 2 Abs. 1) tatsächlich anbietet. Eine Arbeitsunfähigkeit insbesondere aufgrund einer Erkrankung steht dem Angebot der Arbeitsleistung gleich, nicht jedoch eine verspätete Einreise nach Deutschland, gleich ob diese vom Arbeitnehmer verschuldet ist oder nicht. Dies gilt auch bei Reiseerschwerungen infolge von Krisen, beispielsweise aufgrund von Krieg oder Naturkatastrophen. " +
                "<br/><br/><br/>" +
                "Bietet der Arbeitnehmer seine Arbeitsleistung nicht oder nicht innerhalb eines Monats in der Betriebsstätte an oder legt er den Aufenthaltstitel nicht oder erst nach mehr als 3 Monaten vor, kommt kein Arbeitsverhältnis zustande");
        kasseParagraph1OptionalContent6.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1OptionalContent6.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1OptionalContent6.setSelectionGroup(1);
        kasseParagraph1OptionalContents.add(kasseParagraph1OptionalContent6);
        OptionalContent kasseParagraph1OptionalContent4 = new OptionalContent();
        kasseParagraph1OptionalContent4.setTitle("Auflösende Bedingung Ausländer");
        kasseParagraph1OptionalContent4.setShortName("OPT-1-4");
        kasseParagraph1OptionalContent4.setContent("|(3) Das Arbeitsverhältnis steht<b> unter der auflösenden Bedingung</b>, dass der Arbeitnehmer sich in " +
                "Deutschland nicht nur aufhalten, sondern auch einer Erwerbstätigkeit nachgehen darf, mit der Folge, " +
                "dass das<b> Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf</b>, wenn der zur " +
                "Erwerbstätigkeit berechtigende Titel aufgehoben wird oder die darin genannte Frist abläuft, ohne dass " +
                "eine Verlängerung erteilt und diese der Arbeitgeberin spätestens am Fristablauftag nachgewiesen wird.<br/>");
        kasseParagraph1OptionalContent4.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1OptionalContent4.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1OptionalContent4.setSelectionGroup(2);
        kasseParagraph1OptionalContents.add(kasseParagraph1OptionalContent4);
        OptionalContent kasseParagraph1OptionalContent5 = new OptionalContent();
        kasseParagraph1OptionalContent5.setTitle("Auflösende Bedingung Werkstudent");
        kasseParagraph1OptionalContent5.setShortName("OPT-1-5");
        kasseParagraph1OptionalContent5.setContent("|(3) Das Arbeitsverhältnis steht<b> unter der auflösenden Bedingung</b>, dass der Arbeitnehmer als " +
                "Vollzeitstudent immatrikuliert ist und seine letzte Prüfungsleistung noch nicht erbracht hat, mit der Folge, " +
                "dass das<b> Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf</b>, wenn das " +
                "Vollzeitstudium beendet ist. Der Arbeitnehmer verpflichtet sich, den Arbeitgeber sofort zu informieren, " +
                "wenn sich sein beruflicher Status ändert.<br/>");
        kasseParagraph1OptionalContent5.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph1OptionalContent5.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph1OptionalContent5.setSelectionGroup(2);
        kasseParagraph1OptionalContents.add(kasseParagraph1OptionalContent5);
        kasseParagraph1.setOptionalContents(kasseParagraph1OptionalContents);
        kasseParagraphList.add(kasseParagraph1);


        ContractParagraph kasseParagraph2 = new ContractParagraph();
        kasseParagraph2.setParagraphNumber("2");
        kasseParagraph2.setParagraphTitle("Arbeitsort, alternative Aufgaben");
        kasseParagraph2.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph2.setSelectionGroups(false);
        kasseParagraph2.setClean(true);
        kasseParagraph2.setTemplate(true);
        kasseParagraph2.setParagraphContent("(1) Der Arbeitsort ist die Betriebsstätte [Arbeitsort].<br/><br/>" +
                "|(2) Der Arbeitgeber ist berechtigt, die vertraglich geschuldete Tätigkeit durch eine Stellenbeschreibung zu konkretisieren.<br/>" +
                "|(3) Der Arbeitnehmer erklärt sich ferner bereit, vorübergehend für die Dauer von [1 Monat / 2 Monaten] " +
                "aushilfsweise andere zumutbare Tätigkeiten, " +
                "auch an anderen Orten auszuüben. Dies gilt auch nach langjähriger unveränderter Tätigkeit.");
        List<ContentField> kasseParagraph2ContentFields = new ArrayList<>();
        ContentField kasseParagraph2ContentField1 = new ContentField();
        kasseParagraph2ContentField1.setFieldName("Arbeitsort");
        kasseParagraph2ContentField1.setFieldDescription("Betriebsstätte");
        kasseParagraph2ContentField1.setFieldType(FieldType.TEXT);
        kasseParagraph2ContentFields.add(kasseParagraph2ContentField1);
        ContentField kasseParagraph2ContentField2 = new ContentField();
        kasseParagraph2ContentField2.setFieldName("1 Monat / 2 Monaten");
        kasseParagraph2ContentField2.setFieldDescription("Dauer der aushilfsweise zumutbaren Tätigkeit");
        kasseParagraph2ContentField2.setFieldType(FieldType.AUSWAHL);
        kasseParagraph2ContentFields.add(kasseParagraph2ContentField2);
        kasseParagraph2.setContentFields(kasseParagraph2ContentFields);
        kasseParagraphList.add(kasseParagraph2);



        ContractParagraph kasseParagraph3 = new ContractParagraph();
        kasseParagraph3.setParagraphNumber("3");
        kasseParagraph3.setParagraphTitle("Vertragsbestandteile");
        kasseParagraph3.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph3.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph3.setSelectionGroups(false);
        kasseParagraph3.setClean(true);
        kasseParagraph3.setTemplate(true);
        kasseParagraph3.setParagraphContent("(1) Bestandteil des Arbeitsvertrages ist der vom Arbeitnehmer ausgefüllte Personalbogen. Mit der " +
                "Unterschrift unter diesen Vertrag erklärt der Arbeitnehmer, dass alle Angaben zu seiner Person " +
                "vollständig und richtig sind. Wissentlich falsche Angaben können die Anfechtung bzw. die fristlose " +
                "Kündigung des Arbeitsverhältnisses begründen.<br/><br/>" +
                "|(2) Im Übrigen gelten die Allgemeinen Arbeitsbedingungen, die Betriebsordnung und Anweisungen in " +
                "ihren jeweils gültigen Fassungen.");
        kasseParagraphList.add(kasseParagraph3);


        ContractParagraph kasseParagraph4 = new ContractParagraph();
        kasseParagraph4.setParagraphNumber("4");
        kasseParagraph4.setParagraphTitle("Vergütung");
        kasseParagraph4.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph4.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph4.setSelectionGroups(false);
        kasseParagraph4.setClean(true);
        kasseParagraph4.setTemplate(true);
        kasseParagraph4.setParagraphContent("(1) Die Vergütung beträgt<b> [Gehalt] EUR </b>brutto/Monat. " +
                "Die Zahlung des Nettobetrages erfolgt durch Überweisung auf das vom Arbeitnehmer hierfür zu " +
                "benennende Konto bei einer Bank.<br/><br/>" +
                "[OPT-4-1]" +
                "|(2) Mit der in diesem Vertrag vereinbarten sowie der aufgrund etwaiger weiterer Vereinbarungen " +
                "geleisteten Vergütung sind alle Tätigkeiten des Arbeitnehmers im Rahmen des Arbeitsverhältnisses " +
                "abgegolten. Dies gilt auch für eine etwaige Arbeit an Samstagen sowie Sonn- und Feiertagen sowie " +
                "die Leistung von Schichtarbeit.<br/>" +
                "|(3) Ebenso mit dieser Vergütung abgegolten sind Überstunden im Umfang von 10% der regelmäßigen " +
                "Arbeitszeit, soweit durch die Gesamtvergütung sichergestellt ist, dass jede tatsächlich geleistete " +
                "Arbeitsstunde mindestens mit dem jeweiligen gesetzlichen Mindestlohn vergütet wird. Von der " +
                "vorgenannten Abgeltungsregelung betroffen sind die Überstunden, die nach Ablauf eines " +
                "Kalenderjahres nicht durch Freizeit ausgeglichen wurden und als Habensaldo auf dem " +
                "Arbeitszeitkonto gemäß § 8 Abs. 4 verbleiben. Dies ist bei der Abrechnung des Arbeitszeitkontos zu " +
                "berücksichtigen.");
        List<ContentField> kasseParagraph4ContentFields = new ArrayList<>();
        ContentField kasseParagraph4ContentField1 = new ContentField();
        kasseParagraph4ContentField1.setFieldName("Gehalt");
        kasseParagraph4ContentField1.setFieldDescription("Vergütung in EUR brutto/Monat mit Kommastellen");
        kasseParagraph4ContentField1.setFieldType(FieldType.TEXT);
        kasseParagraph4ContentFields.add(kasseParagraph4ContentField1);
        kasseParagraph4.setContentFields(kasseParagraph4ContentFields);
        List<OptionalContent> kasseParagraph4OptionalContents = new ArrayList<>();
        OptionalContent kasseParagraph4OptionalContent1 = new OptionalContent();
        kasseParagraph4OptionalContent1.setTitle("Mankogeld");
        kasseParagraph4OptionalContent1.setShortName("OPT-4-1");
        kasseParagraph4OptionalContent1.setContent("Der Arbeitnehmer erhält darüber hinaus eine Fehlgeldprämie " +
                "(Mankogeld) in Höhe von [Betrag],00 EUR brutto/Monat, wenn und solange er mit der Entgegennahme " +
                "und/oder Herausgabe von Bargeld betraut ist. Dieses Mankogeld stellt eine Kompensation für die " +
                "verschuldensunabhängige Haftung nach § 22 dar.<br/><br/>");
        kasseParagraph4OptionalContent1.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph4OptionalContent1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph4OptionalContent1Fields = new ArrayList<>();
        OptionalContentField kasseParagraph4OptionalContent1Field1 = new OptionalContentField();
        kasseParagraph4OptionalContent1Field1.setFieldName("Betrag");
        kasseParagraph4OptionalContent1Field1.setFieldDescription("Betrag Mankogeld");
        kasseParagraph4OptionalContent1Fields.add(kasseParagraph4OptionalContent1Field1);
        kasseParagraph4OptionalContent1.setOptionalContentFields(kasseParagraph4OptionalContent1Fields);
        kasseParagraph4OptionalContents.add(kasseParagraph4OptionalContent1);
        kasseParagraph4.setOptionalContents(kasseParagraph4OptionalContents);
        kasseParagraphList.add(kasseParagraph4);


        ContractParagraph kasseParagraph5 = new ContractParagraph();
        kasseParagraph5.setParagraphNumber("5");
        kasseParagraph5.setParagraphTitle("Freiwillige Leistungen");
        kasseParagraph5.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph5.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph5.setSelectionGroups(false);
        kasseParagraph5.setClean(true);
        kasseParagraph5.setTemplate(true);
        kasseParagraph5.setParagraphContent("(1) Sämtliche Leistungen, die vom Arbeitgeber erbracht werden, ohne dass diese " +
                "in diesem Vertrag oder einer sonstigen Vereinbarung verbindlich vereinbart worden sind, " +
                "sind freiwillige Leistungen des Arbeitgebers, auf die ein Anspruch nicht besteht und aus " +
                "denen auch bei wiederholter Zahlung eine betriebliche Übung nicht abgeleitet werden kann. " +
                "Dies gilt insbesondere für etwaige Gratifikationen und sonstige Sonderleistungen ohne " +
                "Vergütungscharakter.<br/><br/>" +
                "|(2) Sollte das Unternehmen freiwillige Leistungen erbringen, sind Mitarbeiter ausgeschlossen während " +
                "der Elternzeit, des freiwilligen Wehr- oder Ersatzdienstes, der Teilnahme an Wehrübungen und " +
                "in allen Fällen, in denen das Anstellungsverhältnis ruht.<br/>" +
                "|(3) Freiwillige Leistungen werden auch bei Unterbrechungen des Arbeitsverhältnisses von " +
                "mehr als sechs Wochen innerhalb des Leistungsszeitraums, während derer kein Anspruch auf " +
                "Entgeltfortzahlung besteht, entsprechend der Dauer der Unterbrechung gekürzt.");
        kasseParagraphList.add(kasseParagraph5);

        ContractParagraph kasseParagraph6 = new ContractParagraph();
        kasseParagraph6.setParagraphNumber("6");
        kasseParagraph6.setParagraphTitle("Kollektivvereinbarungen (Betriebsvereinbarungen, Tarifverträge)");
        kasseParagraph6.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph6.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph6.setSelectionGroups(false);
        kasseParagraph6.setClean(true);
        kasseParagraph6.setTemplate(true);
        kasseParagraph6.setParagraphContent("(1) Im Betrieb geltende Kollektivvereinbarungen gehen den " +
                "Bestimmungen dieses Arbeitsvertrages in jedem Fall vor. Dies gilt zunächst für im " +
                "Betrieb derzeit bestehende und/oder zukünftig geschlossene Betriebsvereinbarungen " +
                "und Regelungsabreden. Die Parteien sind sich darüber einig, dass die jeweils gültigen " +
                "einschlägigen Betriebsvereinbarungen sowie die getroffenen Regelungsabreden Anwendung " +
                "finden und für die Dauer ihrer Geltung den Regelungen in diesem Vertrag auch dann " +
                "vorgehen, wenn die vertragliche Regelung im Einzelfall günstiger ist.<br/><br/>" +
                "|(2) Derzeit findet kein Tarifvertrag auf das Arbeitsverhältnis Anwendung. " +
                "Falls zukünftig ein Tarifvertrag zwingend gelten sollte, treten an die Stelle " +
                "der entsprechenden Regelungen dieses Vertrages und der Betriebsvereinbarungen bzw. " +
                "Regelungsabreden oder Gesamtzusagen ausschließlich die tariflichen Regelungen. Der Mitarbeiter " +
                "kann sich auf etwa günstigere Regelungen aus diesem Vertrag, aus " +
                "Betriebsvereinbarungen oder Regelungsabreden während der Zeit der Tarifbindung " +
                "nicht berufen. Alle freiwilligen Leistungen werden für den Zeitraum der Tarifbindung " +
                "außer Kraft gesetzt, es sei denn, diesbezüglich wird eine andere Festlegung getroffen. " +
                "Dies gilt insbesondere für den Fall der zwingenden Geltung eines Lohn- bzw. " +
                "Gehaltstarifvertrages; für den Zeitraum einer solchen Tarifbindung des Arbeitgebers " +
                "(Dauer einer Allgemeinverbindlicherklärung bzw. Laufzeit des verbindlichen Tarifvertrages) " +
                "bemisst sich die Höhe der Vergütung ausschließlich nach den tariflichen Regelungen. " +
                "Das Günstigkeitsprinzip ist insoweit ausgeschlossen.<br/>" +
                "|(3) Im Falle der Anwendung eines Tarifvertrages müssen die tariflichen " +
                "Mindestleistungen innerhalb eines Kalenderjahres erbracht sein. " +
                "Mindervergütungen in einzelnen Monaten können mit übertariflichen Leistungen " +
                "in anderen Monaten verrechnet werden. Sollte das Unternehmen während einer " +
                "Tarifbindung über- oder außertarifliche Leistungen erbringen, sind diese auf " +
                "die tariflichen Leistungen, Tariferhöhungen, Höhergruppierungen anrechenbar.<br/>" +
                "|(4) Nach Ablauf einer Bindung an Kollektivvereinbarungen (Wegfall einer " +
                "Allgemeinverbindlicherklärung bzw. Ablauf des Tarifvertrages oder Ablauf " +
                "einer Betriebsvereinbarung oder Entfall einer Regelungsabrede) wird hiermit " +
                "vereinbart, dass die während der Anwendung kollektivrechtlicher Regelungen " +
                "verdrängten Bestimmungen dieses Arbeitsvertrages in vollem Umfang wieder aufleben. " +
                "Das gleiche gilt für sonstige Vereinbarungen aus und im Zusammenhang " +
                "mit dem Arbeitsverhältnis, soweit diese durch anwendbare Tarifbestimmungen " +
                "ganz oder teilweise verdrängt waren. Eine etwaige Nachwirkung von Tarifverträgen " +
                "bzw. von Betriebsvereinbarungen und eine betriebliche Übung werden hiermit " +
                "ausgeschlossen. Insbesondere richtet sich die Vergütung des Arbeitnehmers " +
                "nach Ablauf einer Tarifbindung wieder ausschließlich nach § 4.");
        kasseParagraphList.add(kasseParagraph6);


        ContractParagraph kasseParagraph7 = new ContractParagraph();
        kasseParagraph7.setParagraphNumber("7");
        kasseParagraph7.setParagraphTitle("Arbeitszeit");
        kasseParagraph7.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph7.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph7.setSelectionGroups(false);
        kasseParagraph7.setClean(true);
        kasseParagraph7.setTemplate(true);
        kasseParagraph7.setParagraphContent("(1) Der Arbeitgeber und der Arbeitnehmer sind sich darüber einig, " +
                "dass die Jahresarbeitszeit<br/><br/>" +
                "<span style=\"display:block; text-align:center; margin:0 auto;\">[Arbeitsstunden] Stunden</span><br/>" +
                "beträgt und zwar unabhängig von den eventuell zukünftig geltenden tariflichen Bestimmungen. " +
                "Diese Arbeitszeit wird im Rahmen eines Dienstplanes entsprechend den betrieblichen Erfordernissen " +
                "in der Regel wie folgt innerhalb der Woche aufgeteilt:<br/><br/>" +
                "<span style=\"display:block; text-align:center; margin:0 auto;\">Samstag + [Wochentage] Wochentage</span><br/><br/>" +
                "|(2) Liegt diese Arbeitszeit über der regelmäßigen Arbeitszeit, die sich aus einem für den Arbeitgeber " +
                "verbindlichen Manteltarifvertrag ergibt und ist für den Arbeitgeber zugleich ein Lohn- bzw. " +
                "Gehaltstarifvertrag verbindlich, dann hat der Arbeitnehmer Anspruch darauf, dass sich die nach dem " +
                "Lohn- bzw. Gehaltstarifvertrag bemessene Mindestvergütung um den der Mehrarbeit entsprechenden " +
                "Anteil erhöht. Nach Ablauf der Tarifbindung (Wegfall einer Allgemeinverbindlichkeitserklärung bzw. " +
                "Ablauf des Tarifvertrages) gilt § 6 Abs. 4 dieses Vertrages.<br/>" +
                "|(3) Der Arbeitnehmer ist bei entsprechendem betrieblichem Bedarf bereit, Mehr- und Überstunden zu " +
                "leisten. Diese Bereitschaft bezieht sich auch auf die Erbringung von Arbeitsleistungen an " +
                "Wochenenden. Überstunden werden nur dann anerkannt, wenn sie vorher schriftlich mit dem " +
                "Arbeitgeber vereinbart bzw. durch diesen angeordnet wurden. Darüber hinaus ist der Arbeitnehmer " +
                "verpflichtet, Beginn und Ende der Überstunden und Mehrarbeit täglich schriftlich zu erfassen und " +
                "diese spätestens am Ende der Kalenderwoche dem Arbeitgeber vorzulegen, wenn im Betrieb generell " +
                "oder für einzelne Tage keine elektronische Zeiterfassung möglich ist; anderenfalls gelten die " +
                "Bestimmungen des § 8.<br/>" +
                "|(4) Die Verteilung der Arbeitszeit wird flexibel im Rahmen der täglichen Geschäftsöffnungszeiten " +
                "auf Grundlage des jeweils gültigen Dienst-/Schichtplanes geregelt. Der Arbeitnehmer erklärt sich " +
                "bereit, auch an Sonderverkaufsveranstaltungen außerhalb dieser Öffnungszeiten sowie an Sonntags- " +
                "und Feiertagsöffnungen teilzunehmen. Darüber hinaus gilt als vereinbart, dass die tägliche " +
                "Arbeitszeit bei einer etwaigen Änderung des Ladenschlussgesetzes den neuen Geschäftsöffnungszeiten " +
                "angepasst wird. Die Vergütung ändert sich danach nicht.<br/>" +
                "|(5) Der Arbeitgeber ist berechtigt, dem Arbeitnehmer nach billigem Ermessen feste Pausenzeiten " +
                "vorzugeben, für die keine Vergütungspflicht besteht; dabei ist auch eine Vorgabe von Pausen " +
                "zulässig, die die Dauer der gesetzlichen Mindestpausen übersteigt. Soweit eine solche Vorgabe nicht " +
                "besteht, gelten die gesetzlichen Pausenbestimmungen. Der Arbeitnehmer ist verpflichtet, Pausen " +
                "entsprechend der geltenden Vorgabe einzulegen. Wenn und soweit eine Erfassung von individuellen " +
                "Pausenzeiten über das IT-System des Arbeitgebers möglich ist, ist der Arbeitnehmer zur individuellen " +
                "Erfassung seiner Pausenzeiten über dieses System verpflichtet; dies gilt für alle Pausenzeiten, auch " +
                "sog. „Raucherpausen“ und Arbeitsunterbrechungen aus vergleichbaren Gründen. Wenn eine " +
                "individuelle Pausenzeiterfassung über das IT-System nicht möglich ist, werden die vorgegebenen " +
                "Pausenzeiten im Rahmen der Zeiterfassung automatisch pauschaliert in Abzug gebracht, ohne dass " +
                "der Arbeitgeber Dauer und Lage der tatsächlichen Inanspruchnahme von Pausen erfasst oder prüft. " +
                "Eine Änderung der automatisch in Ansatz gebrachten Pausenzeiten, etwa auf der Grundlage " +
                "händischer Aufzeichnungen des Arbeitnehmers, erfolgt nicht.<br/>" +
                "|(6) Der Arbeitgeber ist berechtigt, Kurzarbeit anzuordnen, wenn ein erheblicher, auf wirtschaftlichen " +
                "Gründen oder einem unabwendbaren Ereignis beruhender Arbeitsausfall vorliegt und er dies bei der " +
                "Agentur für Arbeit anzeigt. Im Fall der Anordnung von Kurzarbeit ist der Arbeitnehmer mit der " +
                "vorübergehenden Verkürzung seiner individuellen Arbeitszeit sowie der dementsprechenden " +
                "Reduzierung seiner Vergütung einverstanden, wenn und soweit die Voraussetzungen für die " +
                "Gewährung von Kurzarbeitergeld erfüllt sind. Bei vollständigem Arbeitsausfall können die Arbeitszeit " +
                "und dementsprechend auch die Vergütung auf Null herabgesetzt werden (Kurzarbeit Null). Der " +
                "Arbeitgeber hat dem Arbeitnehmer gegenüber bei der Anordnung von Kurzarbeit eine " +
                "Ankündigungsfrist von zwei Wochen einzuhalten; diese Ankündigungsfrist kann durch eine " +
                "Betriebsvereinbarung abgekürzt werden.<br/>");
        List<ContentField> kasseParagraph7ContentFields = new ArrayList<>();
        ContentField kasseParagraph7ContentField1 = new ContentField();
        kasseParagraph7ContentField1.setFieldName("Wochentage");
        kasseParagraph7ContentField1.setFieldDescription("Anzahl Wochentage");
        kasseParagraph7ContentField1.setFieldType(FieldType.TEXT);
        kasseParagraph7ContentFields.add(kasseParagraph7ContentField1);
        ContentField kasseParagraph7ContentField2 = new ContentField();
        kasseParagraph7ContentField2.setFieldName("Arbeitsstunden");
        kasseParagraph7ContentField2.setFieldDescription("Anzahl der Arbeitsstunden in der gewählten Einordnung");
        kasseParagraph7ContentField2.setFieldType(FieldType.TEXT);
        kasseParagraph7ContentFields.add(kasseParagraph7ContentField2);
        kasseParagraph7.setContentFields(kasseParagraph7ContentFields);
        kasseParagraphList.add(kasseParagraph7);


        ContractParagraph kasseParagraph8 = new ContractParagraph();
        kasseParagraph8.setParagraphNumber("8");
        kasseParagraph8.setParagraphTitle("Arbeitszeitkonto");
        kasseParagraph8.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph8.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph8.setSelectionGroups(false);
        kasseParagraph8.setClean(true);
        kasseParagraph8.setTemplate(true);
        kasseParagraph8.setParagraphContent("(1) Der Arbeitgeber führt für den Arbeitnehmer ein individuelles Arbeitszeitkonto. In diesem werden die " +
                "tatsächlich geleisteten Arbeitszeiten erfasst und mit der regelmäßigen Soll-Arbeitszeit nach § 7 " +
                "saldiert. Dabei kann das Arbeitszeitkonto bis zum Umfang von 200 Arbeitsstunden auch im Soll " +
                "belastet werden; der Arbeitnehmer ist in diesem Fall auch kalenderjahrübergreifend zur Nacharbeit " +
                "verpflichtet. Zeiten der Entgeltfortzahlung (insbesondere Krankheit, Urlaub) werden mit der " +
                "vereinbarten täglichen Arbeitszeit erfasst (Ist = Soll). Zeiten des Ruhens des Arbeitsverhältnisses " +
                "bleiben für das Arbeitszeitkonto neutral.<br/><br/>" +
                "|(2) Die Zeiterfassung erfolgt – vorbehaltlich einer abweichenden Vorgabe gemäß Abs. 3 – durch " +
                "Ein- und Ausstempeln an den betrieblich vorgesehenen Terminals am jeweiligen Personaleingang. " +
                "Der Arbeitgeber ist hierbei berechtigt, bei der Erfassung der tatsächlich geleisteten Arbeitszeiten unter " +
                "Berücksichtigung der konkreten betrieblichen Anforderungen, insbesondere der innerbetrieblichen " +
                "Wege- und vergleichbarer Zeiten, in angemessenem Umfang Kappungsgrenzen und Schwellenwerte " +
                "einzuführen und/oder Rundungen vorzunehmen. Soweit für den Arbeitnehmer eine betriebliche " +
                "Arbeitszeiteinteilung oder Schichtplanung besteht, ist der Arbeitgeber ferner berechtigt, angemessene " +
                "automatische Kappungen der betrieblichen Anwesenheitszeiten vorzusehen.<br/>" +
                "|(3) Der Arbeitgeber ist berechtigt, im Betrieb insgesamt, für einzelne Abteilungen oder für einzelne " +
                "Arbeitsplätze (auch Flexoffice-Arbeitsplätze) eine Erfassung der tatsächlichen Arbeitszeiten über das " +
                "IT-System am Arbeitsplatz bzw. dafür vorgesehene Erfassungsterminals am jeweiligen Standort " +
                "vorzugeben. Wenn und soweit eine solche Vorgabe des Arbeitgebers besteht, ist der Arbeitnehmer " +
                "abweichend von Abs. 2 verpflichtet, Beginn und Ende der täglichen Arbeitszeit gemäß den " +
                "Vorgaben des Arbeitgebers zu erfassen. Eine Erfassung oder ein Ein- und Ausstempeln abweichend " +
                "von den Vorgaben des Arbeitgebers ist unzulässig. Unabhängig hiervon ist und bleibt der " +
                "Arbeitnehmer zur Registrierung des Kommens und Gehens beim Personaleingang verpflichtet. Das " +
                "gilt auch bei Inanspruchnahme von Pausen außerhalb des Hauses. Diese Registrierung hat keinen " +
                "Einfluss auf die Arbeitszeit- und Pausenzeiterfassung und wird auch nicht hierfür herangezogen.<br/>" +
                "|(4) Manuelle Korrekturen von erfassten Arbeitszeiten werden nur vorgenommen, wenn diese vorab " +
                "vom Vorgesetzten genehmigt worden sind.<br/>" +
                "|(5) Die nach den vorstehenden Absätzen ermittelte Anwesenheitszeit wird um die Pausenzeiten nach " +
                "§ 7 Abs. 5 gekürzt und als Netto-Arbeitszeit in das Arbeitszeitkonto übernommen.<br/>" +
                "|(6) Das Arbeitszeitkonto wird jeweils zum Ende eines Kalenderjahres abgerechnet. Bei unterjährigem " +
                "Ausscheiden des Arbeitnehmers erfolgt die Abrechnung spätestens bei Beendigung des " +
                "Arbeitsverhältnisses. Bei Abrechnung des Arbeitszeitkontos wird geprüft, ob der Arbeitnehmer im " +
                "zurückliegenden Kalenderjahr insgesamt eine Vergütung (einschließlich etwaiger " +
                "Ausgleichszahlungen auf die Mindestvergütung) erhalten hat, die mindestens der Summe der gemäß " +
                "den Absätzen 1 bis 5 erfassten Arbeitsstunden multipliziert mit dem jeweiligen gesetzlichen " +
                "Mindestlohn entspricht. Sollte dies nicht der Fall sein, so erfolgt zugunsten des Arbeitnehmers eine " +
                "entgeltliche Abgeltung so vieler zusätzlicher Arbeitsstunden aus dem Arbeitszeitkonto, dass der " +
                "gesetzliche Mindestlohnanspruch erfüllt ist. Im Falle des unterjährigen Ausscheidens gilt als " +
                "zurückliegendes Kalenderjahr das laufende Kalenderjahr bis zum Zeitpunkt des Ausscheidens.<br/>" +
                "|(7) Ein nach Abrechnung und etwaiger Abgeltung gemäß Abs. 6 verbleibender Habensaldo des " +
                "Arbeitszeitkontos wird um die gemäß § 4 Abs. 3 im zurückliegenden Abrechnungszeitraum bereits mit " +
                "der laufenden Vergütung abgegoltenen Überstunden gekürzt; der Aufbau eines Sollsaldos durch diese " +
                "Kürzung ist ausgeschlossen. Ein hiernach noch verbleidender Soll- oder Habensaldo wird als " +
                "Anfangssaldo in das Arbeitszeitkonto des nachfolgenden Abrechnungszeitraums übertragen.<br/>" +
                "|(8) Bei Guthaben des Arbeitszeitkontos handelt es sich um reine Zeitguthaben, die durch Freizeit " +
                "abzubauen sind. Der Arbeitgeber kann einen Freizeitausgleich im Rahmen des § 106 GewO auch " +
                "einseitig anordnen. Während des Laufs des Arbeitsverhältnisses besteht außer in den Fällen des " +
                "Abs. 6 kein Anspruch auf Abgeltung von Zeitguthaben durch zusätzliche Vergütung.<br/>" +
                "|(9) Betriebsvereinbarungen über Arbeitszeitkonten gehen den vorstehenden Bestimmungen vor (§ 6).<br/>");
        kasseParagraphList.add(kasseParagraph8);


        ContractParagraph kasseParagraph9 = new ContractParagraph();
        kasseParagraph9.setParagraphNumber("9");
        kasseParagraph9.setParagraphTitle("Beendigung des Arbeitsverhältnisses");
        kasseParagraph9.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph9.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph9.setSelectionGroups(false);
        kasseParagraph9.setClean(true);
        kasseParagraph9.setTemplate(true);
        kasseParagraph9.setParagraphContent("(1) Das Arbeitsverhältnis ist jederzeit ordentlich kündbar; dies gilt auch während einer etwaigen " +
                "Befristung. Während einer vereinbarten Probezeit (§ 1 Abs. 2) beträgt die Kündigungsfrist 2 Wochen. " +
                "Im Übrigen gelten die Kündigungsfristen des § 622 BGB. Das Arbeitsverhältnis kann auch schon vor " +
                "seinem Beginn gekündigt werden. Die Kündigungsfrist beginnt in diesem Fall mit dem Zugang der " +
                "Kündigung.<br/><br/>" +
                "|(2) Eine fristlose Kündigung des Arbeitsverhältnisses ist bei Vorliegen eines wichtigen Grundes " +
                "jederzeit möglich. Sollte eine fristlose Kündigung unwirksam sein, so gilt diese als fristgemäße " +
                "Kündigung zum nächsten zulässigen Kündigungstermin.<br/>" +
                "|(3) Das Arbeitsverhältnis endet, ohne dass es einer Kündigung bedarf, mit Ablauf des Monats, in dem " +
                "der Arbeitnehmer das gesetzlich geregelte Regelrentenalter erreicht. Bis zu diesem Termin kann das " +
                "Arbeitsverhältnis gemäß Abs. 1 von jeder Partei unter Einhaltung der dort geregelten " +
                "Kündigungsfristen gekündigt werden. Soweit sich die Regelaltersgrenze ändert, kommen die " +
                "geänderten gesetzlichen Vorschriften zur Anwendung.<br/>" +
                "|(4) Das Arbeitsverhältnis endet ebenfalls zu dem Zeitpunkt, ab dem der Arbeitnehmer eine " +
                "unbefristete Rente wegen voller Erwerbsminderung erhält, frühestens aber an dem Tag, an dem der " +
                "entsprechende Rentenbescheid dem Arbeitnehmer zugeht und der Arbeitnehmer gegen diesen " +
                "Bescheid nicht Widerspruch einlegt. Legt der Arbeitnehmer Widerspruch gegen den Bescheid ein, so " +
                "endet das Arbeitsverhältnis, sobald rechtskräftig über die Gewährung der Erwerbsminderungsrente " +
                "entschieden ist. Der Arbeitnehmer verpflichtet sich, den Arbeitgeber über den Zugang eines " +
                "entsprechenden Rentenbescheides zu informieren. Während des Bezugs einer befristeten Rente " +
                "wegen voller Erwerbsminderung ruht das Arbeitsverhältnis.<br/>" +
                "|(5) Der Arbeitnehmer kann während der Kündigungsfrist unter Fortzahlung der Vergütung und unter " +
                "Anrechnung auf Urlaubsansprüche sowie auf ein etwaiges Zeitguthaben aus dem Arbeitszeitkonto von " +
                "der Arbeitsverpflichtung freigestellt werden.<br/>");
        kasseParagraphList.add(kasseParagraph9);


        ContractParagraph kasseParagraph10 = new ContractParagraph();
        kasseParagraph10.setParagraphNumber("10");
        kasseParagraph10.setParagraphTitle("Allgemeine Pflichten");
        kasseParagraph10.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph10.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph10.setSelectionGroups(true);
        kasseParagraph10.setClean(true);
        kasseParagraph10.setTemplate(true);
        kasseParagraph10.setParagraphContent("(1) Der Arbeitnehmer ist verpflichtet, den Arbeitsanweisungen der zuständigen Vorgesetzten Folge zu " +
                "leisten. Er wird die ihm übertragenen Arbeiten sorgfältig und gewissenhaft ausführen.<br/><br/>" +
                "|(2) Der Arbeitnehmer darf Fahrzeuge auf dem Betriebsgelände nur in den angewiesenen Bereichen " +
                "und nur mit dessen ausdrücklicher Genehmigung abstellen. Das Abstellen geschieht in jedem Fall " +
                "ausschließlich auf Gefahr des Arbeitnehmers.<br/>" +
                "|[OPT-10-1][OPT-10-2]<br/>");
        List<OptionalContent> kasseParagraph10OptionalContents = new ArrayList<>();
        OptionalContent kasseParagraph10OptionalContent1 = new OptionalContent();
        kasseParagraph10OptionalContent1.setTitle("Erscheinungsbild");
        kasseParagraph10OptionalContent1.setShortName("OPT-10-1");
        kasseParagraph10OptionalContent1.setContent("3) Der Arbeitnehmer hat auf ein gepflegtes äußeres " +
                "Erscheinungsbild zu achten, insbesondere die berufs- oder betriebsübliche Kleidung zu tragen. " +
                "Im Übrigen gilt die jeweils gültige Betriebsordnung.");
        kasseParagraph10OptionalContent1.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph10OptionalContent1.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph10OptionalContent1.setSelectionGroup(1);
        kasseParagraph10OptionalContents.add(kasseParagraph10OptionalContent1);
        OptionalContent kasseParagraph10OptionalContent2 = new OptionalContent();
        kasseParagraph10OptionalContent2.setTitle("Dienstkleidung");
        kasseParagraph10OptionalContent2.setShortName("OPT-10-2");
        kasseParagraph10OptionalContent2.setContent("(3) Der Arbeitnehmer verpflichtet sich, während der Arbeit " +
                "die vom Arbeitgeber gestellte Dienstkleidung zu tragen. Er verpflichtet sich ferner, die " +
                "Dienstkleidung pfleglich zu behandeln und diese regelmäßig auf eigene Kosten zu reinigen. Für " +
                "Schäden an der Dienstkleidung haftet der Arbeitnehmer. Die normale Abnutzung geht zu Lasten des " +
                "Arbeitgebers. Die Dienstkleidung bleibt Eigentum des Arbeitgebers und ist auf Verlangen, " +
                "spätestens jedoch bei Beendigung des Arbeitsverhältnisses an den Arbeitgeber zurückzugeben.");
        kasseParagraph10OptionalContent2.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph10OptionalContent2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph10OptionalContent2.setSelectionGroup(1);
        kasseParagraph10OptionalContents.add(kasseParagraph10OptionalContent2);
        kasseParagraph10.setOptionalContents(kasseParagraph10OptionalContents);
        kasseParagraphList.add(kasseParagraph10);


        ContractParagraph kasseParagraph11 = new ContractParagraph();
        kasseParagraph11.setParagraphNumber("11");
        kasseParagraph11.setParagraphTitle("Weitere Tätigkeiten, Wettbewerb");
        kasseParagraph11.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph11.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph11.setSelectionGroups(false);
        kasseParagraph11.setClean(true);
        kasseParagraph11.setTemplate(true);
        kasseParagraph11.setParagraphContent("(1) Während des Bestandes dieses Arbeitsvertrages ist dem Arbeitnehmer jegliche Tätigkeit für ein " +
                "Unternehmen untersagt, das mit dem Arbeitgeber im Wettbewerb steht. Das gleiche gilt für eine " +
                "Beteiligung an einem solchen Unternehmen, soweit diese nicht in einer reinen Kapitalanlage ohne " +
                "gesellschaftsrechtliche Einflussnahmemöglichkeit besteht.<br/><br/>" +
                "|(2) Eine anderweitige Erwerbstätigkeit ist ihm nur mit ausdrücklicher Zustimmung des Arbeitgebers " +
                "gestattet, wobei der Arbeitgeber diese Zustimmung erteilen wird, soweit berechtigte Belange des " +
                "Arbeitgebers nicht erheblich beeinträchtigt werden. Tritt eine solche Beeinträchtigung später auf, so " +
                "kann der Arbeitgeber die Zustimmung widerrufen.<br/>");
        kasseParagraphList.add(kasseParagraph11);


        ContractParagraph kasseParagraph12 = new ContractParagraph();
        kasseParagraph12.setParagraphNumber("12");
        kasseParagraph12.setParagraphTitle("Krankheit und Arbeitsverhinderung");
        kasseParagraph12.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph12.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph12.setSelectionGroups(false);
        kasseParagraph12.setClean(true);
        kasseParagraph12.setTemplate(true);
        kasseParagraph12.setParagraphContent("(1) Der Arbeitnehmer verpflichtet sich, jede krankheitsbedingte Arbeitsunfähigkeit und " +
                "Arbeitsverhinderung aus anderen Gründen sowie deren voraussichtliche Dauer dem Vorgesetzten " +
                "unverzüglich nach Erkennbarkeit, spätestens am ersten Tag der Abwesenheit zu Dienstbeginn, " +
                "telefonisch zu melden. Dauert die Arbeitsunfähigkeit bzw. Arbeitsverhinderung länger als ursprünglich " +
                "mitgeteilt, gelten die Pflichten entsprechend.<br/><br/>" +
                "|(2) Eine krankheitsbedingte Arbeitsunfähigkeit ist vom ersten Tag an durch ein ärztliches Attest zu " +
                "belegen. Das Gleiche gilt, wenn die Arbeitsunfähigkeit länger dauert als ursprünglich angegeben.<br/>" +
                "|(3) Bei einer akut aufgetretenen Pflegesituation im Sinne von § 2 PflegeZG ist der Arbeitnehmer " +
                "verpflichtet, die Pflegebedürftigkeit des nahen Angehörigen und die Erforderlichkeit der Pflege durch " +
                "ein ärztliches Attest nachzuweisen. Das Attest muss spätestens am dritten Arbeitstag vorgelegt " +
                "werden.<br/>" +
                "|(4) Der Arbeitnehmer ist verpflichtet, einen Arbeitsunfall unverzüglich anzuzeigen.<br/>" +
                "|(5) Der Arbeitnehmer ist bereit, sich im Falle von durch Tatsachen begründeten Zweifeln an seiner " +
                "Arbeitsfähigkeit oder an dem Bestand einer zur Arbeitsunfähigkeit führenden Erkrankung auf " +
                "Verlangen des Arbeitgebers einer vertrauensärztlichen Untersuchung zu unterziehen.<br/>" +
                "|(6) Der Arbeitgeber ist zur Entgeltfortzahlung nur in den gesetzlich zwingend normierten Fällen " +
                "verpflichtet. Die Bestimmung des § 616 BGB wird – soweit rechtlich zulässig – ausgeschlossen. Dies " +
                "gilt insbesondere bei Arbeitsverhinderung wegen Kindpflege und Pflege nach dem Pflegezeitgesetz.<br/>");
        kasseParagraphList.add(kasseParagraph12);


        ContractParagraph kasseParagraph13 = new ContractParagraph();
        kasseParagraph13.setParagraphNumber("13");
        kasseParagraph13.setParagraphTitle("Abtretung von Schadensersatzforderungen");
        kasseParagraph13.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph13.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph13.setSelectionGroups(false);
        kasseParagraph13.setClean(true);
        kasseParagraph13.setTemplate(true);
        kasseParagraph13.setParagraphContent("Schadensersatzansprüche, die der Arbeitnehmer bei Unfall oder Krankheit gegen Dritte erwirkt, " +
                "werden hiermit an den Arbeitgeber bis zur Höhe der Beträge abgetreten, die der Arbeitgeber aufgrund " +
                "gesetzlicher, tariflicher oder vertraglicher Bestimmungen für die Dauer der Arbeitsunfähigkeit " +
                "gewähren muss. Dazu hat der Arbeitnehmer unverzüglich dem Arbeitgeber die zur Geltendmachung " +
                "der Schadenersatzansprüche erforderlichen Angaben zu machen.<br/><br/>");
        kasseParagraphList.add(kasseParagraph13);


        ContractParagraph kasseParagraph14 = new ContractParagraph();
        kasseParagraph14.setParagraphNumber("14");
        kasseParagraph14.setParagraphTitle("Verpfändung des Arbeitseinkommens");
        kasseParagraph14.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph14.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph14.setSelectionGroups(false);
        kasseParagraph14.setClean(true);
        kasseParagraph14.setTemplate(true);
        kasseParagraph14.setParagraphContent("Der Arbeitnehmer darf seine Vergütungsansprüche an Dritte nur nach vorheriger schriftlicher<br/>" +
                "Zustimmung durch den Arbeitgeber verpfänden oder abtreten.<br/><br/>");
        kasseParagraphList.add(kasseParagraph14);


        ContractParagraph kasseParagraph15 = new ContractParagraph();
        kasseParagraph15.setParagraphNumber("15");
        kasseParagraph15.setParagraphTitle("Fortbildung");
        kasseParagraph15.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph15.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph15.setSelectionGroups(false);
        kasseParagraph15.setClean(true);
        kasseParagraph15.setTemplate(true);
        kasseParagraph15.setParagraphContent("(1) Der Arbeitnehmer verpflichtet sich, die vom Arbeitgeber direkt oder indirekt gebotenen " +
                "Fortbildungsmöglichkeiten zu nutzen. Er ist bereit, auch Seminare und Schulungen außerhalb des " +
                "Betriebes und an anderen Orten zu besuchen, selbst wenn damit eine mehrtägige Ortsabwesenheit " +
                "verbunden ist. Dauert die Fortbildungsmaßnahme einschließlich eventueller, vom Arbeitnehmer selbst " +
                "organisierter Wegezeiten länger als die vertraglich abzuleistende Arbeitszeit, so zählt die darüber " +
                "hinausgehende Zeit nicht als Mehrarbeit.<br/><br/>" +
                "|(2) Die Teilnahme an Schulungen und Seminaren ist generell durch die Vergütung nach § 4 " +
                "umfassend abgegolten.<br/>");
        kasseParagraphList.add(kasseParagraph15);


        ContractParagraph kasseParagraph16 = new ContractParagraph();
        kasseParagraph16.setParagraphNumber("16");
        kasseParagraph16.setParagraphTitle("Gesetzlicher Urlaub");
        kasseParagraph16.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph16.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph16.setSelectionGroups(false);
        kasseParagraph16.setClean(true);
        kasseParagraph16.setTemplate(true);
        kasseParagraph16.setParagraphContent("(1) Die Dauer des Urlaubs und die Bezahlung richten sich nach dem Bundesurlaubsgesetz. Der " +
                "Anspruch auf Urlaub vermindert sich – soweit gesetzlich zulässig – zeitanteilig für Tage innerhalb des " +
                "jeweiligen Urlaubsjahres, an denen weder Arbeitspflicht noch Entgeltanspruch bestehen; dies gilt " +
                "insbesondere bei unbezahltem Sonderurlaub und Kurzarbeit, nicht aber im Falle einer Erkrankung des " +
                "Arbeitnehmers.<br/><br/>" +
                "|(2) Der Urlaub wird im Rahmen der betrieblichen Möglichkeiten und unter Berücksichtigung der " +
                "persönlichen Wünsche des Arbeitnehmers gewährt.<br/>");
        kasseParagraphList.add(kasseParagraph16);


        ContractParagraph kasseParagraph16A = new ContractParagraph();
        kasseParagraph16A.setParagraphNumber("16a");
        kasseParagraph16A.setParagraphTitle("Freiwillig gewährter Urlaub");
        kasseParagraph16A.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph16A.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph16A.setSelectionGroups(false);
        kasseParagraph16A.setClean(true);
        kasseParagraph16A.setTemplate(true);
        kasseParagraph16A.setParagraphContent("(1) Gewährt das Unternehmen über den gesetzlichen Urlaub hinaus zusätzlichen Urlaub, handelt es " +
                "sich um eine freiwillige Leistung, auf die auch nach wiederholter Gewährung kein Rechtsanspruch " +
                "entsteht. Auch das Entstehen einer betrieblichen Übung wird ausdrücklich ausgeschlossen.<br/><br/>" +
                "|(2) Gewährt das Unternehmen auf Basis der Freiwilligkeit generell zusätzlichen Urlaub, haben nur " +
                "diejenigen Arbeitnehmer Anspruch auf freiwilligen Urlaub, die im Urlaubsjahr ganzjährig in einem " +
                "Arbeitsverhältnis gestanden haben. Im Ein- und Austrittsjahr besteht kein Anspruch auf freiwilligen " +
                "Urlaub. § 16 Abs. 1 S. 2 dieses Vertrages gilt auch für den freiwilligen Urlaub entsprechend.<br/>" +
                "|(3) Der genommene Urlaub wird zunächst auf den gesetzlichen und sodann auf den etwa freiwillig " +
                "gewährten Urlaub angerechnet.<br/>" +
                "|(4) Nicht in Anspruch genommener freiwillig gewährter Urlaub verfällt grundsätzlich mit Ablauf des " +
                "31.03. des Folgejahres, ohne dass es eines ausdrücklichen Hinweises des Arbeitsgebers im Einzelfall " +
                "bedarf. Dies gilt auch bei einer langandauernden Erkrankung. Für bei Ausscheiden aus dem " +
                "Arbeitsverhältnis noch bestehenden freiwillig gewährten Urlaub erfolgt keine Abgeltung oder ein " +
                "sonstiger Ausgleich.<br/>");
        kasseParagraphList.add(kasseParagraph16A);

        ContractParagraph kasseParagraph17 = new ContractParagraph();
        kasseParagraph17.setParagraphNumber("17");
        kasseParagraph17.setParagraphTitle("Vertraulichkeit");
        kasseParagraph17.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph17.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph17.setSelectionGroups(false);
        kasseParagraph17.setClean(true);
        kasseParagraph17.setTemplate(true);
        kasseParagraph17.setParagraphContent("(1) Der Arbeitnehmer ist verpflichtet, insbesondere auch während der Zeit nach Beendigung dieses " +
                "Arbeitsvertrages alle vertraulichen Angelegenheiten, Betriebs- und Geschäftsgeheimnisse des " +
                "Arbeitgebers und verbundener Unternehmen, welche ihm bei Ausübung seiner Tätigkeiten für den " +
                "Arbeitgeber zur Kenntnis gelangen oder die vom Arbeitgeber als vertraulich bezeichnet werden, streng " +
                "geheim zu halten. Als vertrauliche Angelegenheiten in diesem Sinne gelten auch " +
                "Geschäftsgeheimnisse im Sinne des GeschGehG, deren Offenlegung nicht nach § 3 Abs. 2 " +
                "GeschGehG erlaubt ist, insbesondere Verfahren, Daten, Know-how, Marketing-Pläne, " +
                "Geschäftsplanungen, Budgets, Lizenzen, Preise, Kosten und Kunden- und Lieferantenlisten. " +
                "In Zweifelsfällen ist der Arbeitnehmer verpflichtet, eine Weisung des Arbeitgebers einzuholen, ob eine " +
                "bestimmte Tatsache als vertraulich zu behandeln ist.<br/><br/>" +
                "|(2) Der Arbeitnehmer sichert zu, dass er insbesondere sämtliche ihm in Ausübung des " +
                "Arbeitsverhältnisses übergebenen oder bekannt gewordenen Daten und Dokumente über die " +
                "Angelegenheiten des Unternehmens, seiner Mitarbeiter, Lieferanten, Kunden und sonstigen Kontakte " +
                "zeitlich unbegrenzt, insbesondere auch über die Dauer des Vertragsverhältnisses hinaus, streng " +
                "vertraulich behandelt und geheim hält. Er versichert, dass er derartige Daten und Dokumente Dritten " +
                "nicht zugänglich machen oder sonst zum eigenen oder fremden Nutzen preisgeben wird, außer in " +
                "Erfüllung seiner vertraglichen Pflichten.<br/>" +
                "|(3) In besonderer Weise sind Daten von Kunden vertraulich zu behandeln und vor dem Zugriff Dritter " +
                "zu schützen. Auch eine Kommunikation mit Kunden über soziale Netzwerke oder unverschlüsselte " +
                "Messenger-Dienste, insbesondere WhatsApp, ist untersagt, soweit diese dem Arbeitnehmer nicht vom " +
                "Arbeitgeber generell oder für Einzelfälle gestattet worden ist.<br/>" +
                "|(4) Ausgenommen von den Verschwiegenheitsverpflichtungen sind Angaben gegenüber Behörden " +
                "oder aufgrund gesetzlicher Verpflichtung, soweit diese erforderlich sind, sowie Offenlegungen zum " +
                "Schutz eines berechtigten Interesses gemäß § 5 GeschGehG; der Arbeitnehmer ist aber verpflichtet " +
                "zunächst zu versuchen, den Schutz dieser berechtigen Interessen durch eine zumutbare interne " +
                "Meldung und Abhilfe zu erreichen und sich nicht ohne Weiteres an externe Dritte zu wenden.<br/>" +
                "|(5)<b> Alle Angaben, die dieses Arbeitsverhältnis betreffen, sind vertraulich zu behandeln und " +
                "dürfen Dritten nicht zugänglich gemacht werden.</b><br/>");
        kasseParagraphList.add(kasseParagraph17);


        ContractParagraph kasseParagraph18 = new ContractParagraph();
        kasseParagraph18.setParagraphNumber("18");
        kasseParagraph18.setParagraphTitle("Zuverlässigkeit");
        kasseParagraph18.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph18.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph18.setSelectionGroups(false);
        kasseParagraph18.setClean(true);
        kasseParagraph18.setTemplate(true);
        kasseParagraph18.setParagraphContent("(1) Der Arbeitnehmer versichert, dass bei Vertragsabschluss keine " +
                "einschlägigen Vorstrafen vorliegen oder Verfahren anhängig sind, die Zweifel an seiner beruflichen " +
                "Gewissenhaftigkeit, Zuverlässigkeit und seinem Verantwortungsgefühl begründen können. Der Arbeitgeber " +
                "ist versicherungs- und haftungsrechtlich gehalten, hierfür entsprechende Nachweise zu fordern und zu " +
                "prüfen. Der Arbeitnehmer legt unverzüglich, möglichst noch vor Beginn des Arbeitsverhältnisses ein " +
                "polizeiliches Führungszeugnis (Belegart N) in deutscher Sprache vor, dessen Ausstellungsdatum nicht " +
                "länger als drei Monate zurückliegt. Ein aktueller Eintrag in diesem Führungszeugnis, der Zweifel an " +
                "der Eignung und/oder Zuverlässigkeit des Arbeitnehmers begründet, berechtigt den Arbeitgeber zur " +
                "Anfechtung des Arbeitsvertrages. Das Arbeitsverhältnis gilt in diesem Fall als nicht zustande " +
                "gekommen; das gleiche gilt, wenn der Arbeitnehmer ein Führungszeugnis auch nach angemessener " +
                "Fristsetzung nicht vorlegt oder die Erteilung eines Führungszeugnisses sonstwie vereitelt.<br/><br/>" +
                "|(2) Auf begründete Anforderung des Arbeitgebers ist der Arbeitnehmer auch während der " +
                "Vertragslaufzeit verpflichtet, ein aktualisiertes Führungszeugnis vorzulegen. Ein Eintrag in einem " +
                "solchen Führungszeugnis, der Zweifel an der Eignung und/oder Zuverlässigkeit des Arbeitnehmers " +
                "begründet, kann Anlass für arbeitsrechtliche Konsequenzen des Arbeitgebers bis hin zu einer " +
                "ordentlichen oder sogar außerordentlichen Kündigung sein.<br/>" +
                "|(3) Liegt die Vorlage des letzten aktuellen Führungszeugnisses zu diesem Zeitpunkt noch nicht " +
                "mindestens fünf Jahre zurück und beinhaltet das vorzulegende Führungszeugnis keine neuen Eintragungen, " +
                "so erstattet der Arbeitgeber dem Arbeitnehmer die hiermit verbundenen Kosten. Führungszeugnisse für " +
                "den Arbeitgeber werden zur Personalakte genommen und ausschließlich zu Dokumentationszwecken genutzt.<br/><br/>");
        kasseParagraphList.add(kasseParagraph18);


        ContractParagraph kasseParagraph19 = new ContractParagraph();
        kasseParagraph19.setParagraphNumber("19");
        kasseParagraph19.setParagraphTitle("Rückgabe von Betriebsmitteln und Firmenunterlagen");
        kasseParagraph19.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph19.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph19.setSelectionGroups(false);
        kasseParagraph19.setClean(true);
        kasseParagraph19.setTemplate(true);
        kasseParagraph19.setParagraphContent("Beim Ausscheiden des Arbeitnehmers aus dem Betrieb sind alle dem Arbeitgeber gehörenden " +
                "Betriebsmittel, Unterlagen, schriftliche und digitale Aufzeichnungen, Werkzeuge, Zugangschips bzw. " +
                "-karten, Schlüssel etc. herauszugeben. Wird der Arbeitnehmer während des Arbeitsverhältnisses " +
                "freigestellt, so kann der Arbeitgeber die Herausgabe bereits ab Beginn der Freistellung verlangen. " +
                "Dem Arbeitgeber steht ein Zurückbehaltungsrecht an sämtlichen Leistungen bis zur Erfüllung fälliger " +
                "Herausgabeansprüche zu.<br/><br/>");
        kasseParagraphList.add(kasseParagraph19);


        ContractParagraph kasseParagraph20 = new ContractParagraph();
        kasseParagraph20.setParagraphNumber("20");
        kasseParagraph20.setParagraphTitle("Schriftform, Rechtsgültigkeit");
        kasseParagraph20.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph20.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph20.setSelectionGroups(false);
        kasseParagraph20.setClean(true);
        kasseParagraph20.setTemplate(true);
        kasseParagraph20.setParagraphContent("(1) Änderungen und Ergänzungen dieses Vertrages " +
                "bedürfen, soweit sie nicht auf einer individuellen Abrede beruhen, der Schriftform. " +
                "Gleiches gilt auch für die Aufhebung dieses Schriftformerfordernisses. Den Parteien " +
                "ist bewusst, dass ein Erwachsen von Ansprüchen aus einer betrieblichen Übung daher " +
                "ausgeschlossen ist.<br/><br/>" +
                "|(2) Für diesen Arbeitsvertrag gilt ausschließlich deutsches Recht als vereinbart.<br/>");
        kasseParagraphList.add(kasseParagraph20);


        ContractParagraph kasseParagraph21 = new ContractParagraph();
        kasseParagraph21.setParagraphNumber("21");
        kasseParagraph21.setParagraphTitle("Verfall von Ansprüchen");
        kasseParagraph21.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph21.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph21.setSelectionGroups(false);
        kasseParagraph21.setClean(true);
        kasseParagraph21.setTemplate(true);
        kasseParagraph21.setParagraphContent("(1) Alle wechselseitigen Ansprüche aus dem Arbeitsvertrag und solche, die damit in Verbindung " +
                "stehen sowie Ansprüche aus Anlass der Beendigung des Arbeitsverhältnisses verfallen, wenn sie nicht " +
                "innerhalb von drei Monaten nach Fälligkeit gegenüber der anderen Vertragspartei in Textform geltend " +
                "gemacht worden sind. War die Fälligkeit des Anspruches für den Arbeitnehmer auch bei größter " +
                "Sorgfalt nicht erkennbar, so kann der Arbeitnehmer Ansprüche abweichend noch innerhalb von drei " +
                "Monaten nach dem Zeitpunkt geltend machen, an dem der Arbeitnehmer Kenntnis von der Fälligkeit " +
                "haben musste.<br/><br/>" +
                "|(2) Lehnt die andere Vertragspartei den rechtzeitig geltend gemachten Anspruch ab oder erklärt sie " +
                "sich nicht innerhalb einer Erklärungsfrist von vier Wochen nach der Geltendmachung des Anspruchs, " +
                "so verfällt dieser dennoch, wenn er nicht innerhalb einer Frist von drei Monaten nach Ablehnung oder " +
                "dem Ablauf der Erklärungsfrist gerichtlich geltend gemacht wird.<br/>" +
                "|(3) Die Ausschluss- und Verfallfristen gelten nicht für wechselseitige Ansprüche aus einer Haftung für " +
                "vorsätzliche Pflichtverletzungen, für Schäden aus der Verletzung des Lebens, des Körpers oder der " +
                "Gesundheit, für Ansprüche auf verbindliche Mindestlöhne, andere nach staatlichem Recht zwingende " +
                "Mindestarbeitsbedingungen und nicht für sonstige Ansprüche, die kraft Gesetzes der Regelung durch " +
                "eine Ausschlussfrist entzogen sind. Sie gelten ebenfalls nicht für wechselseitige Ansprüche auf " +
                "Erstattung von Lohn- und Kirchensteuer, Solidaritätszuschlag sowie Sozialversicherungsbeiträgen, die " +
                "durch Nachberechnung entstanden sind.<br/>");
        kasseParagraphList.add(kasseParagraph21);


        ContractParagraph kasseParagraph22 = new ContractParagraph();
        kasseParagraph22.setParagraphNumber("22");
        kasseParagraph22.setParagraphTitle("– entfällt –");
        kasseParagraph22.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph22.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph22.setSelectionGroups(false);
        kasseParagraph22.setClean(true);
        kasseParagraph22.setTemplate(true);
        kasseParagraphList.add(kasseParagraph22);


        ContractParagraph kasseParagraph23 = new ContractParagraph();
        kasseParagraph23.setParagraphNumber("23");
        kasseParagraph23.setParagraphTitle("– entfällt –");
        kasseParagraph23.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph23.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph23.setSelectionGroups(false);
        kasseParagraph23.setClean(true);
        kasseParagraph23.setTemplate(true);
        kasseParagraphList.add(kasseParagraph23);


        ContractParagraph kasseParagraph24 = new ContractParagraph();
        kasseParagraph24.setParagraphNumber("24");
        kasseParagraph24.setParagraphTitle("Personalvollmachten");
        kasseParagraph24.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph24.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph24.setSelectionGroups(false);
        kasseParagraph24.setClean(true);
        kasseParagraph24.setTemplate(true);
        kasseParagraph24.setParagraphContent("Dem Arbeitnehmer ist bekannt, dass jeder Geschäftsführer und jeder " +
                "Prokurist des Arbeitgebers und seines Komplementärs einzeln und unabhängig von der Reichweite seines " +
                "Vertretungsrechts im Allgemeinen sowie der [Hausleiter / Restaurantleiter] und im Falle dessen " +
                "Verhinderung sein Stellvertreter jeweils einzeln durch den Arbeitgeber bevollmächtigt sind, alle " +
                "Rechtshandlungen betreffend das Arbeitsverhältnis für den Arbeitgeber vorzunehmen. Diese " +
                "Personalvollmacht erstreckt sich insbesondere auf Einstellungen sowie den Ausspruch und die " +
                "Entgegennahme von Kündigungen. Der Arbeitnehmer wird laufend über betriebliche Aushänge über die " +
                "Person des [Hausleiter / Restaurantleiter]s und seines Stellvertreters informiert; Geschäftsführer " +
                "und Prokuristen werden durch das Handelsregister allgemein bekannt gemacht.<br/><br/>");
        List<ContentField> kasseParagraph24ContentFields = new ArrayList<>();
        ContentField kasseParagraph24ContentField0 = new ContentField();
        kasseParagraph24ContentField0.setFieldName("Hausleiter / Restaurantleiter");
        kasseParagraph24ContentField0.setFieldDescription("Vorgesetzter");
        kasseParagraph24ContentField0.setFieldType(FieldType.AUSWAHL);
        kasseParagraph24ContentFields.add(kasseParagraph24ContentField0);
        kasseParagraph24.setContentFields(kasseParagraph24ContentFields);
        kasseParagraphList.add(kasseParagraph24);


        ContractParagraph kasseParagraph25 = new ContractParagraph();
        kasseParagraph25.setParagraphNumber("25");
        kasseParagraph25.setParagraphTitle("Sondervereinbarung");
        kasseParagraph25.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25.setSelectionGroups(false);
        kasseParagraph25.setClean(true);
        kasseParagraph25.setTemplate(true);
        kasseParagraph25.setParagraphContent("(1) Als [technisches Eintrittsdatum / Eintrittsdatum] gilt der [Eintrittsdatum], bzw. siehe § 1.<br/><br/>" +
                "|(2) Der Arbeitgeber gewährt zur Zeit als freiwillige Leistung [Anzahl Sonderurlaubstage] Arbeitstage 5-Tage-Woche) " +
                "Erholungsurlaub gemäß § 16a dieses Vertrages.<br/>" +
                "[OPT-25-1a]" +
                "[OPT-25-1b]" +
                "[OPT-25-2]" +
                "[OPT-25-2b]" +
                "[OPT-25-2c]" +
                "[OPT-25-3]" +
                "[OPT-25-5]" +
                "[OPT-25-6a]" +
                "[OPT-25-6b]" +
                "[OPT-25-6d]" +
                "[OPT-25-7a]" +
                "[OPT-25-7b]" +
                "[OPT-25-8]" +
                "[OPT-25-9]" +
                "[OPT-25-10a]" +
                "[OPT-25-10b]" +
                "[OPT-25-11a]" +
                "[OPT-25-11b]");
        List<ContentField> kasseParagraph25ContentFields = new ArrayList<>();
        ContentField kasseParagraph25ContentField0 = new ContentField();
        kasseParagraph25ContentField0.setFieldName("technisches Eintrittsdatum / Eintrittsdatum");
        kasseParagraph25ContentField0.setFieldDescription("technisch");
        kasseParagraph25ContentField0.setFieldType(FieldType.AUSWAHL);
        kasseParagraph25ContentFields.add(kasseParagraph25ContentField0);
        ContentField kasseParagraph25ContentField1 = new ContentField();
        kasseParagraph25ContentField1.setFieldName("Eintrittsdatum");
        kasseParagraph25ContentField1.setFieldDescription("Datum des Eitritts");
        kasseParagraph25ContentField1.setFieldType(FieldType.DATUM);
        kasseParagraph25ContentFields.add(kasseParagraph25ContentField1);
        ContentField kasseParagraph25ContentField2 = new ContentField();
        kasseParagraph25ContentField2.setFieldName("Anzahl Sonderurlaubstage");
        kasseParagraph25ContentField2.setFieldDescription("Anzahl der Sonderurlaubstage");
        kasseParagraph25ContentField2.setFieldType(FieldType.TEXT);
        kasseParagraph25ContentFields.add(kasseParagraph25ContentField2);
        kasseParagraph25.setContentFields(kasseParagraph25ContentFields);
        List<OptionalContent> kasseParagraph25OptionalContents = new ArrayList<>();
        OptionalContent kasseParagraph25OptionalContent1a = new OptionalContent();
        kasseParagraph25OptionalContent1a.setTitle("Urlaub im Eintrittsjahr");
        kasseParagraph25OptionalContent1a.setShortName("OPT-25-1a");
        kasseParagraph25OptionalContent1a.setContent("|(n) Im Kalenderjahr des Eintritts gewährt der Arbeitgeber insgesamt [Urlaubstage Eintrittsjahr] " +
                "Werktage Erholungsurlaub (gesetzlicher Urlaub und freiwilliger Urlaub).<br/>");
        kasseParagraph25OptionalContent1a.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent1a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent1aFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent1Field1 = new OptionalContentField();
        kasseParagraph25OptionalContent1Field1.setFieldName("Urlaubstage Eintrittsjahr");
        kasseParagraph25OptionalContent1Field1.setFieldDescription("gesetzlicher und freiwilliger Urlaub im Eintrittsjahr");
        kasseParagraph25OptionalContent1aFields.add(kasseParagraph25OptionalContent1Field1);
        kasseParagraph25OptionalContent1a.setOptionalContentFields(kasseParagraph25OptionalContent1aFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent1a);

        OptionalContent kasseParagraph25OptionalContent1b = new OptionalContent();
        kasseParagraph25OptionalContent1b.setTitle("Betriebsurlaub");
        kasseParagraph25OptionalContent1b.setShortName("OPT-25-1b");
        kasseParagraph25OptionalContent1b.setContent("|(n) Der Arbeitgeber ist berechtigt, bei betrieblichen Erfordernissen in angemessenem Umfang " +
                "Betriebsferien anzuordnen, die wie der sonstige Urlaub auf die dem Arbeitnehmer zustehenden " +
                "Urlaubstage anzurechnen sind.<br/>");
        kasseParagraph25OptionalContent1b.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent1b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent1b);

        OptionalContent kasseParagraph25OptionalContent2 = new OptionalContent();
        kasseParagraph25OptionalContent2.setTitle("Sonderleistungen");
        kasseParagraph25OptionalContent2.setShortName("OPT-25-2");
        kasseParagraph25OptionalContent2.setContent("|(n) Der Arbeitnehmer erhält zusätzlich zu der Vergütung nach § 4 folgende Leistungszulagen / " +
                "Prämien:<br/>" +
                "    [Prämie/-n]" +
                "Besteht das Arbeitsverhältnis bei Beginn des 13. Beschäftigungsmonats ungekündigt fort, " +
                "so zahlt der Arbeitgeber an den Arbeitnehmer einmalig eine Treueprämie in Höhe von" +
                "<b> [Treueprämie] EUR </b>brutto. Die Prämie dient ausschließlich der Honorierung der Betriebstreue. " +
                "Daher besteht kein, auch kein anteiliger Anspruch des Arbeitnehmers auf die Treueprämie, wenn das " +
                "Arbeitsverhältnis innerhalb der ersten zwölf Beschäftigungsmonate von einer der Parteien gekündigt wird; " +
                "auf den Anlass und den Grund der Kündigung kommt es hierbei nicht an. Auch ein Anspruch auf Schadensersatz " +
                "bei Nichtentstehung des Anspruchs infolge einer Kündigung besteht nicht. Eine Kündigung, die später durch die " +
                "Parteien oder ein Gericht für unwirksam erklärt wird, gilt nicht als Kündigung im Sinne dieser Klausel. " +
                "Die entstandene Treueprämie wird mit dem nächsten regulären Abrechnungslauf des Arbeitgebers gezahlt, " +
                "der auf die Vollendung des zwölften Beschäftigungsmonats folgt.<br/>");
        kasseParagraph25OptionalContent2.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent2.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent2Fields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent3Field1 = new OptionalContentField();
        kasseParagraph25OptionalContent3Field1.setFieldName("Prämie/-n");
        kasseParagraph25OptionalContent3Field1.setFieldDescription("Auflistung aller Prämien");
        kasseParagraph25OptionalContent2Fields.add(kasseParagraph25OptionalContent3Field1);
        OptionalContentField kasseParagraph25OptionalContent3Field2 = new OptionalContentField();
        kasseParagraph25OptionalContent3Field2.setFieldName("Treueprämie");
        kasseParagraph25OptionalContent3Field2.setFieldDescription("Treueprämie im 13. Monat (in Euro mit Nachkommastellen)");
        kasseParagraph25OptionalContent2Fields.add(kasseParagraph25OptionalContent3Field2);
        kasseParagraph25OptionalContent2.setOptionalContentFields(kasseParagraph25OptionalContent2Fields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent2);
        OptionalContent kasseParagraph25OptionalContent2b = new OptionalContent();
        kasseParagraph25OptionalContent2b.setTitle("Lage der Arbeitszeit");
        kasseParagraph25OptionalContent2b.setShortName("OPT-25-2b");
        kasseParagraph25OptionalContent2b.setContent("|(n) Abweichend von § 7 Abs. 4 und 5 sowie § 8 gilt bis auf Weiteres unter dem Vorbehalt des jederzeitigen Widerrufs folgendes: " +
                "Die Lage der Arbeitszeit wird eigenverantwortlich vom Arbeitnehmer unter Beachtung der Grenzen des Arbeitszeitgesetzes bestimmt. " +
                "Ferner ist der Arbeitnehmer von der Teilnahme an der betrieblichen Zeiterfassung entbunden und gehalten, " +
                "diese selbst zu überwachen und zu dokumentieren. Der Arbeitgeber führt für den Arbeitnehmer kein Arbeitszeitkonto. " +
                "Der Widerruf dieser Ausnahmeregelung bedarf keines besonderen Grundes, das Direktionsrecht des Arbeitgebers wird durch diese Bestimmung nicht eingeschränkt. " +
                "Der Arbeitgeber wird diese Ausnahmeregelung unter anderem widerrufen, wenn der Arbeitnehmer sich im Zusammenhang mit der Bestimmung, " +
                "Überwachung oder Dokumentation seiner Arbeitszeit pflichtwidrig verhält, " +
                "eine gesetzliche oder behördliche Anordnung dies erfordert oder dies betrieblich erforderlich ist.<br/>");
        kasseParagraph25OptionalContent2b.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent2b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent2b);

        OptionalContent kasseParagraph25OptionalContent2c = new OptionalContent();
        kasseParagraph25OptionalContent2c.setTitle("abweichende Kündigungsfrist");
        kasseParagraph25OptionalContent2c.setShortName("OPT-25-2c");
        kasseParagraph25OptionalContent2c.setContent("|(n) Die Kündigungsfrist beträgt abweichend von § 9 nach erfolgreich absolvierter Probezeit für beide Seiten 3 Monate zum Monatsende.<br/>");
        kasseParagraph25OptionalContent2c.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent2c.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent2c);

        OptionalContent kasseParagraph25OptionalContent3 = new OptionalContent();
        kasseParagraph25OptionalContent3.setTitle("erfolgsabhängige Prämie");
        kasseParagraph25OptionalContent3.setShortName("OPT-25-3");
        kasseParagraph25OptionalContent3.setContent("|(n) Der Arbeitnehmer erhält zusätzlich zu der Vergütung nach § 4 als weitere Vergütung bei Erreichen " +
                "der jeweiligen Voraussetzungen und/oder Ziele eine Prämie gemäß der gesonderten Anlage zu " +
                "diesem Vertrag.<br/>");
        kasseParagraph25OptionalContent3.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent3.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent3);

        OptionalContent kasseParagraph25OptionalContent5 = new OptionalContent();
        kasseParagraph25OptionalContent5.setTitle("Vermögenswirksame Leistungen");
        kasseParagraph25OptionalContent5.setShortName("OPT-25-5");
        kasseParagraph25OptionalContent5.setContent("|(n) Das Unternehmen gewährt auf Basis der Freiwilligkeit im Sinne von § 5 dieses Vertrages nach " +
                "Vollendung des ersten Jahres Betriebszugehörigkeit nach Eintritt [Vermögenswirksame Leistungen] EUR vermögenswirksame " +
                "Leistungen.<br/>");
        kasseParagraph25OptionalContent5.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent5.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent5Fields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent5Field1 = new OptionalContentField();
        kasseParagraph25OptionalContent5Field1.setFieldName("Vermögenswirksame Leistungen");
        kasseParagraph25OptionalContent5Field1.setFieldDescription("vermögenswirksame Leistungen");
        kasseParagraph25OptionalContent5Fields.add(kasseParagraph25OptionalContent5Field1);
        kasseParagraph25OptionalContent5.setOptionalContentFields(kasseParagraph25OptionalContent5Fields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent5);

        OptionalContent kasseParagraph25OptionalContent6a = new OptionalContent();
        kasseParagraph25OptionalContent6a.setTitle("Arbeitszeit/Schicht");
        kasseParagraph25OptionalContent6a.setShortName("OPT-25-6a");
        kasseParagraph25OptionalContent6a.setContent("|(n) Dem Arbeitnehmer ist bekannt, dass die Arbeit im Schicht- und Wechselschichtdienst im Zeitraum " +
                "von [frühestmöglicher Arbeits-/Schichtbeginn] Uhr bis [spätestmögliches Arbeits-/Schichtende] Uhr zu " +
                "leisten ist.<br/>");
        kasseParagraph25OptionalContent6a.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent6a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent6aFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent6aField1 = new OptionalContentField();
        kasseParagraph25OptionalContent6aField1.setFieldName("frühestmöglicher Arbeits-/Schichtbeginn");
        kasseParagraph25OptionalContent6aField1.setFieldDescription("Angabe frühestmöglicher Arbeits-/Schichtbeginn als Uhrzeit");
        kasseParagraph25OptionalContent6aFields.add(kasseParagraph25OptionalContent6aField1);
        OptionalContentField kasseParagraph25OptionalContent6aField2 = new OptionalContentField();
        kasseParagraph25OptionalContent6aField2.setFieldName("spätestmögliches Arbeits-/Schichtende");
        kasseParagraph25OptionalContent6aField2.setFieldDescription("Angabe spätestmögliches Arbeits-/Schichtende als Uhrzeit");
        kasseParagraph25OptionalContent6aFields.add(kasseParagraph25OptionalContent6aField2);
        kasseParagraph25OptionalContent6a.setOptionalContentFields(kasseParagraph25OptionalContent6aFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent6a);

        OptionalContent kasseParagraph25OptionalContent6b = new OptionalContent();
        kasseParagraph25OptionalContent6b.setTitle("Arbeitszeit Regel");
        kasseParagraph25OptionalContent6b.setShortName("OPT-25-6b");
        kasseParagraph25OptionalContent6b.setContent("|(n) In der Regel beträgt die Arbeitszeit täglich ([Wochentage]) [Wochenarbeitsstunden] Stunden. Die " +
                "Monatsarbeitszeit beträgt mindestens [Mindestmonatsarbeitsstundenanzahl] Stunden.<br/>");
        kasseParagraph25OptionalContent6b.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent6b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent6bFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent6bField1 = new OptionalContentField();
        kasseParagraph25OptionalContent6bField1.setFieldName("Wochentage");
        kasseParagraph25OptionalContent6bField1.setFieldDescription("Anzahl der Arbeitstage in der Woche");
        kasseParagraph25OptionalContent6bFields.add(kasseParagraph25OptionalContent6bField1);
        OptionalContentField kasseParagraph25OptionalContent6bField2 = new OptionalContentField();
        kasseParagraph25OptionalContent6bField2.setFieldName("Wochenarbeitsstunden");
        kasseParagraph25OptionalContent6bField2.setFieldDescription("Anzahl der Arbeitsstunden in der Woche");
        kasseParagraph25OptionalContent6bFields.add(kasseParagraph25OptionalContent6bField2);
        OptionalContentField kasseParagraph25OptionalContent6bField3 = new OptionalContentField();
        kasseParagraph25OptionalContent6bField3.setFieldName("Mindestmonatsarbeitsstundenanzahl");
        kasseParagraph25OptionalContent6bField3.setFieldDescription("Anzahl der Arbeitsstunden im Monat die mindestens geleistet werden müssen");
        kasseParagraph25OptionalContent6bFields.add(kasseParagraph25OptionalContent6bField3);
        kasseParagraph25OptionalContent6b.setOptionalContentFields(kasseParagraph25OptionalContent6bFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent6b);

        OptionalContent kasseParagraph25OptionalContent6d = new OptionalContent();
        kasseParagraph25OptionalContent6d.setTitle("Arbeitszeit Werkstudent");
        kasseParagraph25OptionalContent6d.setShortName("OPT-25-6d");
        kasseParagraph25OptionalContent6d.setContent("|(n) Um den Status als Werkstudent zu erfüllen, wird vereinbart, dass der Werkstudent während der " +
                "Studienzeit maximal 20 Stunden pro Woche arbeitet. Diese Arbeitszeit kann auf bis zu 40 Stunden " +
                "pro Woche in den Semesterferien erhöht werden (hierzu Bedarf es einer Bescheinigung über die " +
                "Dauer der Semsterferien).<br/>");
        kasseParagraph25OptionalContent6d.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent6d.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent6d);

        OptionalContent kasseParagraph25OptionalContent7a = new OptionalContent();
        kasseParagraph25OptionalContent7a.setTitle("Kündigungsfrist nach Probezeit");
        kasseParagraph25OptionalContent7a.setShortName("OPT-25-7a");
        kasseParagraph25OptionalContent7a.setContent("|(n) Die Kündigungsfrist beträgt abweichend von § 9 nach erfolgreich absolvierter Probezeit für beide " +
                "Seiten [Kündigungsfrist] Monate zum Monatsende.<br/>");
        kasseParagraph25OptionalContent7a.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent7a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent7aFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent7aField1 = new OptionalContentField();
        kasseParagraph25OptionalContent7aField1.setFieldName("Kündigungsfrist");
        kasseParagraph25OptionalContent7aField1.setFieldDescription("Angabe Kündigungsfrist in Monaten");
        kasseParagraph25OptionalContent7aFields.add(kasseParagraph25OptionalContent7aField1);
        kasseParagraph25OptionalContent7a.setOptionalContentFields(kasseParagraph25OptionalContent7aFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent7a);

        OptionalContent kasseParagraph25OptionalContent7b = new OptionalContent();
        kasseParagraph25OptionalContent7b.setTitle("Kündigungsfrist ab Beginn");
        kasseParagraph25OptionalContent7b.setShortName("OPT-25-7b");
        kasseParagraph25OptionalContent7b.setContent("|(n) Die Kündigungsfrist beträgt abweichend von § 9 für beide Seiten [Kündigungsfrist] Monate zum " +
                "Monatsende.<br/>");
        kasseParagraph25OptionalContent7b.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent7b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent7bFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent7bField1 = new OptionalContentField();
        kasseParagraph25OptionalContent7bField1.setFieldName("Kündigungsfrist");
        kasseParagraph25OptionalContent7bField1.setFieldDescription("Angabe Kündigungsfrist in Monaten");
        kasseParagraph25OptionalContent7bFields.add(kasseParagraph25OptionalContent7bField1);
        kasseParagraph25OptionalContent7b.setOptionalContentFields(kasseParagraph25OptionalContent7bFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent7b);

        OptionalContent kasseParagraph25OptionalContent8 = new OptionalContent();
        kasseParagraph25OptionalContent8.setTitle("Handynutzung");
        kasseParagraph25OptionalContent8.setShortName("OPT-25-8");
        kasseParagraph25OptionalContent8.setContent("|(n) Während der Arbeitszeit ist die Nutzung von privaten Handys zu privaten Zwecken untersagt.<br/>");
        kasseParagraph25OptionalContent8.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent8.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent8);

        OptionalContent kasseParagraph25OptionalContent9 = new OptionalContent();
        kasseParagraph25OptionalContent9.setTitle("Leitender Angestellter");
        kasseParagraph25OptionalContent9.setShortName("OPT-25-9");
        kasseParagraph25OptionalContent9.setContent("|(n) Der Arbeitnehmer ist nach übereinstimmender Auffassung der Vertragspartner als leitender " +
                "Angestellter im Sinne des § 5 Abs. 3 BetrVG einzustufen.<br/>");
        kasseParagraph25OptionalContent9.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent9.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent9);

        OptionalContent kasseParagraph25OptionalContent10a = new OptionalContent();
        kasseParagraph25OptionalContent10a.setTitle("im Falle einer Ablösung eines bestehenden Vertrages (gleicher Arbeitgeber):");
        kasseParagraph25OptionalContent10a.setShortName("OPT-25-10a");
        kasseParagraph25OptionalContent10a.setContent("|(n) Durch diesen Vertrag werden alle bisherigen mündlichen und schriftlichen Vereinbarungen " +
                "einschließlich etwaiger betrieblicher Übungen und nachwirkender Betriebsvereinbarungen insgesamt " +
                "abgelöst und damit gegenstandslos; dies gilt nicht für die unmittelbar und zwingend geltenden" +
                "Betriebsvereinbarungen und gültige Regelungsabreden. Die Parteien wollen die Bedingungen des " +
                "Arbeitsverhältnisses insgesamt neu festlegen.<br/>");
        kasseParagraph25OptionalContent10a.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent10a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent10a);

        OptionalContent kasseParagraph25OptionalContent10b = new OptionalContent();
        kasseParagraph25OptionalContent10b.setTitle("im Falle einer Übernahme von anderem Arbeitgeber aus der Gruppe:");
        kasseParagraph25OptionalContent10b.setShortName("OPT-25-10b");
        kasseParagraph25OptionalContent10b.setContent("|(n) Mit Abschluss dieses Vertrages heben die Parteien zugleich das bisher bestehende " +
                "Arbeitsverhältnis zwischen dem Arbeitnehmer und der [bisheriger Arbeitgeber] zum Stichtag des " +
                "Beginn des Arbeitsverhältnisses nach diesem Vertrag auf. Der Arbeitgeber handelt insoweit zugleich " +
                "in Vollmacht für den bisherigen Arbeitgeber. Die Parteien stellen klar, dass sich das Arbeitsverhältnis " +
                "ausschließlich nach den Bestimmungen dieses Vertrages richtet. Ein Besitzstand aus dem " +
                "bisherigen Arbeitsverhältnis wird nur und nur insoweit anerkannt, wenn und soweit dies in diesem " +
                "Vertrag ausdrücklich zugestanden wird.<br/>");
        kasseParagraph25OptionalContent10b.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent10b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent10bFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent10bField1 = new OptionalContentField();
        kasseParagraph25OptionalContent10bField1.setFieldName("bisheriger Arbeitgeber");
        kasseParagraph25OptionalContent10bField1.setFieldDescription("Angabe des bisherigen Arbeitgebers");
        kasseParagraph25OptionalContent10bFields.add(kasseParagraph25OptionalContent10bField1);
        kasseParagraph25OptionalContent10b.setOptionalContentFields(kasseParagraph25OptionalContent10bFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent10b);

        OptionalContent kasseParagraph25OptionalContent11a = new OptionalContent();
        kasseParagraph25OptionalContent11a.setTitle("Anlagen allgemein");
        kasseParagraph25OptionalContent11a.setShortName("OPT-25-11a");
        kasseParagraph25OptionalContent11a.setContent("|(n) Die Anlage zum Arbeitsvertrag [Bezeichnung Anlage] ist fester Bestandteil dieses Vertrages.<br/>");
        kasseParagraph25OptionalContent11a.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent11a.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        List<OptionalContentField> kasseParagraph25OptionalContent11aFields = new ArrayList<>();
        OptionalContentField kasseParagraph25OptionalContent11aField1 = new OptionalContentField();
        kasseParagraph25OptionalContent11aField1.setFieldName("Bezeichnung Anlage");
        kasseParagraph25OptionalContent11aField1.setFieldDescription("genaue Bezeichnung der Anlage");
        kasseParagraph25OptionalContent11aFields.add(kasseParagraph25OptionalContent11aField1);
        kasseParagraph25OptionalContent11a.setOptionalContentFields(kasseParagraph25OptionalContent11aFields);
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent11a);

        OptionalContent kasseParagraph25OptionalContent11b = new OptionalContent();
        kasseParagraph25OptionalContent11b.setTitle("Anlage gesonderte Vergütungsvereinbarung");
        kasseParagraph25OptionalContent11b.setShortName("OPT-25-11b");
        kasseParagraph25OptionalContent11b.setContent("|(n) Die gesondert abgeschlossene Vergütungsvereinbarung ist fester Bestandteil dieses Vertrages " +
                "und ergänzt dessen Bestimmungen.<br/>");
        kasseParagraph25OptionalContent11b.setContractVersionName(ContractVersionName.KASSE);
        kasseParagraph25OptionalContent11b.setVersionTemplateNames(VersionTemplateName.KEIN_TEMPLATE.name());
        kasseParagraph25OptionalContents.add(kasseParagraph25OptionalContent11b);
        kasseParagraph25.setOptionalContents(kasseParagraph25OptionalContents);
        kasseParagraphList.add(kasseParagraph25);
        return kasseParagraphList;
    }
}
