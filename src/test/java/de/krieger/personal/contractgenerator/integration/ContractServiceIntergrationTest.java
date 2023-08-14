package de.krieger.personal.contractgenerator.integration;

import de.krieger.personal.contractgenerator.repository.ContractRepository;
import de.krieger.personal.contractgenerator.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;

import static org.assertj.core.api.Assertions.assertThat;

public class ContractServiceIntergrationTest extends AbstractIntergationTest {
    @Autowired
    ContractRepository contractRepository;

    @Autowired
    ContractService contractService;

  /*  @Test
    void uploadFile() {

        //create contract with values and save state as created
        Contract contract = Contract.builder()
                .id(3L)
                .candidateId(1L)
                .employeeId(2L)
                .contractFileName("test")
                .contractFileType(".pdf")
                .contractFileStatus(ContractFileStatus.CREATED)
                .build();
        //update the name of the uploaded file for the corresponding ID and save the uploaded contract
        MultipartFile fileContract = new MockMultipartFile("testContract.pdf", "helloNewContent".getBytes());
        contractRepository.save(contract);
        contractService.uploadFile(3L, fileContract);
        Optional<Contract> savedContract = contractRepository.findById(contract.getContractId());
        //Checking if the values are same
        assertThat(savedContract.isPresent());
        assertThat(savedContract.get().getContractId()).isEqualTo(contract.getContractId());
        assertThat(savedContract.get().getCandidateId()).isEqualTo(contract.getCandidateId());
        assertThat(savedContract.get().getEmployeeId()).isEqualTo(contract.getEmployeeId());
        assertThat(savedContract.get().getContractFileName()).isEqualTo(contract.getContractFileName());
        assertThat(savedContract.get().getContractFileType()).isEqualTo(contract.getContractFileType());
        assertThat(savedContract.get().getContractFileStatus()).isEqualTo(ContractFileStatus.UPLOADED);
    }*/
}
