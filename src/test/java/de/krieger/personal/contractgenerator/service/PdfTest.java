package de.krieger.personal.contractgenerator.service;

import com.lowagie.text.DocumentException;
import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.Candidate;
import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.repository.CandidateRepository;
import de.krieger.personal.contractgenerator.service.impl.PdfLayoutServiceImpl;
import de.krieger.personal.contractgenerator.service.impl.PdfServiceImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PdfTest {

    @InjectMocks
    private PdfLayoutServiceImpl pdfLayoutService;
    @InjectMocks
    private PdfServiceImpl pdfService;
    @Mock
    private CandidateRepository candidateRepository;

    @BeforeAll
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createFile() throws IOException, DocumentException {
        Candidate candidate = new Candidate();
        candidate.setFirstName("Max");
        candidate.setLastName("Mustermann");
        candidate.setStreet("Musterstraße");
        candidate.setStreetNumber("19");
        candidate.setZipCode("10437");
        candidate.setResidence("Musterhausen");
        candidate.setCandidateId(1L);
        candidate.setSalutation("Herrn");
        Mockito.when(candidateRepository.save(Mockito.any(Candidate.class))).thenReturn(candidate);
        Mockito.when(candidateRepository.findById(Mockito.any(Long.class))).thenReturn(Optional.of(candidate));
        candidateRepository.save(candidate);
        Contract contract = new Contract();
        contract.setTemplate("");
        contract.setLocation("Am Rondell 1\n" +
                "12529 Schönefeld");
        contract.setId(7L);
        contract.setFileName("test.pdf");
        contract.setCandidateId(candidate.getCandidateId());
        contract.setPlaceOfSignature("Schönefeld");
        contract.setContractVersionName(ContractVersionName.VERWALTUNG);
        contract.setVersionTemplateName(VersionTemplateName.KEIN_TEMPLATE);
        contract.setCreator("Krause");
//        pdfService.renderPdf("<html><body>Hallo</body></html>");
        final OutputStream outputStream = new FileOutputStream("LayoutTest.pdf");

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString("<html><body><h1>Hi</h1></body></html>");
        renderer.layout();
        renderer.createPDF(outputStream);
        outputStream.close();

    }
}
