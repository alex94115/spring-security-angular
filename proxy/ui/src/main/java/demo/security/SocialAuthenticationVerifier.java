package demo.security;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.openid.OpenIDAttribute;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

public class SocialAuthenticationVerifier {

	private static final String GRAPH_API_URL = "https://graph.facebook.com/v2.2";
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@Value("${spring.facebook.app.accessToken}")
	private String facebookAppAccessToken;
	
	@Value("${spring.gplus.app.id}")
	private String gplusAppId;
	
	public SocialAuthenticationToken verifyToken( SocialProvider provider, String userId, String userAccessToken ) throws SocialAuthenticationException {
	
		try {
			
			List<OpenIDAttribute> attributes = null;
			
			if( SocialProvider.FACEBOOK.equals( provider )) {
			
				URIBuilder builder = new URIBuilder( String.format( "%s/debug_token", GRAPH_API_URL ));
				
				// send the request using the application's access token
				builder.addParameter( "access_token", this.facebookAppAccessToken );
				builder.addParameter( "input_token", userAccessToken );
				
				URI uri = builder.build();
				RestTemplate restTemplate = new RestTemplate();
				JsonNode response = restTemplate.getForObject( uri, JsonNode.class );
				Boolean isValid = response
									.path( "data" )
									.findValue("is_valid")
									.asBoolean();
				
				if( !isValid ) {
					return this.failureToken(userId);
				}
				
				String verifiedUserId = String.format( "facebook/%s", response
						.path( "data" )
						.findValue("user_id")
						.asText() );
				
				if( verifiedUserId == null || !verifiedUserId.equals( userId ) ) {
					throw new AuthenticationServiceException( String.format( "Presented identity: %s did not match verified identity: %s", userId, verifiedUserId ) );
				}
	
				attributes = this.fetchFacebookAttributes( userAccessToken, attributesToFetch() );
			} else if( SocialProvider.GOOGLE_PLUS.equals( provider ) ) {
				
				URIBuilder builder = new URIBuilder( "https://www.googleapis.com/oauth2/v1/tokeninfo" );
				builder.addParameter( "access_token", userAccessToken );
				
				URI uri = builder.build();
				RestTemplate restTemplate = new RestTemplate();
				JsonNode response = restTemplate.getForObject( uri, JsonNode.class );
				String actualAppId = response.path( "issued_to" ).asText();
				String verifiedUserId = String.format( "gplus/%s", response.path( "user_id" ).asText() );
				
				if( actualAppId == null ||
					!actualAppId.equals( this.gplusAppId ) ||
					verifiedUserId == null ||
					!verifiedUserId.equals( userId ) ) {
					
					throw new AuthenticationServiceException( String.format( "Presented identity: %s did not match verified identity: %s", userId, verifiedUserId ) );
				}
				
			} else {
				// error -- unknown provider
				throw new AuthenticationServiceException( String.format( "Unknown social provider: %s", provider ) );
			}
			
			return new SocialAuthenticationToken( AuthenticationStatus.SUCCESS, userId, "authentication suceeded", attributes );
			
		} catch( AuthenticationServiceException e ) {
			throw e;
		} catch( Exception e ) {
			throw new SocialAuthenticationException( "Error confirming token validity with provider", e );
		}
		
	}

	SocialAuthenticationToken failureToken(String userId) {
		return new SocialAuthenticationToken( AuthenticationStatus.FAILURE, 
				userId == null ? "Unknown" : userId,
				"Access token validation failure",
				Collections.<OpenIDAttribute>emptyList() );
	}
	
	List<OpenIDAttribute> attributesToFetch() {
		
		OpenIDAttribute firstname = new OpenIDAttribute( "first_name", "http://axschema.org/namePerson/first" );
		OpenIDAttribute lastname = new OpenIDAttribute( "last_name", "http://axschema.org/namePerson/last" );
		
		List<OpenIDAttribute> attributesToFetch = new ArrayList<OpenIDAttribute>();
		attributesToFetch.add( firstname );
		attributesToFetch.add( lastname );
		
		return attributesToFetch;
	}
	
	List<OpenIDAttribute> fetchFacebookAttributes( String userAcessToken, List<OpenIDAttribute> attributesToFetch ) throws URISyntaxException {
		
		URIBuilder builder = new URIBuilder( String.format( "%s/me", GRAPH_API_URL ));
		
		// send the request using the user's access token
		builder.addParameter( "access_token", userAcessToken );
		
		URI uri = builder.build();
		RestTemplate restTemplate = new RestTemplate();
		JsonNode response = restTemplate.getForObject( uri, JsonNode.class );
		
		List<OpenIDAttribute> fetchedAttributes = new ArrayList<OpenIDAttribute>(attributesToFetch.size());
		
		for( OpenIDAttribute attribute : attributesToFetch ) {
			
			List<String> values = new ArrayList<String>();
			values.add( response.findValue( attribute.getName() )
					.asText() );
			OpenIDAttribute fetched = new OpenIDAttribute( attribute.getName(), attribute.getType(), values );
			fetchedAttributes.add( fetched );
		}
		
		if( logger.isDebugEnabled() ) {
            logger.debug("Retrieved attributes" + fetchedAttributes );
        }
		
		return fetchedAttributes;
		
	}
	
}
