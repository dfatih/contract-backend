package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "OPTIONAL_CONTENT_FIELD")
public class OptionalContentField implements Comparable<OptionalContentField> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String fieldName;
    private String fieldDescription;
    private String fieldValue;
    @ManyToOne
    @ToString.Include
    @JsonBackReference(value = "optionalContentFields")
    private OptionalContent optionalContent;

    @Override
    public int compareTo(OptionalContentField optionalContentField) {
        return (int) (this.id - optionalContentField.getId());
    }
}
