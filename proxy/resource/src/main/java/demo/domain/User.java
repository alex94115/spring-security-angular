package demo.domain;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="Users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="user_id")
	private Long id;
	
	@Column(unique=true)
	private String username;
	
	@Column(unique=true, name="screen_name")
	private String screenName;
	
	@Column(name="display_name")
	private String displayName;
	
	@Column(name="first_name")
	private String firstName;
	
	@Column(name="middle_name")
	private String middleName;
	
	@Column(name="last_name")
	private String lastName;
	
	@Column(name="email")
	private String email;
	
	@Column(name="gender")
	private String gender;
	
	@Column(name="timezone")
	private Integer timezone;
	
	@Column(name="enabled")
	private Boolean isEnabled;
	
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable( name="UsersRoles",
		        joinColumns={@JoinColumn(name="user_id", referencedColumnName="user_id")},
		        inverseJoinColumns={@JoinColumn(name="role_id", referencedColumnName="role_id")})
	private Set<Role> roles;
	
	@OneToMany( mappedBy="user", fetch=FetchType.LAZY, cascade={ CascadeType.PERSIST, CascadeType.MERGE } )
	private Set<SocialProfile> socialProfiles;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Long getId() {
		return id;
	}
	
	public void setIsEnabled( Boolean isEnabled ) {
		this.isEnabled = isEnabled;
	}
	
	public Boolean getIsEnabled() {
		return this.isEnabled;
	}
	
	public Set<SocialProfile> getSocialProfiles() {
		return socialProfiles;
	}

	public void setSocialProfiles(Set<SocialProfile> socialProfiles) {
		this.socialProfiles = socialProfiles;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public Integer getTimezone() {
		return timezone;
	}

	public void setTimezone(Integer timezone) {
		this.timezone = timezone;
	}
	
	public String getScreenName() {
		return screenName;
	}

	public void setScreenName(String screenName) {
		this.screenName = screenName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((username == null) ? 0 : username.hashCode());
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
		User other = (User) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

}
