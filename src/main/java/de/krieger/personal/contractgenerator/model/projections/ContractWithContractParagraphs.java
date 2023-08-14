package de.krieger.personal.contractgenerator.model.projections;

import com.fasterxml.jackson.annotation.JsonBackReference;
import de.krieger.personal.contractgenerator.enums.ContractVersionName;
import de.krieger.personal.contractgenerator.enums.VersionTemplateName;
import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.model.ContractParagraph;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.rest.core.config.Projection;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Projection(name = "contractWithParagraphs", types = { Contract.class })
public interface ContractWithContractParagraphs {

    Long getId();
    String getFileName();
    String getTemplate();
    Long getCandidateId();
    Long getCompanyId();
    String getLocation();
    LocalDateTime getLastEdit();
    String getCreator();
    String getNameOfSignee();
    String getPositionOfSignee();
    String getPlaceOfSignature();
    LocalDate getDateOfSignature();
    List<ContractParagraph> getParagraphList();
    String getLanguage();
    ContractVersionName getContractVersion();
    VersionTemplateName getVersionTemplate();

}
