package de.krieger.personal.contractgenerator.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.util.FileCopyUtils;

import de.krieger.personal.contractgenerator.security.AuthorityResolver;
import de.krieger.personal.contractgenerator.security.AuthorityResolvingAccessTokenConverter;
import de.krieger.personal.contractgenerator.security.AuthorityResolvingUserAuthenticationConverter;
import de.krieger.personal.contractgenerator.security.ContractGeneratorAuthorityResolver;

@Configuration
public class JwtConfiguration {

    @Bean
    @Qualifier("tokenStore")
    public TokenStore tokenStore(JwtAccessTokenConverter jwtAccessTokenConverter) {
        return new JwtTokenStore(jwtAccessTokenConverter);
    }

    @Bean
    protected JwtAccessTokenConverter jwtTokenEnhancer() {
        JwtAccessTokenConverter converter =  new JwtAccessTokenConverter();
        Resource resource = new ClassPathResource("public.cert");
        String publicKey = null;
        try {
            publicKey = new String(FileCopyUtils.copyToByteArray(resource.getInputStream()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        converter.setVerifierKey(publicKey);

        AuthorityResolver authorityResolver = new ContractGeneratorAuthorityResolver();

        AuthorityResolvingUserAuthenticationConverter userAuthenticationConverter = new AuthorityResolvingUserAuthenticationConverter();
        userAuthenticationConverter.setAuthorityResolver(authorityResolver);

        AuthorityResolvingAccessTokenConverter accessTokenConverter = new AuthorityResolvingAccessTokenConverter();
        accessTokenConverter.setAuthorityResolver(authorityResolver);

        accessTokenConverter.setUserTokenConverter(userAuthenticationConverter);

        converter.setAccessTokenConverter(accessTokenConverter);

        return converter;
    }
}
