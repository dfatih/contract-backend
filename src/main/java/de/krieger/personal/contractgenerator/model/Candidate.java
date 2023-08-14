package de.krieger.personal.contractgenerator.model;

import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import lombok.Data;

import javax.persistence.*;
import java.util.Arrays;

@Data
@Entity
@Table(name = "CANDIDATE")
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long candidateId;
    private String salutation;
    private String degree;
    private String firstName;
    private String lastName;
    private String street;
    private String streetNumber;
    private String zipCode;
    private String residence;

}
