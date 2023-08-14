package de.krieger.personal.contractgenerator.service;

/*
import de.krieger.personal.contractgenerator.config.VaultConfigurationProperties;
import de.krieger.personal.contractgenerator.model.Candidate;
import de.krieger.personal.contractgenerator.model.Contract;
import de.krieger.personal.contractgenerator.repository.CandidateRepository;
import de.krieger.personal.contractgenerator.service.impl.PdfLayoutServiceImpl;
import de.krieger.personal.contractgenerator.service.impl.VaultServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Optional;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class VaultServiceTest {

    @InjectMocks
    private VaultServiceImpl vaultService;
    @Mock
    private CandidateRepository candidateRepository;

    private VaultConfigurationProperties vaultConfigurationProperties = new VaultConfigurationProperties();

    @BeforeAll
    void init() {
        MockitoAnnotations.openMocks(this);
        vaultConfigurationProperties.setEncrypt(true);
        vaultConfigurationProperties.setHost("https://vault.prod.kriegerit.de/");
        vaultConfigurationProperties.setTransitPath("v1/krieger/transit/dev");
        vaultService = new VaultServiceImpl(vaultConfigurationProperties);
    }

    @Test
    void createFile() throws IOException {
        //String testData = "that is just a test";
        //TODO: switch to test user
        String clientToken = vaultService.getClientToken("username", "password");
        System.out.println(clientToken);
        //String keyName = "test";
        //vaultService.createKey(keyName, clientToken);
        //String encryptedData = vaultService.encrypt(testData, keyName, clientToken);
        //System.out.println(encryptedData);
        //String decryptedData = vaultService.decrypt(encryptedData, keyName, clientToken);
        //System.out.println(decryptedData);
        //Assertions.assertEquals(testData, decryptedData);
    }
}
*/
