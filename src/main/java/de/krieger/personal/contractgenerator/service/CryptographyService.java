package de.krieger.personal.contractgenerator.service;

import java.util.List;

import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.ContractParagraph;

public interface CryptographyService {

    List<ContractParagraph> encrypt(List<ContractParagraph> contractParagraphList, String username, String password, String shortName);
    Contract decrypt(Contract contract, String username, String password, String shortName);

}
