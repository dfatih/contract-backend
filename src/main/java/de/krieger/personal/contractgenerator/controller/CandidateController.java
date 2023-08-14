package de.krieger.personal.contractgenerator.controller;

import de.krieger.personal.contractgenerator.model.Candidate;
import de.krieger.personal.contractgenerator.repository.CandidateRepository;
import de.krieger.personal.contractgenerator.service.VaultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;

@RestController
public class CandidateController {

    private final CandidateRepository candidateRepository;
    private final VaultService vaultService;

    public CandidateController(CandidateRepository candidateRepository, VaultService vaultService) {
        this.candidateRepository = candidateRepository;
        this.vaultService = vaultService;
    }

    @CrossOrigin
    @GetMapping("/api/candidates/{id}")
    public ResponseEntity<?> getCandidate(@PathVariable Long id) {
        if(candidateRepository.findById(id).isPresent()) {
            return ResponseEntity.ok(candidateRepository.findById(id).get());
        } else {
            return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/api/saveCandidate")
    public ResponseEntity<?> saveCandidate(@RequestBody Candidate candidate, @RequestHeader("content-token") String password, @RequestParam("company") String shortName) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            Candidate savedCandidate = null;
            try {
                vaultService.createKey("authTest", shortName);
                vaultService.encrypt("This is a test.", "authTest", shortName, vaultService.getClientToken(authenticatedUser, password));
                savedCandidate = candidateRepository.save(candidate);
            } catch (HttpStatusCodeException e) {
                return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
            }
            return ResponseEntity.ok(savedCandidate);
        } else {
                String response = vaultService.getClientToken(authenticatedUser, password);
                // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
                return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    @PutMapping("/candidates/{id}")
    public ResponseEntity<?> updateCandidate(@PathVariable Long id, @RequestBody Candidate candidate, @RequestHeader("content-token") String password) {
        String authenticatedUser = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!vaultService.getClientToken(authenticatedUser, password).contains("errors")) {
            if(candidateRepository.findById(id).isPresent()) {
                Candidate savedCandidate = null;
                try {
                    savedCandidate = candidateRepository.save(candidate);
                } catch (HttpClientErrorException e) {
                    return ResponseEntity.status(e.getStatusCode()).body(e.getMessage());
                }
                return ResponseEntity.ok(savedCandidate);
            } else {
                return new ResponseEntity<>("Not found", HttpStatus.NOT_FOUND);
            }
        } else {
            String response = vaultService.getClientToken(authenticatedUser, password);
            // Arrays.toString(JsonPath.parse(response.substring(3)).read("$.errors", String[].class))
            return new ResponseEntity<>("Das Passwort ist nicht korrekt.", HttpStatus.valueOf(response.substring(0, 3)));
        }
    }

    //API to Delete contract by Contract ID
    @DeleteMapping("/api/deleteCandidateById")
    public void deleteContractById(@RequestParam Long id) {
        candidateRepository.deleteById(id);
    }

}
