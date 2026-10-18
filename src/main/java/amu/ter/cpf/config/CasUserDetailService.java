package amu.ter.cpf.config;

import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.apereo.cas.client.authentication.AttributePrincipal;
import org.springframework.security.cas.authentication.CasAssertionAuthenticationToken;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@AllArgsConstructor
public class CasUserDetailService implements AuthenticationUserDetailsService<@NonNull CasAssertionAuthenticationToken> {
    final String ATT_MAIL = "mail";
    final String ATT_AFFILIATION = "eduPersonPrimaryAffiliation";


    private final UserDAO userDAO;
    @Override
    public @NonNull UserDetails loadUserDetails(CasAssertionAuthenticationToken token) throws UsernameNotFoundException {
        AttributePrincipal principal = token.getAssertion().getPrincipal();
        Map<?, ?> attributes = principal.getAttributes();
        String email = attributes.get(ATT_MAIL).toString();

        User client;
        if (userDAO.findByEmail(email).isPresent()) {
            client = userDAO.findByEmail(email).get();
            client.setLoginCAS(token.toString());
            userDAO.save(client);
        }else {
        client = new User();
        client.setEmail(email);
        client.setLoginCAS(token.toString());
        client.setLastName(attributes.get("sn").toString());
        client.setFirstName(attributes.get("givenName").toString());
        client.setRole(Role.fromAffiliation(attributes.get(ATT_AFFILIATION).toString()));
        userDAO.save(client);
    }
        return org.springframework.security.core.userdetails.User.withUsername(client.getEmail())
                .password("")
                .disabled(false)
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .roles(client.getRole().name())
                .build();
    }
}
