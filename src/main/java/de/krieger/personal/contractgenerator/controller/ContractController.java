package de.krieger.personal.contractgenerator.controller;


import java.time.LocalDateTime;
import java.util.List;

import de.krieger.personal.contractgenerator.service.VaultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.repository.ContractRepository;
import de.krieger.personal.contractgenerator.service.ContractService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.client.HttpStatusCodeException;

@RestController
@RequiredArgsConstructor
public class ContractController {

    private final ContractRepository contractRepository;
    private final ContractService contractService;
    private final VaultService vaultService;

    @PostMapping("/api/saveContract")
    public ResponseEntity<?> saveContract(@RequestBody Contract contract) {
        contract.setLastEdit(LocalDateTime.now());
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        contract.setCreator(authenticatedUser);
        return ResponseEntity.ok(contractService.save(contract));
    }

    @PutMapping("/api/contracts/update")
    public ResponseEntity<?> updateContract(@RequestBody Contract contract, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            contract.setLastEdit(LocalDateTime.now());
            contract.setCreator(authenticatedUser);
            return ResponseEntity.ok(contractService.update(contract, password, shortName));
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    @GetMapping("/api/contracts/{id}")
    public ResponseEntity<?> getContractById(@PathVariable Long id, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            Contract decryptedContract = null;
            try {
                vaultService.createKey("authTest", shortName);
                vaultService.encrypt("This is a test.", "authTest", shortName, vaultService.getClientToken(authenticatedUser, password));
                decryptedContract = contractService.findById(id, password, shortName);
            } catch (HttpStatusCodeException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
            }
            return ResponseEntity.ok(decryptedContract);
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    //API to fetch list of all contracts
    @CrossOrigin
    @GetMapping("/api/viewAllContracts")
    public List<Contract> findAll() {
        return contractRepository.findAll();
    }

    //API to Delete contract by Contract ID
    @DeleteMapping("/api/deleteContractById")
    public void deleteContractById(@RequestParam Long id) {
        contractService.deleteById(id);

//        contractRepository.deleteById(id);
    }



}
