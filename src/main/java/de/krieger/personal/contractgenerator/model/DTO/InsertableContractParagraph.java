package de.krieger.personal.contractgenerator.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InsertableContractParagraph {

    private String paragraphNumber;
    private int paragraphNumberAsInt;
    private String paragraphTitle;
    private String firstPassage;
    private List<String> paragraphPassages;
}
