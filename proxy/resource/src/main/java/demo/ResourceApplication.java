package demo;

import demo.domain.User;
import demo.validation.UserValidator;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@RestController
@EnableRedisHttpSession
class ResourceApplication extends WebSecurityConfigurerAdapter {

	@RequestMapping("/")
	public Map<String, String> home() {
		Map<String, String> map = new HashMap<String, String>();
		map.put("id", UUID.randomUUID().toString());
		map.put("content", "Hello alex!");

		return map;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().disable();
		http.authorizeRequests().anyRequest().authenticated();
	}
	
	@Bean
	public UserValidator beforeCreateUserValidator() {
		return new UserValidator();
	}

	public static void main(String[] args) {
		SpringApplication.run(ResourceApplication.class, args);
	}

}
