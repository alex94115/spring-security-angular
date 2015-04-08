package demo.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.domain.Role;
import demo.repository.RoleRepository;

@Service
@Transactional(readOnly = true)
public class RoleService {
	
	private static final String USER_ROLE_DESCRIPTION = "USER";
	
	@Autowired
	private RoleRepository roleRepository;

	public Set<Role> getDefaultRoleSet() {
		Role role = this.roleRepository.findByDescription( USER_ROLE_DESCRIPTION );
		
		if( role == null ) {
			throw new RuntimeException( "Couldn't find the USER role. Check the database." );
		}
		
		Set<Role> roles = new HashSet<Role>();
		roles.add( role );
		return roles;
	}
	
}
