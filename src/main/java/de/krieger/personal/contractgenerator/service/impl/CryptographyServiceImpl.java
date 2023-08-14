package de.krieger.personal.contractgenerator.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.ContractParagraph;
import de.krieger.personal.contractgenerator.service.CryptographyService;
import de.krieger.personal.contractgenerator.service.VaultService;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CryptographyServiceImpl implements CryptographyService {
    
    private final VaultService vaultService;

    @Override
    public List<ContractParagraph> encrypt(List<ContractParagraph> contractParagraphList, String username, String password, String shortName) {
        if (contractParagraphList.isEmpty()) {
            return contractParagraphList;
        }
        String clientToken = vaultService.getClientToken(username, password);
        contractParagraphList.forEach(contractParagraph -> {
            String encryptionKey = contractParagraph.getContract().getId().toString();
            vaultService.createKey(encryptionKey, shortName);
            contractParagraph.setParagraphContent(vaultService.encrypt(contractParagraph.getParagraphContent(), encryptionKey, shortName, clientToken));
            if (contractParagraph.getContentFields() != null) {
                contractParagraph.getContentFields().forEach(contentField -> {
                    contentField.setFieldValue(vaultService.encrypt(contentField.getFieldValue(), encryptionKey, shortName, clientToken));
                });
            }
            if (contractParagraph.getOptionalContents() != null) {
                contractParagraph.getOptionalContents().forEach(optionalContent -> {
                    optionalContent.setContent(vaultService.encrypt(optionalContent.getContent(), encryptionKey, shortName, clientToken));
                    if (optionalContent.getOptionalContentFields() != null) {
                        optionalContent.getOptionalContentFields().forEach(contentField -> {
                            contentField.setFieldValue(vaultService.encrypt(contentField.getFieldValue(), encryptionKey, shortName, clientToken));
                        });
                    }
                });
            }
        });
        return contractParagraphList;
    }

    @Override
    public Contract decrypt(Contract contract, String username, String password, String shortName) {
        String clientToken = vaultService.getClientToken(username, password);
        String encryptionKey = contract.getId().toString();
        contract.getParagraphList().forEach(contractParagraph -> {
            contractParagraph.setParagraphContent(vaultService.decrypt(contractParagraph.getParagraphContent(), encryptionKey, shortName, clientToken));
            if (contractParagraph.getContentFields() != null) {
                contractParagraph.getContentFields().forEach(contentField -> {
                    contentField.setFieldValue(vaultService.decrypt(contentField.getFieldValue(), encryptionKey, shortName, clientToken));
                });
            }
            if (contractParagraph.getOptionalContents() != null) {
                contractParagraph.getOptionalContents().forEach(optionalContent -> {
                    optionalContent.setContent(vaultService.decrypt(optionalContent.getContent(), encryptionKey, shortName, clientToken));
                    if (optionalContent.getOptionalContentFields() != null) {
                        optionalContent.getOptionalContentFields().forEach(contentField -> {
                            contentField.setFieldValue(vaultService.decrypt(contentField.getFieldValue(), encryptionKey, shortName, clientToken));
                        });
                    }
                });
            }
        });
        return contract;
    }
    
}
