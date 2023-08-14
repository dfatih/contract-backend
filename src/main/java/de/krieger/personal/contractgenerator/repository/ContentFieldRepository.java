package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.ContentField;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContentFieldRepository extends JpaRepository<ContentField, Long> {
}
