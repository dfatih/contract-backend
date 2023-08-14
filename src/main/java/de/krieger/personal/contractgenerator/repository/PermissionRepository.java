package de.krieger.personal.contractgenerator.repository;

import de.krieger.personal.contractgenerator.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {
}
