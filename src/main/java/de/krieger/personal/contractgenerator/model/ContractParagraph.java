package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.Language;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"contract"})
@Table(name = "PARAGRAPH")
public class ContractParagraph implements Comparable<ContractParagraph>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @ManyToOne
    @JoinColumn(name = "CONTRACT_ID")
    @JsonBackReference(value = "paragraphList")
    private Contract contract;
    private String paragraphNumber;
    private String paragraphTitle;
    @Column(length = 65535)
    private String paragraphContent;
    @OneToMany(mappedBy = "contractParagraph", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "contentFields")
    private List<ContentField> contentFields;
    @Column(name = "IS_TEMPLATE")
    private boolean template;
    @Column(name = "IS_CLEAN")
    private boolean clean;
    private Language language;
    @Enumerated(EnumType.STRING)
    private ContractVersionName contractVersionName;
    private String versionTemplateNames;
    @OneToMany(mappedBy = "contractParagraph", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "optionalContents")
    private List<OptionalContent> optionalContents;
    private boolean selectionGroups;

    @Override
    public int compareTo(ContractParagraph contractParagraph) {
        int compareParagraphNumber = normalizeSplittedParagraphNumber(contractParagraph.getParagraphNumber());
        return normalizeSplittedParagraphNumber(this.paragraphNumber) - compareParagraphNumber;
    }

    private int normalizeSplittedParagraphNumber(String contractParagraphNumber) {
        return contractParagraphNumber.length() == 3 ? Integer.parseInt(contractParagraphNumber.substring(0, 2)) : Integer.parseInt(contractParagraphNumber);
    }
}
