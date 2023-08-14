package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.ContractParagraph;
import de.krieger.personal.contractgenerator.model.OptionalContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionalContentRepository extends JpaRepository<OptionalContent, Long> {
    List<OptionalContent> findAllByContractParagraphAndModified(ContractParagraph contractParagraph, boolean modified);
}
