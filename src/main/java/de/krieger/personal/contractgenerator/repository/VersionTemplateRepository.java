package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.VersionTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VersionTemplateRepository extends JpaRepository<VersionTemplate, Long> {
}
