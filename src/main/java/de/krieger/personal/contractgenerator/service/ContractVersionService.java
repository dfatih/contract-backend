package de.krieger.personal.contractgenerator.service;

import de.krieger.personal.contractgenerator.model.ContractVersion;
import de.krieger.personal.contractgenerator.model.OldContractVersion;

import java.util.List;

public interface ContractVersionService {
    List<OldContractVersion> getAll();
}
