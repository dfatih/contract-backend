package de.krieger.personal.contractgenerator.integration;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import io.restassured.RestAssured;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
abstract class AbstractIntergationTest {

    @LocalServerPort
    private int serverPort;

    @Value("${server.servlet.context-path}")
    private String prefix;

    @BeforeAll
    public void setAppPort(){
       RestAssured.port =serverPort;
       RestAssured.basePath=prefix;
    }

}
