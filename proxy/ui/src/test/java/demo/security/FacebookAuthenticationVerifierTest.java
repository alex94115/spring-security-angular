package demo.security;

import static org.junit.Assert.*;

import java.net.URI;

import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;

import demo.security.SocialAuthenticationToken;
import demo.security.SocialAuthenticationVerifier;

public class FacebookAuthenticationVerifierTest {

	private static final String VALID_ACCESS_TOKEN = 
			"CAAVz39QYHwkBANdWniwBW93ohVtYmpisXkPCrynUDPfYHeSA7YLDwsC1ZBjDaYXyUHaB8YcfpbL9LBJuBIqlZBH81X3LgVZBgBrbgwRlQWho36U9ftyJ2wytu9RF7FjMZBGPjHcMsJYrgQS4HnElMwfAwbsMwy0ZCqZCZBGDdDgwPbKDVHPhqThziEtGI2zA6X1fWzsGnXKjHuQg8uxh0LLqY8QdzfS99wZD";

	private SocialAuthenticationVerifier verifier;
	
	@Before
	public void before() {
		this.verifier = new SocialAuthenticationVerifier();
		ReflectionTestUtils.setField( this.verifier, "facebookAppAccessToken", "1534780056805129|3sSmXbRKC1q_GG5atvoYEe5sT4g" );
	}
	
	@Test
	public void testValidFbToken() throws Exception {

		String userId = "facebook/1383352035311024";
		//String accessToken = "garbage";
		
		Authentication result = verifier.verifyToken( SocialProvider.FACEBOOK, userId, VALID_ACCESS_TOKEN );
		assertNotNull( result );
	}
	
	@Test
	public void testInvalidFbToken() throws Exception {
		
		String userId = "facebook/1383352035311024";
		String garbageAccessToken = "garbage";
		
		SocialAuthenticationToken result = verifier.verifyToken( SocialProvider.FACEBOOK, userId, garbageAccessToken );
		assertEquals( AuthenticationStatus.FAILURE, result.getStatus() );
	}
	
	@Test
	public void testInvalidFbUserId() throws Exception {
		
		String garbageUserId = "facebook/garbage";
		
		try {
			Authentication result = verifier.verifyToken( SocialProvider.FACEBOOK, garbageUserId, VALID_ACCESS_TOKEN );
			fail( "Should have thrown an error." );
		} catch( AuthenticationServiceException e ) {
			// pass
		}
	}
	
	@Test
	public void testValidGPlusToken() throws Exception {
		
//		String userId = "";
//		URIBuilder builder = new URIBuilder( String.format( "https://www.googleapis.com/plus/v1/people/me", userId ));
//		
//		// send the request using the application's access token
//		builder.addParameter( "access_token", "ya29.HgFt2JJTYUiFWndU47ssBVz79kgykht96ZK1J6lUp-fhCIvz95A7kMdBA1ptjgsPhSGkISRTIFio9Q" );
//		
//		URI uri = builder.build();
//		RestTemplate restTemplate = new RestTemplate();
//		JsonNode response = restTemplate.getForObject( uri, JsonNode.class );
//		assertNotNull( response );
		
		
		String accessToken = "ya29.HwG2nU3rVRm3UBedKCjruqxgc318pr8-1sWDZNO_HuFxmgTksyTpop1AtneqqzMSvoZdNt6ZoEEVjQ";
		
		URIBuilder builder = new URIBuilder( "https://www.googleapis.com/oauth2/v1/tokeninfo" );
		builder.addParameter( "access_token", accessToken );
		
		URI uri = builder.build();
		RestTemplate restTemplate = new RestTemplate();
		JsonNode response = restTemplate.getForObject( uri, JsonNode.class );
		assertNotNull( response );
		
	}

}
