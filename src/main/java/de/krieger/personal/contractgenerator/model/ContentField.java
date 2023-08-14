package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import de.krieger.personal.contractgenerator.enums.FieldType;
import lombok.*;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "CONTENT_FIELD")
public class ContentField implements Comparable<ContentField> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldName;
    private String fieldDescription;
    private String fieldValue;
    private FieldType fieldType;
    @ManyToOne
    @ToString.Include
    @JsonBackReference(value = "contentFields")
    private ContractParagraph contractParagraph;

    @Override
    public int compareTo(ContentField contentField) {
        return (int) (this.id - contentField.getId());
    }
}
