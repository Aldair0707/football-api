package com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.security;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.security.jwt.AuthEntryPointJwt;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.security.jwt.AuthTokenFilter;
import com.aldair.spring.security.postgresql.SpringBootSecurityPostgresqlApplication.security.services.UserDetailsServiceImpl;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
// (securedEnabled = true,
// jsr250Enabled = true,
// prePostEnabled = true) // by default
public class WebSecurityConfig {
  @Autowired
  UserDetailsServiceImpl userDetailsService;

  @Autowired
  private AuthEntryPointJwt unauthorizedHandler;

  //@Autowired
  //private AuthTokenFilter authTokenFilter;


  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
   return new AuthTokenFilter();
  }

  @Bean
  public DaoAuthenticationProvider authenticationProvider() {
      DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
       
      authProvider.setUserDetailsService(userDetailsService);
      authProvider.setPasswordEncoder(passwordEncoder());
   
      return authProvider;
  }
 
  @Bean
  public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
    return authConfig.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
  
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
   http.csrf(csrf -> csrf.disable())
       .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
       .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
       .authorizeHttpRequests(auth ->
         auth.requestMatchers("/api/auth/**").permitAll()
             .requestMatchers("/api/test/**").permitAll()
             .requestMatchers("/api/publicaciones/**").authenticated()
             .requestMatchers("/api/publicaciones").authenticated()
             .requestMatchers("/api/comentarios/**").authenticated()
             .requestMatchers("/api/reacciones/**").authenticated()
             .anyRequest().denyAll()
       );
  
   http.authenticationProvider(authenticationProvider());
   http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

    
    return http.build();
  }
}