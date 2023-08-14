package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import lombok.*;

import javax.persistence.*;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "CONTRACT_VERSION")
public class ContractVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "contractVersion", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "versionTemplate")
    private List<VersionTemplate> versionTemplates;
    private String abbreviation;
    @ManyToMany
    private List<Permission> permissions;
    private String created;


}
