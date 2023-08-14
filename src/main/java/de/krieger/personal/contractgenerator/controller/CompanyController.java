package de.krieger.personal.contractgenerator.controller;


import de.krieger.personal.contractgenerator.model.Company;
import de.krieger.personal.contractgenerator.repository.CompanyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CompanyController {

    private final CompanyRepository companyRepository;

    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // API to get all the firm names along with the associated subCompany ID and names
    @GetMapping("/api/firmNamesDropdown")
    public ResponseEntity<List<Company>> findAll() {

        return ResponseEntity.ok(companyRepository.findAll());
    }

    @PostMapping("/api/saveCompany")
    public ResponseEntity<?> saveCompany(@RequestBody Company company) {
        return ResponseEntity.ok(companyRepository.save(company));
    }


   /* @GetMapping("/api/subCompany")
    public @ResponseBody
    ResponseEntity<Firm> getSubCompany(@RequestParam Long firmId) {
        Optional<Firm> subCompany = subCompanyService.getSubCompany(firmId);
        if (subCompany.isEmpty()) {
            return ResponseEntity.notFound();
        } else {
            return ResponseEntity.ok(subCompany);
        }

    }

    */


}

