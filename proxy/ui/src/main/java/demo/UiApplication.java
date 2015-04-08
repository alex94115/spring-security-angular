package demo;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import demo.security.SocialAuthenticationProvider;
import demo.security.SocialAuthenticationToken;
import demo.security.SocialAuthenticationVerifier;
import demo.security.SocialTokenFilter;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableZuulProxy
@EnableRedisHttpSession
@EnableJpaRepositories
public class UiApplication {

	public static void main(String[] args) {
		SpringApplication.run(UiApplication.class, args);
	}
	
	@Configuration
	@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
	protected static class SecurityConfiguration extends WebSecurityConfigurerAdapter {
		
		@Autowired
		DataSource dataSource;
		
		@Autowired
	    private ObjectPostProcessor<Object> objectPostProcessor;

		
		/**
		 * Authentication Manager Builder
		 */
		
		@Bean(name="authenticationManager")
	    public AuthenticationManager authenticationManager() throws Exception {
	        return new AuthenticationManagerBuilder( objectPostProcessor )
	                .authenticationProvider( socialAuthProvider() ).build();
	    }
		
		@Bean(name="socialAuthenticationProvider")
	    public SocialAuthenticationProvider socialAuthProvider() {
			SocialAuthenticationProvider provider = new SocialAuthenticationProvider();
	        provider.setAuthenticationUserDetailsService(
	                new UserDetailsByNameServiceWrapper<SocialAuthenticationToken>( jdbcUserDetailsService() ));
	        return provider;
	    }
		
	    @Bean(name="jdbcUserDetailsService")
	    public UserDetailsService jdbcUserDetailsService() {
	    	JdbcUserDetailsManager judm = new JdbcUserDetailsManager();
	    	judm.setDataSource( dataSource );
	    	judm.setUsersByUsernameQuery( "SELECT username, '', enabled FROM users WHERE username=?" );
	    	judm.setAuthoritiesByUsernameQuery( "SELECT u.username, r.description FROM Users u JOIN users_roles USING (user_id) JOIN Roles r USING (role_id) WHERE u.username=?" );
	    	
	    	return judm;
	    }

		/**
		 * Social Filter
		 */
	    
	    @Bean(name="socialTokenFilter")
	    public SocialTokenFilter socialTokenFilter() throws Exception {
	    	SocialTokenFilter filter = new SocialTokenFilter();
	        filter.setAuthenticationManager( authenticationManager() );
	        filter.setAuthenticationVerifier( socialAuthenticationVerifier() );
	        return filter;
	    }
	    
	    @Bean(name="socialAuthenticationVerifier")
	    public SocialAuthenticationVerifier socialAuthenticationVerifier() {
	    	return new SocialAuthenticationVerifier();
	    }

	    @Bean(name="socialFilterReg")
	    public FilterRegistrationBean socialFilterReg() throws Exception {
	        FilterRegistrationBean registrationBean = new FilterRegistrationBean();
	        registrationBean.addUrlPatterns("/login/social");
	        registrationBean.setFilter( socialTokenFilter() );

	        return registrationBean;
	    }

	    @Bean(name="socialProcessingFilterEntryPoint")
	    public AuthenticationEntryPoint socialProcessingFilterEntryPoint() {
	        return new Http403ForbiddenEntryPoint();
	    }
	        
		@Override
		protected void configure( HttpSecurity http ) throws Exception {
			// @formatter:off
			http
				//.formLogin()
				//	.and()
				.logout()
					.and()
				.authorizeRequests()
					.antMatchers( HttpMethod.POST, "/resource/user", "/login/social" ).permitAll()
					.antMatchers( HttpMethod.GET, "/index.html", "/main.html", "/authentication.html", "/footer.html", "/header.html", "/profile/createProfile.html", "/" ).permitAll()
					.anyRequest().authenticated()
					.and()
				.csrf()
					.csrfTokenRepository(csrfTokenRepository())
					.and()
				.addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
			// @formatter:on
		}
		
		private Filter csrfHeaderFilter() {
			return new OncePerRequestFilter() {
				
				@Override
				protected void doFilterInternal( HttpServletRequest request, HttpServletResponse response, FilterChain filterChain ) throws ServletException, IOException {
					
					CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
					if (csrf != null) {
						Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
						String token = csrf.getToken();
						if (cookie == null || token != null
								&& !token.equals(cookie.getValue())) {
							cookie = new Cookie("XSRF-TOKEN", token);
							cookie.setPath("/");
							response.addCookie(cookie);
						}
					}
					filterChain.doFilter(request, response);
				}
			};
		}

		private CsrfTokenRepository csrfTokenRepository() {
			HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
			repository.setHeaderName("X-XSRF-TOKEN");
			return repository;
		}

	}
	
}
