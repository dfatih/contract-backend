package de.krieger.personal.contractgenerator.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "SIGNEE")
public class Signee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;
    String name;
    String position;
    @ManyToOne
    @ToString.Include
    @JsonBackReference(value = "signee")
    private Contract contract;
}
