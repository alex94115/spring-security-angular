package demo.security;

public enum AuthenticationStatus {

	SUCCESS("success"),
	FAILURE("failure");
	
	private final String name;
	
    private AuthenticationStatus(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}

