package demo.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

public class SocialTokenFilter extends AbstractAuthenticationProcessingFilter {

	
	private static final String SOCIAL_PROVIDER_FORM_KEY = "social_provider";
	
	private static final String FACEBOOK_USER_ID_FORM_KEY = "facebook_userId";
    private static final String FACEBOOK_ACCESS_TOKEN_FORM_KEY = "facebook_accessToken";
    
    private static final String GOOGLE_PLUS_USER_ID_FORM_KEY = "gPlus_userId";
    private static final String GOOGLE_PLUS_ACCESS_TOKEN_FORM_KEY = "gPlus_accessToken";
	
    
    private static Log logger = LogFactory.getLog(SocialTokenFilter.class);
	
	private SocialAuthenticationVerifier verifier;
	
    public SocialTokenFilter() {
        super( "/login/social" );
    }
    
	public void setAuthenticationVerifier( SocialAuthenticationVerifier verifier ) {
		this.verifier = verifier;
	}
	
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		logger.debug( "Inside the FacebookTokenFilter" );
		
		SocialAuthenticationToken token;
		
		if( !request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }
		
		SocialProvider provider = this.getProvider( request );
		String accessToken = this.getAccessToken( provider, request );
		String userId = this.getUserId( provider, request );
		
		if (logger.isDebugEnabled()) {
            logger.debug( String.format( "Presented accessToken: %s and userId: %s for provdier: %s", accessToken, userId, provider ) );
        }
		
		try {
			token = this.verifier.verifyToken( provider, userId, accessToken );
		} catch( SocialAuthenticationException e ) {
			throw new AuthenticationServiceException( "Access token error", e );
		}
		
		token.setDetails( authenticationDetailsSource.buildDetails( request ));
		
		// Delegate to the authentication provider
		return this.getAuthenticationManager().authenticate( token );
	}
	
	String getAccessToken( SocialProvider provider, HttpServletRequest request ) {
		String result = "";
		
		if( provider.equals( SocialProvider.FACEBOOK ) ) {
			result = request.getParameter( FACEBOOK_ACCESS_TOKEN_FORM_KEY ); 
		} else {
			result = request.getParameter( GOOGLE_PLUS_ACCESS_TOKEN_FORM_KEY );
		}
		
		return result;
	}
	
	String getUserId( SocialProvider provider, HttpServletRequest request ) {
		String result = "";
		
		if( provider.equals( SocialProvider.FACEBOOK ) ) {
			result = String.format( "facebook/%s", request.getParameter( FACEBOOK_USER_ID_FORM_KEY ) );
		} else {
			result = String.format( "gplus/%s", request.getParameter( GOOGLE_PLUS_USER_ID_FORM_KEY ) );
		}
		
		return result;
	}
	
	SocialProvider getProvider( HttpServletRequest request ) {
		return  SocialProvider.valueOf( request.getParameter( SOCIAL_PROVIDER_FORM_KEY ) );
	}
}
