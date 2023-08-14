package de.krieger.personal.contractgenerator.service;

import de.krieger.personal.contractgenerator.model.ContractParagraph;

import java.util.List;

public interface DatabasePopulationService {
    List<ContractParagraph> populateDatabase();

    List<ContractParagraph> updateTemplates();
}
