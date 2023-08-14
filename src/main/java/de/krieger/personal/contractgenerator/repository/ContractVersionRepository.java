package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.ContractVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractVersionRepository extends JpaRepository<ContractVersion, Long> {
}
