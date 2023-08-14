package de.krieger.personal.contractgenerator.security;

import org.springframework.security.core.GrantedAuthority;

public class Permission implements GrantedAuthority {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3729339899493065208L;
	private String name;
	
	public Permission(String name) {
		super();
		this.name = name;
	}

	@Override
	public String getAuthority() {
		return name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Permission other = (Permission) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Permission [name=" + name + "]";
	}
	
}
