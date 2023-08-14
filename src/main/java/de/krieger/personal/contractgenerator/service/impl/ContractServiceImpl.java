package de.krieger.personal.contractgenerator.service.impl;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.*;
import de.krieger.personal.contractgenerator.repository.*;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import de.krieger.personal.contractgenerator.service.ContractService;
import de.krieger.personal.contractgenerator.service.CryptographyService;
import lombok.RequiredArgsConstructor;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final CryptographyService cryptographyService;
    private final ParagraphRepository paragraphRepository;
    private final OptionalContentRepository optionalContentRepository;
    private final SigneeRepository signeeRepository;

    @Override
    public Contract save(Contract contract) {
        return contractRepository.save(contract);
    }

    @Override
    @Transactional(readOnly = true)
    public Contract findById(Long id, String password, String shortName) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract does not exist with id: " + id));
        List<ContractParagraph> paragraphList = paragraphRepository.findAllByContract(contract);
        if (!paragraphList.isEmpty()) {
            for (ContractParagraph paragraph : paragraphList) {
                if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                    for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
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
            Collections.sort(paragraphList);
        }
        contract.setParagraphList(paragraphList);
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        return cryptographyService.decrypt(contract, authenticatedUser, password, shortName);
    }

    @Override
    public void deleteById(Long id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Contract does not exist with id: " + id));
        List<ContractParagraph> paragraphList = paragraphRepository.findAllByContract(contract);
        paragraphRepository.deleteAll(paragraphList);
        contractRepository.delete(contract);
    }

    @Override
    public Contract update(Contract contract, String password, String shortName) {
        List<ContractParagraph> contractParagraphList = contract.getParagraphList();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        for(Signee signee : signeeRepository.findAllByContract(contract)) {
            if (contract.getSigneeList().stream().noneMatch((signee1 -> signee.getId().equals(signee1.getId())))) {
                signeeRepository.delete(signee);
            }
        }
        for (ContractParagraph paragraph : contractParagraphList) {
            if (paragraph.getContentFields() != null && !paragraph.getContentFields().isEmpty()) {
                for (ContentField contentField : paragraph.getContentFields()) {
                    contentField.setContractParagraph(paragraph);
                    if (paragraph.isClean()) {
                        contentField.setId(null);
                    }
                }
            }
            if (paragraph.getOptionalContents() != null && !paragraph.getOptionalContents().isEmpty()) {
                for (OptionalContent optionalContent : paragraph.getOptionalContents()) {
                    optionalContent.setContractParagraph(paragraph);
                    if (paragraph.isClean()) {
                        optionalContent.setId(null);
                    }
                    if (optionalContent.getOptionalContentFields() != null && !optionalContent.getOptionalContentFields().isEmpty()) {
                        for (OptionalContentField optionalContentField : optionalContent.getOptionalContentFields()) {
                            optionalContentField.setOptionalContent(optionalContent);
                            if (paragraph.isClean()) {
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
            if (paragraph.isClean() || paragraphRepository.findById(paragraph.getId()).isPresent() && paragraphRepository.findById(paragraph.getId()).get().isClean()) {
                paragraph.setId(null);
                paragraph.setClean(false);
            }
        }
        contract.setParagraphList(this.cryptographyService.encrypt(contractParagraphList, username, password, shortName));
        if (contract.getParagraphList().stream().anyMatch(contractParagraph -> contractParagraph.getId() == null)) {
            List<ContractParagraph> contractParagraphList2 = paragraphRepository.findAllByContract(contractRepository.findById(contract.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Contract does not exist with id: " + contract.getId())));
            paragraphRepository.deleteAll(contractParagraphList2);
        }
        return contractRepository.save(contract);
    }
}
