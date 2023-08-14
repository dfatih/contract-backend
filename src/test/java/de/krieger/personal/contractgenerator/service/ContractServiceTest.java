package de.krieger.personal.contractgenerator.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import de.krieger.personal.contractgenerator.repository.ContractRepository;
import de.krieger.personal.contractgenerator.repository.ParagraphRepository;
import de.krieger.personal.contractgenerator.service.impl.ContractServiceImpl;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContractServiceTest {
    @Mock
    ContractRepository contractRepository;
    @Mock
    CryptographyService cryptographyService;
    @Mock
    ParagraphRepository paragraphRepository;  
    @InjectMocks
    ContractServiceImpl contractService;

    @BeforeAll
    void init() {
        MockitoAnnotations.initMocks(this);

    }
/*
    @Test
    void uploadFile() throws IOException {
        Contract contract = Contract.builder()
                .id(3L)
                .candidateId(1L)
                .employeeId(2L)
                .contractFileName("testContract")
                .contractFileType(".pdf")
                .contractFileStatus(ContractFileStatus.CREATED)
                .date(LocalDate.now())
                .build();

        MockMultipartFile fileContract = new MockMultipartFile("testContract.pdf", "helloNewContent".getBytes());
        when(contractRepository.findById(3L)).thenReturn(Optional.of(contract));

        contractService.uploadFile(3L, fileContract);
        ArgumentCaptor<Contract> contractCaptor = ArgumentCaptor.forClass(Contract.class);
        verify(contractRepository, times(1)).save(contractCaptor.capture());

        assertEquals(contractCaptor.getValue().getContractFileStatus(), ContractFileStatus.UPLOADED);
        assertEquals(contractCaptor.getValue().getContractFile(), fileContract.getBytes());

    }
*/

}
