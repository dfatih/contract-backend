package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.Language;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import lombok.*;

import javax.persistence.*;
import javax.print.attribute.standard.DateTimeAtCreation;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "CONTRACT")
@Data
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fileName;
    @Column(length = 65535)
    private String template;
    private Long candidateId;
    private Long companyId;
    private String location;
    private LocalDateTime lastEdit;
    private String creator;
    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    @JsonManagedReference(value = "signee")
    private List<Signee> signeeList;
    private String placeOfSignature;
    private LocalDate dateOfSignature;
    @OneToMany(mappedBy = "contract", cascade = CascadeType.MERGE, orphanRemoval = true)
    @JsonManagedReference(value = "paragraphList")
    @ToString.Include
    private List<ContractParagraph> paragraphList = new ArrayList<>();
    private Language language;
    @Enumerated(EnumType.STRING)
    private ContractVersionName contractVersionName;
    @Enumerated(EnumType.STRING)
    private VersionTemplateName versionTemplateName;
}
