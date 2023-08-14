package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "VERSION_TEMPLATE")
public class VersionTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String abbreviation;

    private String templateName;
    @ManyToOne
    @ToString.Include
    @JsonBackReference(value = "versionTemplate")
    private ContractVersion contractVersion;


}
