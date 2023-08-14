package de.krieger.personal.contractgenerator.controller;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.*;
import de.krieger.personal.contractgenerator.repository.ContractRepository;
import de.krieger.personal.contractgenerator.repository.OptionalContentRepository;
import de.krieger.personal.contractgenerator.repository.ParagraphRepository;
import de.krieger.personal.contractgenerator.service.CryptographyService;
import de.krieger.personal.contractgenerator.service.DatabasePopulationService;
import de.krieger.personal.contractgenerator.service.VaultService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Collections;
import java.util.List;

@RestController
public class ParagraphController {

    private final ContractRepository contractRepository;
    private final ParagraphRepository paragraphRepository;
    private final OptionalContentRepository optionalContentRepository;
    private final CryptographyService cryptographyService;
    private final DatabasePopulationService dataBasePopulationService;

    @Autowired
    private VaultService vaultService;

    public ParagraphController(ContractRepository contractRepository, ParagraphRepository paragraphRepository, OptionalContentRepository optionalContentRepository, CryptographyService cryptographyService, DatabasePopulationService dataBasePopulationService) {
        this.contractRepository = contractRepository;
        this.paragraphRepository = paragraphRepository;
        this.optionalContentRepository = optionalContentRepository;
        this.cryptographyService = cryptographyService;
        this.dataBasePopulationService = dataBasePopulationService;
    }

    @GetMapping("/api/paragraphs/{id}")
    public ResponseEntity<?> getParagraph(@PathVariable Long id, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            if (paragraphRepository.findById(id).isPresent()) {
                ContractParagraph contractParagraph = paragraphRepository.findById(id).get();
                String username = SecurityContextHolder.getContext().getAuthentication().getName();
                String clientToken = vaultService.getClientToken(username, password);
                contractParagraph.setParagraphTitle(vaultService.decrypt(contractParagraph.getParagraphTitle(), contractParagraph.getContract().getId().toString(), shortName, clientToken));
                contractParagraph.setParagraphContent(vaultService.decrypt(contractParagraph.getParagraphContent(), contractParagraph.getContract().getId().toString(), shortName, clientToken));
                return ResponseEntity.ok(contractParagraph);
            } else {
                return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
            }
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    @GetMapping("/api/paragraphs/templates")
    public ResponseEntity<?> getAllTemplateParagraphs() {
        return ResponseEntity.ok(paragraphRepository.findAllByTemplate(true));
    }

    @GetMapping("/api/paragraphs/cleanParagraphs")
    public ResponseEntity<?> getAllCleanParagraphs() {
        return ResponseEntity.ok(paragraphRepository.findAllByClean(true));
    }

    @GetMapping("api/getCleanParagraphsByVersionAndTemplate")
    public ResponseEntity<?> getCleanParagraphsByVersionAndTemplate(@RequestParam(name = "contractVersionName") String contractVersionName, @RequestParam(name = "versionTemplateName") String versionTemplateName) {
        List<ContractParagraph> paragraphs = paragraphRepository.findAllByContractVersionNameAndClean(ContractVersionName.valueOf(contractVersionName), true);
        if (!paragraphs.isEmpty()) {
            for (ContractParagraph paragraph : paragraphs) {
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        if (!optionalContent.getContractVersionName().equals(ContractVersionName.valueOf(contractVersionName))) {
                            paragraph.getOptionalContents().remove(optionalContent);
                        } else if (optionalContent.getVersionTemplateNames().contains(VersionTemplateName.valueOf(versionTemplateName).name()) && !optionalContent.getVersionTemplateNames().equals(VersionTemplateName.KEIN_TEMPLATE.name())) {
                            optionalContent.setSelected(true);
                        }
                        if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                            List<OptionalContentField> optionalContentFields = optionalContent.getOptionalContentFields();
                            Collections.sort(optionalContentFields);
                            optionalContent.setOptionalContentFields(optionalContentFields);
                        }
                    }
                    List<OptionalContent> optionalContents = paragraph.getOptionalContents();
                    Collections.sort(optionalContents);
                    paragraph.setOptionalContents(optionalContents);
                }
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    List<ContentField> contentFields = paragraph.getContentFields();
                    Collections.sort(contentFields);
                    paragraph.setContentFields(contentFields);
                }
            }
            Collections.sort(paragraphs);
            return ResponseEntity.ok(paragraphs);
        } else {
            return new ResponseEntity<>("Es konnten keine Paragraphen mit der Version " + ContractVersionName.valueOf(contractVersionName).getVersionName() + " und dem Template " + VersionTemplateName.valueOf(versionTemplateName).getTemplateName() + " gefunden werden.", HttpStatus.NOT_FOUND);
       }
    }

    @PostMapping("/api/saveParagraph")
    public ResponseEntity<?> saveParagraph(@RequestBody ContractParagraph contractParagraph, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            String clientToken = vaultService.getClientToken(authenticatedUser, password);
            contractParagraph.setParagraphTitle(vaultService.encrypt(contractParagraph.getParagraphTitle(), contractParagraph.getContract().getId().toString(), shortName, clientToken));
            contractParagraph.setParagraphContent(vaultService.encrypt(contractParagraph.getParagraphContent(), contractParagraph.getContract().getId().toString(), shortName, clientToken));
            return ResponseEntity.ok(paragraphRepository.save(contractParagraph));
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    @PostMapping("/api/saveParagraphList")
    public ResponseEntity<?> saveParagraphList(@RequestBody List<ContractParagraph> contractParagraphList, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            for (ContractParagraph paragraph : contractParagraphList) {
                if (paragraph.getId() != null) {
                    paragraph.setId(null);
                }
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    for (ContentField contentField : paragraph.getContentFields()) {
                        contentField.setContractParagraph(paragraph);
                        contentField.setId(null);
                    }
                }
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        optionalContent.setContractParagraph(paragraph);
                        optionalContent.setId(null);
                        if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                            for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                                optionalContentField.setOptionalContent(optionalContent);
                                optionalContentField.setId(null);
                            }
                        }
                    }
                }
            }
            List<ContractParagraph> encryptedParagraphs;
            try {
                encryptedParagraphs = cryptographyService.encrypt(contractParagraphList, authenticatedUser, password, shortName);
            } catch (HttpClientErrorException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
            }
            return ResponseEntity.ok(paragraphRepository.saveAll(encryptedParagraphs));
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    @PutMapping("/api/updateParagraphList")
    public ResponseEntity<?> updateParagraphList(@RequestBody List<ContractParagraph> contractParagraphList, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            if (contractParagraphList.stream().anyMatch(contractParagraph -> contractParagraph.getId() == null)) {
                ContractParagraph contractParagraph2 = contractParagraphList.stream().filter(contractParagraph -> contractParagraph.getId() == null).findFirst().get();
                List<ContractParagraph> contractParagraphList2 = paragraphRepository.findAllByContract(contractRepository.findById(contractParagraph2.getContract().getId())
                        .orElseThrow(() -> new ResourceNotFoundException("Contract does not exist with id: " + contractParagraph2.getContract().getId())));
                paragraphRepository.deleteAll(contractParagraphList2);
            }
            for (ContractParagraph paragraph : contractParagraphList) {
                if ((paragraph.getId() != null && paragraph.isClean()) || paragraphRepository.findById(paragraph.getId()).isPresent() && paragraphRepository.findById(paragraph.getId()).get().isClean()) {
                    paragraph.setId(null);
                }
                if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                    for (ContentField contentField : paragraph.getContentFields()) {
                        contentField.setContractParagraph(paragraph);
                        if (paragraph.getId() != null && paragraph.isClean()) {
                            contentField.setId(null);
                        }
                    }
                }
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                        optionalContent.setContractParagraph(paragraph);
                        if (paragraph.getId() != null && paragraph.isClean()) {
                            optionalContent.setId(null);
                        }
                        if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                            for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                                optionalContentField.setOptionalContent(optionalContent);
                                if (paragraph.getId() != null && paragraph.isClean()) {
                                    optionalContentField.setId(null);
                                }
                            }
                        }
                    }
                    if (contractParagraphList.stream().noneMatch(contractParagraph -> contractParagraph.getId() == null) && !optionalContentRepository.findAllByContractParagraphAndModified(paragraph, true).isEmpty()) {
                        for (OptionalContent optionalContent : optionalContentRepository.findAllByContractParagraphAndModified(paragraph, true)) {
                            if (paragraph.getOptionalContents().stream().noneMatch(optionalContent1 -> optionalContent.getId().equals(optionalContent1.getId()))) {
                                paragraph.setParagraphContent(paragraph.getParagraphContent().replaceAll("\\[" + optionalContent.getShortName() + "]", ""));
                                optionalContentRepository.delete(optionalContent);
                            }
                        }
                    }
                }
            }
            List<ContractParagraph> encryptedParagraphs;
            try {
                encryptedParagraphs = cryptographyService.encrypt(contractParagraphList, authenticatedUser, password, shortName);
            } catch (HttpClientErrorException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
            }
            return ResponseEntity.ok(paragraphRepository.saveAll(encryptedParagraphs));
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    @GetMapping("/api/populateDatabase")
    public ResponseEntity<?> populateDatabase() {
        return ResponseEntity.ok(paragraphRepository.saveAll(dataBasePopulationService.populateDatabase()));
    }

    @GetMapping("/api/updateTemplates")
    public ResponseEntity<?> updateTemplates() {
        return ResponseEntity.ok(paragraphRepository.saveAll(dataBasePopulationService.updateTemplates()));
    }
}
