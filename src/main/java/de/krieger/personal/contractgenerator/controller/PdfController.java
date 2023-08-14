package de.krieger.personal.contractgenerator.controller;


import com.github.jhonnymertz.wkhtmltopdf.wrapper.Pdf;
import com.github.jhonnymertz.wkhtmltopdf.wrapper.params.Param;
import com.lowagie.text.DocumentException;
import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.repository.ContractRepository;
import de.krieger.personal.contractgenerator.repository.ParagraphRepository;
import de.krieger.personal.contractgenerator.service.ContractService;
import de.krieger.personal.contractgenerator.service.PdfLayoutService;
import de.krieger.personal.contractgenerator.service.PdfService;
import de.krieger.personal.contractgenerator.service.VaultService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import rst.pdfbox.layout.elements.Document;

import java.io.*;

@Controller
public class PdfController {


    private final PdfLayoutService pdfLayoutService;
    private final PdfService pdfService;
    private final ContractRepository contractRepository;
    private final ParagraphRepository paragraphRepository;
    private final ContractService contractService;
    private final VaultService vaultService;

    public PdfController(
            PdfLayoutService pdfLayoutService,
            PdfService pdfService,
            ContractRepository contractRepository,
            ParagraphRepository paragraphRepository,
            ContractService contractService,
            VaultService vaultService) {
        this.pdfLayoutService = pdfLayoutService;
        this.pdfService = pdfService;
        this.contractRepository = contractRepository;
        this.contractService = contractService;
        this.paragraphRepository = paragraphRepository;
        this.vaultService = vaultService;
    }

    @GetMapping("/api/createPdf/{id}")
    @Transactional(readOnly = true)
    public ResponseEntity<?> createPdf(@PathVariable Long id, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) throws IOException {
        if (contractRepository.findById(id).isPresent()) {
            Contract pdfContract = contractService.findById(id, password, shortName);
            pdfContract.setParagraphList(paragraphRepository.findAllByContract(pdfContract));
            Document pdf = pdfLayoutService.createPdf(contractRepository.save(pdfContract));
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            pdf.save(baos);
            byte[] bytes = baos.toByteArray();
            ByteArrayResource resource = new ByteArrayResource(bytes);
            return ResponseEntity.ok().contentLength(bytes.length).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
        } else {
            return ResponseEntity.ok(ResponseEntity.notFound());
        }
    }


    @GetMapping("/pdf/{id}")
    public ResponseEntity<?> generatePdfFromHtml(@PathVariable Long id, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) throws IOException, DocumentException, InterruptedException {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            if (contractRepository.findById(id).isPresent()) {
                Contract pdfContract;
                try {
                    pdfContract = contractService.findById(id, password, shortName);
                } catch (HttpStatusCodeException e) {
                    return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
                }
                pdfContract.setParagraphList(paragraphRepository.findAllByContract(pdfContract));

                String html = pdfService.parseThymeleafTemplate(pdfContract);

                Pdf pdf = new Pdf();
                pdf.addPageFromString(html);
                pdf.addParam(new Param("--margin-top", "2.5cm"),
                        new Param("--margin-bottom", "1.27cm"),
                        new Param("--margin-left", "2.5cm"),
                        new Param("--margin-right", "2.5cm"),
                        new Param("--disable-smart-shrinking"),
                        new Param("--footer-font-size", "7"),
                        new Param("--footer-left", "AV " + pdfContract.getContractVersionName().getAbbreviation() + pdfContract.getContractVersionName().getCreated() + " " + pdfContract.getVersionTemplateName().getAbbreviation() + " " + pdfContract.getCreator().substring(0, 3))

                );
                byte[] bytes = pdf.getPDF();
                ByteArrayResource resource = new ByteArrayResource(bytes);
//            pdf.saveAs("test12345.pdf");
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ITextRenderer renderer = new ITextRenderer();
//
//            renderer.setDocumentFromString(html);
//            renderer.layout();
//            renderer.createPDF(baos);
//            byte[] bytes = baos.toByteArray();
//            ByteArrayResource resource = new ByteArrayResource(bytes);
                return ResponseEntity.ok().contentLength(bytes.length).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);
//            return ResponseEntity.ok(html);
            } else {
                return new ResponseEntity<String>("Not found", HttpStatus.NOT_FOUND);
            }
        } else {
                String response = vaultService.getClientToken(authenticatedUser, password);
                // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
                return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
            }
    }

//    @GetMapping("/test")
//    public ModelAndView createHtml(ModelAndView modelAndView) {
//        Contract pdfContract = new Contract();
//        Candidate pdfCandidate = new Candidate();
//        pdfCandidate.setFirstName("Max");
//        pdfCandidate.setLastName("Mustermann");
//        pdfCandidate.setSalutation("Herrn");
//        ContractParagraph contractParagraph1 = new ContractParagraph();
//        contractParagraph1.setParagraphNumber("§ 1");
//        contractParagraph1.setParagraphTitle("Tätigkeit, Befristung, Probezeit, Bedingungen");
//        ContractParagraph contractParagraph2 = new ContractParagraph();
//        contractParagraph2.setParagraphNumber("§ 2");
//        contractParagraph2.setParagraphTitle("Arbeitsort, Aufgaben");
//        ContentField contentField  = new ContentField();
//        List<ContentField> contentFieldList = new ArrayList<>();
//        contentFieldList.add(contentField);
//        contractParagraph1.setContentFields(contentFieldList);
//
////        Contract contract = contractRepository.findById(0).get();
////        System.out.println(contract);
////        Contract pdfContract = contractRepository.findById(contract.getId()).get();
////        pdfContract.setParagraphList(paragraphRepository.findAllByContract(pdfContract));
////        System.out.println(pdfContract);
//        modelAndView.addObject("contract",pdfContract);
//        modelAndView.addObject("contractParagraph1",contractParagraph1);
//        modelAndView.addObject("contractParagraph2",contractParagraph2);
//        modelAndView.addObject("candidate", pdfCandidate);
//        modelAndView.setViewName("pdf");
//        return modelAndView;
//    }


}
