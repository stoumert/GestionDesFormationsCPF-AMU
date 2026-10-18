package amu.ter.cpf.dao;

import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDAO extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);

    List<User> findAllByRole(Role role);
}
