package de.krieger.personal.contractgenerator.service.impl;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;

import de.krieger.personal.contractgenerator.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.repository.CandidateRepository;
import de.krieger.personal.contractgenerator.repository.CompanyRepository;
import de.krieger.personal.contractgenerator.repository.ParagraphRepository;
import de.krieger.personal.contractgenerator.service.PdfLayoutService;
import rst.pdfbox.layout.elements.Document;
import rst.pdfbox.layout.elements.Paragraph;
import rst.pdfbox.layout.elements.VerticalSpacer;
import rst.pdfbox.layout.elements.render.ColumnLayout;
import rst.pdfbox.layout.elements.render.RenderContext;
import rst.pdfbox.layout.elements.render.RenderListener;
import rst.pdfbox.layout.elements.render.VerticalLayoutHint;
import rst.pdfbox.layout.text.Alignment;
import rst.pdfbox.layout.text.BaseFont;
import rst.pdfbox.layout.text.Indent;
import rst.pdfbox.layout.text.Position;
import rst.pdfbox.layout.text.SpaceUnit;
import rst.pdfbox.layout.text.TextFlow;
import rst.pdfbox.layout.text.TextFlowUtil;
import rst.pdfbox.layout.text.TextSequenceUtil;
import rst.pdfbox.layout.util.CompatibilityHelper;
import rst.pdfbox.layout.util.WordBreakerFactory;

@Service
public class PdfLayoutServiceImpl implements PdfLayoutService {

    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CandidateRepository candidateRepository;

    @Override
    public Document createPdf(Contract contract) throws IOException {
        String bulletOdd = CompatibilityHelper.getBulletCharacter(1);
        System.setProperty(WordBreakerFactory.WORD_BREAKER_CLASS_PROPERTY, WordBreakerFactory.LEGACY_WORD_BREAKER_CLASS_NAME);
        Document pdf = new Document(40, 60, 40, 60);
        pdf.addRenderListener(new RenderListener() {
            @Override
            public void beforePage(RenderContext renderContext) throws IOException {

            }

            @Override
            public void afterPage(RenderContext renderContext) throws IOException {
                ContractVersionName version = contract.getContractVersionName();
                VersionTemplateName template = contract.getVersionTemplateName();
                String breadcrumb = "AV " + version.getAbbreviation() + version.getCreated() + " " + template.getAbbreviation() + " " + contract.getCreator().substring(0, 3);
                String content = String.format(breadcrumb);
                TextFlow text = TextFlowUtil.createTextFlow(content, 8,
                        PDType1Font.HELVETICA);
                float offset = renderContext.getPageFormat().getMarginLeft()
                        + TextSequenceUtil.getOffset(text,
                        renderContext.getWidth(), Alignment.Right);
                text.drawText(renderContext.getContentStream(), new Position(
                        offset, 30), Alignment.Right, null);
            }
        });
        Paragraph layoutParagraph = new Paragraph();

        if (contract.getTemplate() == null) {
            layoutParagraph.addMarkup("Das Template ist leer", 10, BaseFont.Helvetica);
            pdf.add(layoutParagraph);
            final OutputStream outputStream = new FileOutputStream(contract.getFileName());
            pdf.save(outputStream);
            return pdf;
        }
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
        StringBuilder template = new StringBuilder(contract.getTemplate());
        List<ContractParagraph> paragraphList = contract.getParagraphList();
        if (contract.getParagraphList() != null && !contract.getParagraphList().isEmpty()) {
            Collections.sort(paragraphList);
            for (ContractParagraph paragraph : paragraphList) {
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    for (ContentField contentField : paragraph.getContentFields()) {
                        paragraph.setParagraphContent(paragraph.getParagraphContent().replaceAll("\\[" + contentField.getFieldName() + "]", contentField.getFieldValue()));
                    }
                }

                template.append("|<c>*§ ").append(paragraph.getParagraphNumber()).append(" ").append(paragraph.getParagraphTitle()).append("*\n\n|");
                if (paragraph.getParagraphContent() != null) {
                    template.append(paragraph.getParagraphContent());
                }
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        if (optionalContent.isSelected()) {
                            if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                                for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                                    optionalContent.setContent(optionalContent.getContent().replaceAll("\\[" +optionalContentField.getFieldName() + "]", optionalContentField.getFieldValue()));
                                }
                            }
                            template = new StringBuilder(template.toString().replaceAll("\\[" + optionalContent.getShortName() + "]", optionalContent.getContent()));
                        }
                    }
                }
            }
        }
        template.append("\n\n|[placeOfSignature], [dateOfSignature]\n\n\n\n");
        if (company.getCompanyName() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[companyName\\]", String.valueOf(company.getCompanyName())));
        }
        if (contract.getLocation() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[companyLocation\\]", String.valueOf(contract.getLocation())));
        }
        if (candidate.getSalutation() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[salutation\\]", String.valueOf(candidate.getSalutation())));
        }
        if (candidate.getDegree() != null) {
            if (candidate.getDegree().equals("-")) {
                template = new StringBuilder(template.toString().replaceAll("\\[degree\\]", ""));
            } else {
                template = new StringBuilder(template.toString().replaceAll("\\[degree\\]", String.valueOf(candidate.getDegree())));
            }
        }
        if (candidate.getFirstName() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[firstName\\]", String.valueOf(candidate.getFirstName())));
        }
        if (candidate.getLastName() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[lastName\\]", String.valueOf(candidate.getLastName())));
        }
        if (candidate.getStreet() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[street\\]", String.valueOf(candidate.getStreet())));
        }
        if (candidate.getStreetNumber() != null && !candidate.getStreetNumber().equals("")) {
            template = new StringBuilder(template.toString().replaceAll("\\[streetNumber\\]", String.valueOf(candidate.getStreetNumber())));
        }
        if (candidate.getZipCode() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[zipCode\\]", String.valueOf(candidate.getZipCode())));
        }
        if (candidate.getResidence() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[residence\\]", String.valueOf(candidate.getResidence())));
        }
        if (company.getShortName() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[shortName\\]", String.valueOf(company.getShortName())));
        }
        if (contract.getPlaceOfSignature() != null) {
            template = new StringBuilder(template.toString().replaceAll("\\[placeOfSignature\\]", String.valueOf(contract.getPlaceOfSignature())));
        }
        if (contract.getDateOfSignature() != null) {
            DateFormat formatter = new SimpleDateFormat("dd.MM.yyyy");
            ZoneId zoneId = ZoneId.systemDefault();
            template = new StringBuilder(template.toString().replaceAll("\\[dateOfSignature\\]", formatter.format(Date.from(contract.getDateOfSignature().atStartOfDay(zoneId).toInstant()))));
        }
        // template = new StringBuilder(template.toString().replaceAll("\\[contractVersionName\\]", String.valueOf(contract.getContractVersionName().getVersionName())));

        template = new StringBuilder(template.toString().replaceAll("\\[OPT-\\d+-\\d+.*]", ""));
        int occurences = StringUtils.countMatches(template, "(n)");
        for (int i = 0; i < occurences; i++) {
            int sectionNumber = i + 3;
            template = new StringBuilder(template.toString().replaceFirst("\\(n\\)", "(" + sectionNumber + ")"));
        }
        template = new StringBuilder(template.toString().replaceAll("\\[", ""));
        template = new StringBuilder(template.toString().replaceAll("\\]", ""));
        contract.setTemplate(template.toString());
        List<String> sectionList = new ArrayList<>(Arrays.asList(contract.getTemplate().split("\\|")));
        for (String section : sectionList) {
            if (section.matches("(?s).*<\\d+>.*")) {
                Long indentionValue = Long.parseLong(section.substring(section.indexOf("<") + 1, section.indexOf(">")));
                if (section.contains("$")) {
                    layoutParagraph.add(new Indent("o   ", indentionValue, SpaceUnit.pt, 10, PDType1Font.HELVETICA, Alignment.Right));
                    section = section.replaceAll("\\$", "");
                } else if (section.contains("#")) {
                    layoutParagraph.add(new Indent(bulletOdd, indentionValue, SpaceUnit.pt, 10, PDType1Font.HELVETICA, Alignment.Center));
                    section = section.replaceAll("#", "");
                } else {
                    layoutParagraph.add(new Indent(indentionValue, SpaceUnit.pt));
                }
                section = section.replaceAll("<\\d+>", "");
                layoutParagraph.addMarkup(section, 10, BaseFont.Helvetica);
            } else if (section.matches("(?s).*<.>.*")) {
                layoutParagraph = new Paragraph();
                if (section.substring(section.indexOf("<") + 1, section.indexOf(">")).equals("c")) {
                    section = section.replaceAll("<c>", "");
                    layoutParagraph.addMarkup(section, 10, BaseFont.Helvetica);
                    pdf.add(layoutParagraph, VerticalLayoutHint.CENTER);
                    pdf.add(new VerticalSpacer(5));
                    layoutParagraph = new Paragraph();
                } else if (section.substring(section.indexOf("<") + 1, section.indexOf(">")).equals("t")) {
                    section = section.replaceAll("<t>", "");
                    layoutParagraph.addMarkup(section, 20, BaseFont.Helvetica);
                    pdf.add(layoutParagraph, VerticalLayoutHint.CENTER);
                    pdf.add(new VerticalSpacer(5));
                    layoutParagraph = new Paragraph();
                } else if (section.substring(section.indexOf("<") + 1, section.indexOf(">")).equals("s")) {
                    section = section.replaceAll("<s>", "");
                    layoutParagraph.setAlignment(Alignment.Left);
                    layoutParagraph.addMarkup(section, 8, BaseFont.Helvetica);
                    pdf.add(layoutParagraph, VerticalLayoutHint.RIGHT);
                    pdf.add(new VerticalSpacer(5));
                    layoutParagraph = new Paragraph();
                } else if (section.substring(section.indexOf("<") + 1, section.indexOf(">")).equals("q")) {
                    section = section.replaceAll("<q>", "");
                    layoutParagraph.setAlignment(Alignment.Justify);
                    layoutParagraph.addMarkup(section, 10, BaseFont.Helvetica);
                    pdf.add(layoutParagraph);
                    layoutParagraph = new Paragraph();
                }
            } else {
                layoutParagraph.addMarkup(section, 10, BaseFont.Helvetica);
                pdf.add(layoutParagraph);
            }
        }
        pdf.add(new VerticalSpacer(8));
        pdf.add(new ColumnLayout(2, 5));
        String line = "*__                                                                       __*\n";

        for(Signee signee : contract.getSigneeList()) {
            Paragraph left = new Paragraph();
            left.setLineSpacing(2.0f);
            left.setMaxWidth(pdf.getPageWidth()/2);
            left.addMarkup(line + signee.getName() + ", " + signee.getPosition(), 10, BaseFont.Helvetica);
            pdf.add(left);
        }


        pdf.add(ColumnLayout.NEWCOLUMN);

        Paragraph right = new Paragraph();
        right.setLineSpacing(2.0f);
        right.setMaxWidth(pdf.getPageWidth()/2);
        right.addMarkup(line + candidate.getFirstName() + " " + candidate.getLastName(), 10, BaseFont.Helvetica);
        pdf.add(right);

        return pdf;
    }
}
