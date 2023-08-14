package de.krieger.personal.contractgenerator.config;

import com.sun.istack.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import lombok.Data;

@ConfigurationProperties(prefix = "vault")
@Configuration
@Validated
@Data
public class VaultConfigurationProperties {

    private boolean encrypt;
    @NotNull
    private String transitPath;
    @NotNull
    private String host;

    private String transitUsername;

    private String transitPassword;

}
