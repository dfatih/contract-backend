package de.krieger.personal.contractgenerator.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;

import com.jayway.jsonpath.JsonPath;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import de.krieger.personal.contractgenerator.config.VaultConfigurationProperties;
import de.krieger.personal.contractgenerator.service.VaultService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@RequiredArgsConstructor
@Slf4j
public class VaultServiceImpl implements VaultService {

    private final VaultConfigurationProperties vaultConfigurationProperties;

    @Override
    public String encrypt(String data, String keyName, String shortName, String clientToken) {
        if (!vaultConfigurationProperties.isEncrypt()) {
            return data;
        }
        if (data == null || data.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String url = vaultConfigurationProperties.getHost() + vaultConfigurationProperties.getTransitPath() + "/encrypt/" + shortName + "-" + new HashCodeBuilder(17, 83).append(keyName).toHashCode();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Vault-Token", clientToken);
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("{\"plaintext\": \"" + Base64.getEncoder().encodeToString(data.getBytes()) + "\"}", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, stringHttpEntity, String.class);
        return JsonPath.parse(response.getBody()).read("$.data.ciphertext");
    }

    @Override
    public String decrypt(String encryptedData, String keyName, String shortName,  String clientToken) {
        if (!vaultConfigurationProperties.isEncrypt()) {
            return encryptedData;
        }
        if (encryptedData == null || encryptedData.isEmpty()) {
            return StringUtils.EMPTY;
        }
        String url = vaultConfigurationProperties.getHost() + vaultConfigurationProperties.getTransitPath() + "/decrypt/" + shortName + "-" + new HashCodeBuilder(17, 83).append(keyName).toHashCode();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Vault-Token", clientToken);
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("{\"ciphertext\": \"" + encryptedData + "\"}", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, stringHttpEntity, String.class);
        return new String(Base64.getDecoder().decode(JsonPath.parse(response.getBody()).read("$.data.plaintext").toString().getBytes()));
    }

    @Override
    public String getClientToken(String username, String password) {
        if (!vaultConfigurationProperties.isEncrypt()) {
            return StringUtils.EMPTY;
        }
        String url = vaultConfigurationProperties.getHost() + "v1/auth/ldap/login/" + username;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
        headers.setContentType(mediaType);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.setAcceptCharset(List.of(StandardCharsets.UTF_8));
        HttpEntity<String> stringHttpEntity = new HttpEntity<>("{\"password\": \"" + password + "\"}", headers);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, stringHttpEntity, String.class);
        log.debug(String.valueOf(response.getStatusCodeValue()));
        log.debug(response.toString());
        if (response.getStatusCode().is2xxSuccessful()) {
            return JsonPath.parse(response.getBody()).read("$.auth.client_token", String.class);
        } else {
            return response.getStatusCode() + response.getBody();
        }
    }

    @Override
    public void createKey(String keyName, String shortName) {
        if (vaultConfigurationProperties.isEncrypt()) {
            String clientToken = getClientToken(vaultConfigurationProperties.getTransitUsername(), vaultConfigurationProperties.getTransitPassword());
            String url = vaultConfigurationProperties.getHost() + vaultConfigurationProperties.getTransitPath() + "/keys/" + shortName + "-" + new HashCodeBuilder(17, 83).append(keyName).toHashCode();
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Vault-Token", clientToken);
            HttpEntity<String> stringHttpEntity = new HttpEntity<>("{\"type\": \"aes256-gcm96\"}", headers);
            restTemplate.exchange(url, HttpMethod.POST, stringHttpEntity, String.class);
        }
    }
}
