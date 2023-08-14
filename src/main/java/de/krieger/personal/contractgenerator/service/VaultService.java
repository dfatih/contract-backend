package de.krieger.personal.contractgenerator.service;

public interface VaultService {
    String encrypt(String data, String keyName, String shortname, String clientToken);
    String decrypt(String encryptedData, String keyName, String shortName, String clientToken);
    String getClientToken(String username, String password);
    void createKey(String keyName, String shortName);
}
