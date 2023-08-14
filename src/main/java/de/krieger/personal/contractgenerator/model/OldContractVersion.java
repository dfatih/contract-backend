package de.krieger.personal.contractgenerator.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OldContractVersion {
    private String name;
    private List<String> templateNames;
}
