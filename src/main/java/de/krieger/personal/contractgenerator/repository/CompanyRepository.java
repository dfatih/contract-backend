package de.krieger.personal.contractgenerator.repository;


import de.krieger.personal.contractgenerator.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {


}
