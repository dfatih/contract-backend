package de.krieger.personal.contractgenerator.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;

public interface AuthorityResolver {

	List<? extends GrantedAuthority> resolveAuthorities(List<String> roles);
	
}
