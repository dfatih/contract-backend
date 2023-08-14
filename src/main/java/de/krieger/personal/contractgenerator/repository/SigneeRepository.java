package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.Signee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SigneeRepository extends JpaRepository<Signee, Long> {
    List<Signee> findAllByContract(Contract contract);
}
