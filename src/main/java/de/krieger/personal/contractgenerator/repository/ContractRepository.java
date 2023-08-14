package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.ContractParagraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Long> {

}
