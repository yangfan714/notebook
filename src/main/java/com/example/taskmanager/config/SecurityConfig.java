package com.example.taskmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/", "/user/register", "/user/login", "/css/**", "/js/**", "/images/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/user/login")
                .loginProcessingUrl("/user/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/task/list", true)
                .failureUrl("/user/login?error=true")
                .permitAll()
            .and()
            .logout()
                .logoutUrl("/user/logout")
                .logoutSuccessUrl("/user/login")
                .permitAll()
            .and()
            .csrf()
                .ignoringAntMatchers("/user/register", "/user/login")
            .and()
            .headers()
                .contentTypeOptions()
                    .disable()
                .frameOptions()
                    .disable()
                .cacheControl()
                    .and()
                .xssProtection()
                    .and()
                .contentSecurityPolicy(csp -> csp
                    .policyDirectives("default-src 'self'; " +
                        "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdnjs.cloudflare.com https://fonts.googleapis.com; " +
                        "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net; " +
                        "font-src 'self' https://cdnjs.cloudflare.com https://cdn.jsdelivr.net https://fonts.googleapis.com https://fonts.gstatic.com; " +
                        "img-src 'self' data: https://cdn.jsdelivr.net https://cdnjs.cloudflare.com; " +
                        "connect-src 'self'; " +
                        "frame-src 'self'; " +
                        "object-src 'none'"));
        return http.build();
    }
} 