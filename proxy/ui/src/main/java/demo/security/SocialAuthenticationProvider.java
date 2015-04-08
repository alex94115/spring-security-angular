package demo.security;

import org.springframework.beans.factory.InitializingBean;
//import org.apache.http.client.utils.URIBuilder;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.Assert;

public class SocialAuthenticationProvider implements AuthenticationProvider, InitializingBean {

    private AuthenticationUserDetailsService<SocialAuthenticationToken> userDetailsService;
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();
    
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userDetailsService, "The userDetailsService must be set");
	}

	@Override
	public Authentication authenticate( Authentication authentication ) throws AuthenticationException {
		
		 if( !supports(authentication.getClass()) ) {
	            return null;
	        }

	        if( authentication instanceof SocialAuthenticationToken ) {
	        	SocialAuthenticationToken response = ( SocialAuthenticationToken )authentication;
	            AuthenticationStatus status = response.getStatus();

	            // handle the various possibilities
	            if (status == AuthenticationStatus.SUCCESS) {
	                // Lookup user details
	                UserDetails userDetails = userDetailsService.loadUserDetails(response);

	                return this.createSuccessfulAuthentication(userDetails, response);

	            } else if (status == AuthenticationStatus.FAILURE) {
	                throw new BadCredentialsException("Log in failed - identity could not be verified");
	            } else {
	                throw new AuthenticationServiceException("Unrecognized return value " + status.toString());
	            }
	        }

	        return null;
		
	}

	@Override
	public boolean supports( Class<?> authentication ) {
		return SocialAuthenticationToken.class.isAssignableFrom( authentication );
	}
	
	/**
     * Used to load the {@code UserDetails} for the authenticated Facebook user.
     */
    public void setUserDetailsService( UserDetailsService userDetailsService ) {
        this.userDetailsService = new UserDetailsByNameServiceWrapper<SocialAuthenticationToken>( userDetailsService );
    }

    /**
     * Used to load the {@code UserDetails} for the authenticated Facebook user.
     */
    public void setAuthenticationUserDetailsService( AuthenticationUserDetailsService<SocialAuthenticationToken> userDetailsService ) {
        this.userDetailsService = userDetailsService;
    }

    public void setAuthoritiesMapper( GrantedAuthoritiesMapper authoritiesMapper ) {
        this.authoritiesMapper = authoritiesMapper;
    }
	
	/**
     * Handles the creation of the final <tt>Authentication</tt> object which will be returned by the provider.
     * <p>
     * The default implementation just creates a new OpenIDAuthenticationToken from the original, but with the
     * UserDetails as the principal and including the authorities loaded by the UserDetailsService.
     *
     * @param userDetails the loaded UserDetails object
     * @param auth the token passed to the authenticate method, containing
     * @return the token which will represent the authenticated user.
     */
    Authentication createSuccessfulAuthentication( UserDetails userDetails, SocialAuthenticationToken auth ) {
        return new SocialAuthenticationToken( userDetails, authoritiesMapper.mapAuthorities(userDetails.getAuthorities()),
                auth.getIdentityUrl(), auth.getAttributes() );
    }
	
}
