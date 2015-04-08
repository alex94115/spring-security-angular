package demo.validation;

import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import demo.domain.User;

public class UserValidator implements Validator {

	@Override
	public boolean supports( Class<?> clazz ) {
		return User.class.equals( clazz );
	}

	@Override
	public void validate( Object o, Errors e ) {
		e.reject( "Validation rejected the value." );
	}

}
