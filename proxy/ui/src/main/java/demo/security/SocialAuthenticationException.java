package demo.security;

public class SocialAuthenticationException extends Exception {
	
	private static final long serialVersionUID = 8688682690189058257L;

	public SocialAuthenticationException() {}
	
	public SocialAuthenticationException( String message, Throwable e ) {
		super( message, e );
	}

}
