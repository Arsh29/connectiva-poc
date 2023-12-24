package com.connectiva.poc.cofiguration;

import com.connectiva.poc.security.JWTAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {
    @Autowired
    private AuthenticationEntryPoint point;
    @Autowired
    private JWTAuthenticationFilter filter;
    @Autowired
    UserDetailsService userDetailsService;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Bean
    public SecurityFilterChain  filterChain(HttpSecurity http) throws Exception{
        http.csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth-> auth.requestMatchers("/admin/**").authenticated()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/signup").permitAll()
                        .anyRequest().authenticated())
                .exceptionHandling(ex->ex.authenticationEntryPoint(point))
                .sessionManagement(session->session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.addFilterBefore(filter,UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    DaoAuthenticationProvider daoAuthenticationProvider()
    {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;

    }
}
