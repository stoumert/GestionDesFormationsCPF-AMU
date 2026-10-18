package amu.ter.cpf.config;

import amu.ter.cpf.model.entity.Role;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private final AuthenticationProvider authenticationProvider;

    private final AuthenticationEntryPoint entryPoint;

    private final CasAuthenticationFilter casAuthenticationFilter;

    private final SingleSignOutFilter singleSignOutFilter;

    private final LogoutFilter requestSingleLogoutFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        String[] anonymousRequests = {
                "/login/cas", "/favicon.ico", "/error"
        };
        String[] mangerRequests = {
                "/formation/create",
                "/formation/create",
                "/formation/*/edit",
                "/formation/*/validate",
                "/formation/brouillons",
                "/formation/view/**",

        };
        String[] adminRequests = {
                "/composante/**",
                "/export",
        };

        http.authorizeHttpRequests(config -> {//
            config.dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll();
            // Pour tous
            config.requestMatchers(anonymousRequests).permitAll();
            // pour les managers et les admins
            config.requestMatchers(mangerRequests).hasAnyRole(Role.MANAGER.name(), Role.ADMIN.name());//
            // Pour les admins exclusivement
            config.requestMatchers(adminRequests).hasRole(Role.ADMIN.name());//
            // Pour les autres
            config.anyRequest().authenticated();
        });
        http.authenticationProvider(authenticationProvider);
        http.exceptionHandling(config -> {
            config.authenticationEntryPoint(entryPoint);
        });
        // Add filters to perform single sign on processing logic
        http.addFilter(casAuthenticationFilter);
        // Process authentication logic
        http.addFilterBefore(singleSignOutFilter, CasAuthenticationFilter.class);
        // Process logout
        http.addFilterBefore(requestSingleLogoutFilter, LogoutFilter.class);
        // Nous activons CSRF pour les actions protégées
        http.csrf(config -> config.ignoringRequestMatchers(mangerRequests));
        return http.build();
    }





}
