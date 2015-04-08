package demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import demo.domain.SocialProfile;

public interface SocialProfileRepository extends JpaRepository<SocialProfile, Long> {

	String FIND_BY_SITE_AND_SITE_USER_ID = 
			  " SELECT s "
			+ " FROM SocialProfile s "
			+ " WHERE s.site = (:site) "
			+ "   AND s.siteUserId = (:siteUserId) ";
	
	@Query(FIND_BY_SITE_AND_SITE_USER_ID)
	SocialProfile findUserBySiteAndSiteId( @Param("site") String site, @Param("siteUserId") String siteUserId );
}
