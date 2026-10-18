package amu.ter.cpf.service;

import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDAO userDAO;

    @PostConstruct
    public void init() {
        User aa = new User();
        aa.setRole(Role.ADMIN);
        aa.setFirstName("admin");
        aa.setLastName("admin");
        aa.setEmail("yanis.liassine@etu.univ-amu.fr");
        aa.setLoginCAS("ABCDEFGH");

        User bb = new User();
        bb.setRole(Role.MANAGER);
        bb.setFirstName("Said");
        bb.setLastName("Toumert");
        bb.setEmail("said.toumert@etu.univ-amu.fr");
        bb.setLoginCAS("ABCDEFGH");

        User cc = new User();
        cc.setRole(Role.MANAGER);
        cc.setFirstName("manager2");
        cc.setLastName("manager2");
        cc.setEmail("manager@test2.com");
        cc.setLoginCAS("ABCDEFGH");

        userDAO.save(aa);
        userDAO.save(bb);
        userDAO.save(cc);
    }
    public List<User> getManagers() {
        return userDAO.findAllByRole(Role.MANAGER);
    }

    public List<User> getManagersWithoutAssignComposante(){
        return getManagers().stream().filter(manager ->
                manager.getComposante() == null).collect(Collectors.toList());
    }
    public User getUserById(Integer id) {
        return userDAO.findById(id).orElse(null);
    }

    public User getUserByEmail(String email) {
        return userDAO.findByEmail(email)
                .orElseThrow(
                        () -> new IllegalArgumentException(
                                "Utilisateur introuvable : " + email
                        )
                );
    }
}
