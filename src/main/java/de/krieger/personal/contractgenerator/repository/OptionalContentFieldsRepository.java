package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.OptionalContentField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionalContentFieldsRepository extends JpaRepository<OptionalContentField, Long> {
}
