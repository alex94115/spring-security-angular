package demo.exception;

public class UserExistsException extends RuntimeException {

	private static final long serialVersionUID = 7466345256699264381L;

	public UserExistsException( String message ) {
		super( message );
	}
}
