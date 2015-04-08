package demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.domain.SocialProfile;
import demo.domain.User;
import demo.exception.UserExistsException;
import demo.repository.UserRepository;
import demo.dto.SocialUserDTO;

@Service
@Transactional(readOnly = false)
public class UserService {

	@Autowired
	private SocialProfileService socialProfileService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private RoleService roleService;
	
	public Boolean signUp( SocialUserDTO profile ) {
		
		// check that the social profile isn't already taken
		SocialProfile existingProfile = this.socialProfileService.findBySiteAndSiteId( profile.getSite(), profile.getSiteUserId() );
		if( existingProfile != null ) {
			throw new UserExistsException( String.format("Cannot sign up with site: %s and userId: %s because this user already is registered.", 
				profile.getSite(), profile.getSiteUserId() ));
		}
		
		// create the User object graph
		User user = new User();
		user.setUsername( String.format("%s/%s", profile.getSite(), profile.getSiteUserId() ));
		user.setFirstName( profile.getFirstName() );
		user.setMiddleName( profile.getMiddleName() );
		user.setLastName( profile.getLastName() );
		user.setDisplayName( profile.getDisplayName() );
		user.setEmail( profile.getEmail() );
		user.setGender( profile.getGender() );
		user.setTimezone( profile.getTimezone() );
		user.setIsEnabled( true );
		
		// add role
		user.setRoles( roleService.getDefaultRoleSet() );
		
		// add social profile
		user.setSocialProfiles( this.socialProfileService.fromDto( profile ) );
		
		// persist
		user = this.userRepository.save( user );
		
		return user.getId() != null;
	}
	
}
