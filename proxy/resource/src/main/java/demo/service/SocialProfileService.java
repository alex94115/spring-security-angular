package demo.service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import demo.domain.SocialProfile;
import demo.repository.SocialProfileRepository;
import demo.dto.SocialUserDTO;

@Service
@Transactional(readOnly = true)
public class SocialProfileService {

	@Autowired
	private SocialProfileRepository repository;
	
	public SocialProfile findBySiteAndSiteId( String site, String siteId ) {
		return this.repository.findUserBySiteAndSiteId( site, siteId );
	}
	
	public Set<SocialProfile> fromDto( SocialUserDTO dto ) {
		SocialProfile profile = new SocialProfile();

		profile.setSite( dto.getSite() );
		profile.setSiteUserId( dto.getSiteUserId() );
		profile.setAccessToken( dto.getAccessToken() );
		profile.setImageUrl( dto.getImageUrl() );
		profile.setProfileUrl( dto.getProfileUrl() );
		profile.setRank( 1 );
		
		Set<SocialProfile> profiles = new HashSet<SocialProfile>();
		profiles.add( profile );
		
		return profiles;
	}
	
	
}
