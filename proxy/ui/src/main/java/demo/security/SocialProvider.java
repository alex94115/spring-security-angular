package demo.security;

public enum SocialProvider {
	
	FACEBOOK( "facebook" ),
	GOOGLE_PLUS( "googlePlus" );
	
	private final String provider;
	
    private SocialProvider( String provider ) {
        this.provider = provider;
    }

    public String toString() {
        return provider;
    }

}
