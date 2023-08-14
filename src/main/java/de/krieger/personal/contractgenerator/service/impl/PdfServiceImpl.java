package de.krieger.personal.contractgenerator.service.impl;

import com.lowagie.text.DocumentException;
import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.model.*;
import de.krieger.personal.contractgenerator.model.DTO.InsertableContractParagraph;
import de.krieger.personal.contractgenerator.repository.CandidateRepository;
import de.krieger.personal.contractgenerator.repository.CompanyRepository;
import de.krieger.personal.contractgenerator.service.PdfService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

@Service
public class PdfServiceImpl implements PdfService {
    private static final String PDF_RESOURCES = "/pdf-resources/";
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CandidateRepository candidateRepository;

//    public File generatePdf() throws IOException, DocumentException {
//        Context context = getContext();
//        String html = loadAndFillTemplate(context);
//        return renderPdf(html);
//    }
//
//
//    public File renderPdf(String html) throws IOException, DocumentException {
//        File file = File.createTempFile("pdf_test", ".pdf");
//        OutputStream outputStream = new FileOutputStream(file);
//        ITextRenderer renderer = new ITextRenderer(20f * 4f / 3f, 20);
//        renderer.setDocumentFromString(html);
//        renderer.layout();
//        renderer.createPDF(outputStream);
//        outputStream.close();
//        file.deleteOnExit();
//        return file;
//    }

    @Override
    public String parseThymeleafTemplate(Contract contract) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setCacheable(false);
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();


        Company company = new Company();
        if (contract.getCompanyId() != null) {
            if (companyRepository.findById(contract.getCompanyId()).isPresent()) {
                company = companyRepository.findById(contract.getCompanyId()).get();
            }
        }
        Candidate candidate = new Candidate();
        if (contract.getCandidateId() != null) {
            if (candidateRepository.findById(contract.getCandidateId()).isPresent()) {
                candidate = candidateRepository.findById(contract.getCandidateId()).get();
            }
        }
        String dateOfSignature = "";
        if (contract.getDateOfSignature() != null) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            ZoneId zoneId = ZoneId.systemDefault();
            dateOfSignature = formatter.format(Date.from(contract.getDateOfSignature().atStartOfDay(zoneId).toInstant()));
        }
        String placeOfSignature = "";
        if (contract.getPlaceOfSignature() != null) {
            placeOfSignature = contract.getPlaceOfSignature();
        }
        List<ContractParagraph> paragraphList = contract.getParagraphList();
        List<InsertableContractParagraph> insertableContractParagraphs = new ArrayList<>();
        if (contract.getParagraphList() != null && !contract.getParagraphList().isEmpty()) {
            Collections.sort(paragraphList);
            for (ContractParagraph paragraph : paragraphList) {
                InsertableContractParagraph insertableContractParagraph = new InsertableContractParagraph();
                insertableContractParagraph.setParagraphNumber(paragraph.getParagraphNumber());
                insertableContractParagraph.setParagraphNumberAsInt(paragraph.getParagraphNumber().length() == 3 ? Integer.parseInt(paragraph.getParagraphNumber().substring(0, 2)) : Integer.parseInt(paragraph.getParagraphNumber()));
                insertableContractParagraph.setParagraphTitle(paragraph.getParagraphTitle());
                StringBuilder template = new StringBuilder();
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    for (ContentField contentField : paragraph.getContentFields()) {
                        paragraph.setParagraphContent(paragraph.getParagraphContent().replaceAll("\\[" + contentField.getFieldName() + "]", contentField.getFieldValue()));
                    }
                }
                if (paragraph.getParagraphContent() != null) {
                    template.append(paragraph.getParagraphContent());
                }
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        if (optionalContent.isSelected()) {
                            if(contract.getContractVersionName().equals(ContractVersionName.KASSE) && optionalContent.getTitle().equals("Mankogeld")){
                                paragraphList.get(21).setParagraphTitle("Mankoabrede");
                                paragraphList.get(21).setParagraphContent("(1) Der Arbeitnehmer haftet für die von ihm verschuldeten Kassenfehlbestände nach den gesetzlichen Vorschriften.<br/><br/>" +
                                        "|(2) Unabhängig von Abs. 1 haftet der Arbeitnehmer verschuldensunabhängig für jeden Kassenfehlbestand, dies jedoch je Kalenderjahr maximal in der Höhe einer jährlichen Mankoprämie nach § 4 Abs. 1).<br/>" +
                                        "|(3) Der Arbeitgeber ist berechtigt, seine Ansprüche aus der Mankoabrede unter Beachtung der gesetzlichen Vorschriften mit Vergütungsansprüchen zu verrechnen.<br/><br/>");
                            }
                            if (optionalContent.getTitle().equals("früheres Eintrittsdatum möglich")){
                                paragraphList.get(24).setParagraphContent(paragraphList.get(24).getParagraphContent().replaceAll("\\[OPT-25-0]", "oder früher"));
                            }
                            if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                                for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                                    if (optionalContentField.getFieldName().equals("Treueprämie") && (optionalContentField.getFieldValue().equals("") || optionalContentField.getFieldValue().isEmpty())) {
                                        optionalContent.setContent(optionalContent.getContent().replaceAll("Besteht das Arbeitsverhältnis bei Beginn des 13. Beschäftigungsmonats ungekündigt fort, " +
                                                "so zahlt der Arbeitgeber an den Arbeitnehmer einmalig eine Treueprämie in Höhe von" +
                                                "<b> \\[Treueprämie] EUR </b>brutto. Die Prämie dient ausschließlich der Honorierung der Betriebstreue. " +
                                                "Daher besteht kein, auch kein anteiliger Anspruch des Arbeitnehmers auf die Treueprämie, wenn das " +
                                                "Arbeitsverhältnis innerhalb der ersten zwölf Beschäftigungsmonate von einer der Parteien gekündigt wird; " +
                                                "auf den Anlass und den Grund der Kündigung kommt es hierbei nicht an. Auch ein Anspruch auf Schadensersatz " +
                                                "bei Nichtentstehung des Anspruchs infolge einer Kündigung besteht nicht. Eine Kündigung, die später durch die " +
                                                "Parteien oder ein Gericht für unwirksam erklärt wird, gilt nicht als Kündigung im Sinne dieser Klausel. " +
                                                "Die entstandene Treueprämie wird mit dem nächsten regulären Abrechnungslauf des Arbeitgebers gezahlt, " +
                                                "der auf die Vollendung des zwölften Beschäftigungsmonats folgt.", ""));
                                    } else if (optionalContentField.getFieldName().equals("Prämie/-n") && (optionalContentField.getFieldValue().equals("") || optionalContentField.getFieldValue().isEmpty())) {
                                        optionalContent.setContent(optionalContent.getContent().replaceAll("\\S+\\[Prämie/-n]<br/>", ""));
                                    }
                                    optionalContent.setContent(optionalContent.getContent().replaceAll("\\[" +optionalContentField.getFieldName() + "]", optionalContentField.getFieldValue()));
                                }
                            }
                            template = new StringBuilder(template.toString().replaceAll("\\[" + optionalContent.getShortName() + "]", optionalContent.getContent()));
                        }
                    }
                }
                template = new StringBuilder(template.toString().replaceAll("\\[OPT-\\d+-\\d+[a-z]?]", ""));
                int occurences = StringUtils.countMatches(template, "(n)");
                for (int i = 0; i < occurences; i++) {
                    int sectionNumber = i + 3;
                    template = new StringBuilder(template.toString().replaceFirst("\\(n\\)", "(" + sectionNumber + ")"));
                }
                template = new StringBuilder(template.toString().replaceAll("\\[", ""));
                template = new StringBuilder(template.toString().replaceAll("]", ""));
                insertableContractParagraph.setParagraphPassages(new ArrayList<>(Arrays.asList(template.toString().split("\\|"))));
                insertableContractParagraph.setFirstPassage(insertableContractParagraph.getParagraphPassages().get(0));
                List<String> otherPassages = insertableContractParagraph.getParagraphPassages();
                otherPassages.remove(0);
                insertableContractParagraph.setParagraphPassages(otherPassages);
                insertableContractParagraphs.add(insertableContractParagraph);
            }
        }
        String candidateFirstRow = (candidate.getSalutation().equals("divers")? "" : candidate.getSalutation() + " ")
                + (candidate.getDegree().equals("-")||candidate.getDegree() == null? "" : candidate.getDegree() + " ")
                + candidate.getFirstName()
                + " "
                + candidate.getLastName();
        String candidateSecondRow;
        if (candidate.getStreetNumber().equals("")) {
            candidateSecondRow = candidate.getResidence().replaceAll("\n", "<br/>");
        } else {
            candidateSecondRow = candidate.getStreet()
                    + " "
                    + candidate.getStreetNumber()
                    + ", "
                    + candidate.getZipCode()
                    + " "
                    + candidate.getResidence();
        }

        List<Signee> signees = contract.getSigneeList();
        Signee firstSignee = signees.get(0);
        signees.remove(0);
        context.setVariable("insertableContractParagraphs", insertableContractParagraphs);
        context.setVariable("dateOfSignature", dateOfSignature);
        context.setVariable("placeOfSignature", placeOfSignature);
        context.setVariable("candidateFirstRow", candidateFirstRow);
        context.setVariable("candidateSecondRow", candidateSecondRow);
        context.setVariable("company", company);
        context.setVariable("contract", contract);
        context.setVariable("firstSignee", firstSignee);
        context.setVariable("signees", signees);
        context.setVariable("candidate", candidate);

        return templateEngine.process("pdf", context);
    }

    @Override
    public String parseThymeleafTemplate2() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        Context context = new Context();
        context.setVariable("to", "Baeldung");

        return templateEngine.process("pdf", context);
    }




//    private Context getContext() {
//        Context context = new Context();
//        Company company = new Company();
//        if (contract.getCompanyId() != null) {
//            if (companyRepository.findById(contract.getCompanyId()).isPresent()) {
//                company = companyRepository.findById(contract.getCompanyId()).get();
//            }
//        }
//        Candidate candidate = new Candidate();
//        if (contract.getCandidateId() != null) {
//            if (candidateRepository.findById(contract.getCandidateId()).isPresent()) {
//                candidate = candidateRepository.findById(contract.getCandidateId()).get();
//            }
//        }
//        context.setVariable("companyName", company.getCompanyName());
//        return context;
//    }

//    private String loadAndFillTemplate(Context context) {
//        return templateEngine.process("pdf_test", context);
//    }


}
