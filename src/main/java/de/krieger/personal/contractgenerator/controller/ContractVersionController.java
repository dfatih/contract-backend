package de.krieger.personal.contractgenerator.controller;

import de.krieger.personal.contractgenerator.service.ContractVersionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContractVersionController {

    private final ContractVersionService contractVersionService;

    public ContractVersionController(ContractVersionService contractVersionService) {
        this.contractVersionService = contractVersionService;
    }

    @GetMapping("/api/contractVersions")
    public ResponseEntity<?> getContractVersions(){
        return ResponseEntity.ok().body(contractVersionService.getAll());
    }

}
