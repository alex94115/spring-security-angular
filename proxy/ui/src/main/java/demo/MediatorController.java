package demo;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MediatorController {
	
	private final Log logger = LogFactory.getLog(getClass());
	
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}
	
	@RequestMapping("/token")
	//@ResponseBody
	public Map<String, String> token(HttpSession session) {
		return Collections.singletonMap("token", session.getId());
	}

//	@RequestMapping("/signup")
//	//@ResponseBody
//	public Boolean signup(@Valid @RequestBody SocialUserDTO dto, BindingResult bindingResult ) {
//		
//		this.logger.error( "Received the sign up form with socialUser: " + dto );
//		
//		// TODO validate the form
//		
//		// call the user service
//		return this.userService.signUp( dto );
//	}
	
	/**
	 * Handle a UserExistsException by returning an HTTP 422 
	 * response code to indicate that signing up with this
	 * combination of site and site user id is not possible.
	 * 
	 * @param e
	 * @return
	 */
//	@ExceptionHandler(UserExistsException.class)
//	@ResponseStatus(value = HttpStatus.UNPROCESSABLE_ENTITY)
//	@ResponseBody
//	public String handleUserExistsException( UserExistsException e ) {
//		return e.getMessage();
//	}

}
