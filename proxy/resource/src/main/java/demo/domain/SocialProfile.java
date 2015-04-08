package demo.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name="SocialProfiles")
public class SocialProfile {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="social_profile_id")
	private Long id;
	
	@ManyToOne( fetch=FetchType.LAZY )
	@JoinColumn(name="user_id")
	private User user;
	
	@Column(nullable = false)
	private String site;
	
	@Column(name="site_user_id", nullable = false)
	private String siteUserId;
	
	@Column(name="profile_url")
	private String profileUrl;
	
	@Column(name="image_url")
	private String imageUrl;
	
	@Column(nullable = false)
	private Integer rank;
	
	@Column(nullable = false)
	private String accessToken;

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getSiteUserId() {
		return siteUserId;
	}

	public void setSiteUserId(String siteUserId) {
		this.siteUserId = siteUserId;
	}

	public String getProfileUrl() {
		return profileUrl;
	}

	public void setProfileUrl(String profileUrl) {
		this.profileUrl = profileUrl;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public Long getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((site == null) ? 0 : site.hashCode());
		result = prime * result
				+ ((siteUserId == null) ? 0 : siteUserId.hashCode());
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
		SocialProfile other = (SocialProfile) obj;
		if (site == null) {
			if (other.site != null)
				return false;
		} else if (!site.equals(other.site))
			return false;
		if (siteUserId == null) {
			if (other.siteUserId != null)
				return false;
		} else if (!siteUserId.equals(other.siteUserId))
			return false;
		return true;
	}

	
	
}
