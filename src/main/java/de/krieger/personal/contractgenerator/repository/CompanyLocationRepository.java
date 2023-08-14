package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.CompanyLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyLocationRepository extends JpaRepository<CompanyLocation, Long> {
}
