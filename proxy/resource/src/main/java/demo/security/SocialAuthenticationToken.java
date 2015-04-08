package demo.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.openid.OpenIDAttribute;

public class SocialAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	// ~ Instance fields
	// ================================================================================================

	private final AuthenticationStatus status;
	private final Object principal;
	private final String identityUrl;
	private final String message;
	private final List<OpenIDAttribute> attributes;

	// ~ Constructors
	// ===================================================================================================

	public SocialAuthenticationToken( AuthenticationStatus status,
			String identityUrl, String message, List<OpenIDAttribute> attributes) {
		super(new ArrayList<GrantedAuthority>(0));
		this.principal = identityUrl;
		this.status = status;
		this.identityUrl = identityUrl;
		this.message = message;
		this.attributes = attributes;
		setAuthenticated(false);
	}

	/**
	 * Created by the <tt>FacebookAuthenticationProvider</tt> on successful
	 * authentication.
	 * 
	 * @param principal
	 *            usually the <tt>UserDetails</tt> returned by the the
	 *            configured <tt>UserDetailsService</tt> used by the
	 *            <tt>FacebookAuthenticationProvider</tt>.
	 * 
	 */
	public SocialAuthenticationToken(Object principal,
			Collection<? extends GrantedAuthority> authorities,
			String identityUrl, List<OpenIDAttribute> attributes) {
		super(authorities);
		this.principal = principal;
		this.status = AuthenticationStatus.SUCCESS;
		this.identityUrl = identityUrl;
		this.message = null;
		this.attributes = attributes;

		setAuthenticated(true);
	}

	// ~ Methods
	// ========================================================================================================

	/**
	 * Returns 'null' always, as no credentials are processed by the provider.
	 * 
	 * @see org.springframework.security.core.Authentication#getCredentials()
	 */
	public Object getCredentials() {
		return null;
	}

	public String getIdentityUrl() {
		return identityUrl;
	}

	public String getMessage() {
		return message;
	}

	/**
	 * Returns the <tt>principal</tt> value.
	 * 
	 * @see org.springframework.security.core.Authentication#getPrincipal()
	 */
	public Object getPrincipal() {
		return principal;
	}

	public AuthenticationStatus getStatus() {
		return status;
	}

	public List<OpenIDAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		return "[" + super.toString() + ", attributes : " + attributes + "]";
	}
}
