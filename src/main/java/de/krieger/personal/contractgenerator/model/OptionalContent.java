package de.krieger.personal.contractgenerator.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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
@Table(name = "OPTIONAL_CONTENT")
public class OptionalContent implements Comparable<OptionalContent> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String shortName;
    @Column(length = 65535)
    private String content;
    @Column(name = "IS_MODIFIED")
    private boolean modified;
    @Column(name = "IS_SELECTED")
    private boolean selected;
    @ManyToOne
    @ToString.Include
    @JsonBackReference(value = "optionalContents")
    private ContractParagraph contractParagraph;
    @OneToMany(mappedBy = "optionalContent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "optionalContentFields")
    private List<OptionalContentField> optionalContentFields;
    private String paragraphNumber;
    @Enumerated(EnumType.STRING)
    private ContractVersionName contractVersionName;
    private String versionTemplateNames;
    private Language language;
    private int selectionGroup;

    @Override
    public int compareTo(OptionalContent optionalContent) {
        return (int) (this.id - optionalContent.getId());
    }
}
