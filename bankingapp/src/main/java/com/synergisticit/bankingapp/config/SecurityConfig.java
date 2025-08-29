package com.synergisticit.bankingapp.config;

import com.synergisticit.bankingapp.service.DbUserDetailsService;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final DbUserDetailsService userDetailsService;

    @Bean
    BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    DaoAuthenticationProvider daoAuthProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(userDetailsService);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // If you’ll re-enable CSRF later, remember to add the token to all POST forms.
            .csrf(csrf -> csrf.disable())

            // THIS prevents the /WEB-INF/... JSP forward from being secured → fixes the redirect loop.
            .authorizeHttpRequests(auth -> auth
                .dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()

                // Public endpoints
                .requestMatchers("/auth/login", "/403", "/error",
                                 "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()

                // Admin-only
                .requestMatchers("/roles/**").hasRole("ADMIN")
                .requestMatchers("/branches/new", "/branches/edit/**", "/branches/delete/**").hasRole("ADMIN")

                // Everything else requires authentication
                .anyRequest().authenticated()
            )

            // Register our DAO auth provider (explicit = clearer)
            .authenticationProvider(daoAuthProvider())

            .formLogin(login -> login
                // Unique login page path (GET renders JSP)
                .loginPage("/auth/login")
                // POST processing URL (must match login form action)
                .loginProcessingUrl("/auth/login")
                // Redirect (not forward) after success
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login?logout")
                .permitAll()
            )

            // Don’t send 403 to a protected page
            .exceptionHandling(ex -> ex.accessDeniedPage("/403"))

            // Optional: avoid caching /WEB-INF/... as a saved request (removes noise)
            .requestCache(cache -> cache.disable());

        return http.build();
    }
}
