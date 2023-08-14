package de.krieger.personal.contractgenerator.service;

import de.krieger.personal.contractgenerator.model.Contract;

public interface ContractService {
    
    Contract save(Contract contract);
    Contract findById(Long id, String password, String shortName);
    void deleteById(Long id);
    Contract update(Contract contract, String password, String shortName);
}
