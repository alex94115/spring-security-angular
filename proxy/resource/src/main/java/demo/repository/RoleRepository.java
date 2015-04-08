package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import demo.domain.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByDescription( String description );
}
