package com.newbusiness.one4all.config;

import com.newbusiness.one4all.service.MemberService;
import com.newbusiness.one4all.util.JwtRequestFilter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;

import java.util.Arrays;

@Configuration
public class SecurityConfigurer {
	@Autowired
	@Lazy
	private final JwtRequestFilter jwtRequestFilter;
	// private final MemberService memberService;
	@Autowired
	@Lazy
	private UserDetailsService userDetailsService;
	// Inject CORS properties from application-{profile}.properties
	@Value("${cors.allowed.origins}")
	private String[] allowedOrigins;

	@Value("${cors.allowed.methods}")
	private String[] allowedMethods;

	public SecurityConfigurer(JwtRequestFilter jwtRequestFilter, UserDetailsService userDetailsService) {
		this.jwtRequestFilter = jwtRequestFilter;
		this.userDetailsService = userDetailsService;
	}

	// Replacing WebSecurityConfigurerAdapter with SecurityFilterChain
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf().disable() // Disable CSRF for stateless APIs
				.cors().and() // Enable CORS
				.authorizeRequests().requestMatchers("/api/register", "/api/login","/api/bulk-register").permitAll() // Publicly accessible
																								// endpoints
				.anyRequest().authenticated() // All other endpoints require authentication
				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
				.authenticationProvider(authenticationProvider()) // Use authenticationProvider
				.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Add JWT filter before
																								// UsernamePasswordAuthenticationFilter
																								// // Stateless session
																								// management
		return http.build();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		// Use BCryptPasswordEncoder for secure password storage
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
			throws Exception {
		return authenticationConfiguration.getAuthenticationManager();
	}

	// CORS configuration for allowing frontend domains to communicate with backend
	@Bean
	public CorsFilter corsFilter() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(Arrays.asList(allowedOrigins)); // Allowed origins
		config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
		config.setAllowedMethods(Arrays.asList(allowedMethods));
		config.setAllowCredentials(true); // Allow credentials (e.g., cookies, authorization headers)

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
}
