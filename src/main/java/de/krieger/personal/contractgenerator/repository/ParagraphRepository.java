package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.ContractParagraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParagraphRepository extends JpaRepository<ContractParagraph, Long> {

    List<ContractParagraph> findAllByTemplate(boolean template);

    List<ContractParagraph> findAllByClean(boolean clean);

    List<ContractParagraph> findAllByContract(Contract contract);

    List<ContractParagraph> findAllByContractVersionNameAndClean(ContractVersionName contractVersionName, boolean clean);
}
