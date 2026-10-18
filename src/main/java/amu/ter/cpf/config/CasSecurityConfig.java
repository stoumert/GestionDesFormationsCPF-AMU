package amu.ter.cpf.config;

import lombok.NonNull;
import org.apereo.cas.client.session.SingleSignOutFilter;
import org.apereo.cas.client.validation.Cas30ProxyTicketValidator;
import org.apereo.cas.client.validation.TicketValidator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.Collections;

@Configuration
public class CasSecurityConfig {

    @Value("${cas.server.prefix}")
    private String casServerPrefix;

    @Value("${cas.server.login}")
    private String casServerLogin;

    @Value("${cas.server.logout}")
    private String casServerLogout;

    @Value("${cas.client.login}")
    private String casClientLogin;

    @Value("${cas.client.logout}")
    private String casClientLogout;

    @Value("${cas.client.relative}")
    private String casClientLogoutRelative;

    @Bean
    public ServiceProperties serviceProperties() {
        ServiceProperties serviceProperties = new ServiceProperties();
        serviceProperties.setService(casClientLogin);
        serviceProperties.setSendRenew(false);
        return serviceProperties;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint(ServiceProperties serviceProperties) {
        CasAuthenticationEntryPoint entryPoint = new CasAuthenticationEntryPoint();
        entryPoint.setLoginUrl(casServerLogin);
        entryPoint.setServiceProperties(serviceProperties);
        return entryPoint;
    }
    @Bean
    public TicketValidator ticketValidator() {
        return new Cas30ProxyTicketValidator(casServerPrefix);
    }
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider(ServiceProperties sp, TicketValidator ticketValidator,
                                                               @Qualifier("casUserDetailService") AuthenticationUserDetailsService<@NonNull CasAssertionAuthenticationToken> userDetailsService) {
        CasAuthenticationProvider provider = new CasAuthenticationProvider();
        provider.setServiceProperties(sp);
        provider.setTicketValidator(ticketValidator);
        provider.setAuthenticationUserDetailsService(userDetailsService);
        provider.setKey("yyg");
        return provider;
    }
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter(ServiceProperties sp, AuthenticationProvider ap) {
        CasAuthenticationFilter filter = new CasAuthenticationFilter();
        filter.setServiceProperties(sp);
        filter.setAuthenticationManager(new ProviderManager(Collections.singletonList(ap)));
        return filter;
    }

    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    @Bean
    public LogoutFilter logoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casServerLogout, new SecurityContextLogoutHandler());
        // Set the path of the client logout request
        logoutFilter.setFilterProcessesUrl(casClientLogoutRelative);
        return logoutFilter;
    }

}
