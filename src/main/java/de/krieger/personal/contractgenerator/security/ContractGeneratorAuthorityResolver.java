package de.krieger.personal.contractgenerator.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;


@Component
@Slf4j
public class ContractGeneratorAuthorityResolver implements AuthorityResolver {

	private static final String COMMA = ",";
	private static final char UNDERSCORE = '_';
	private static final String EMPTY = "";
	private static final String PERMISSION_PREFIX = "BU_CON_";

	@Override
	public List<? extends GrantedAuthority> resolveAuthorities(final List<String> roles) {
		return Collections.emptyList();
	}

	private Set<Permission> splitIncomingRolesIntoRelevantPermissions(final List<String> roles) {
		final Set<Permission> permissions = new HashSet<>();

		for (String role : roles) {
			log.debug(role);
			if (role.contains(PERMISSION_PREFIX)) {
				String strippedRole = role.replace(PERMISSION_PREFIX, EMPTY);
				strippedRole = strippedRole.substring(0, strippedRole.indexOf(UNDERSCORE));
				for (String funktionId : strippedRole.split(COMMA)) {
					permissions.add(getPermissionForFunctionId(funktionId));
				}
			}
		}
		return permissions;

	}

	private Permission getPermissionForFunctionId(String funktionId) {
		// PermissionType permissionType = PermissionType.getPermissionTypeByFunctionId(Integer.parseInt(funktionId));
		// if (permissionType != null) {
		// 	return new Permission(permissionType.name());
		// }
		return null;
	}

}
