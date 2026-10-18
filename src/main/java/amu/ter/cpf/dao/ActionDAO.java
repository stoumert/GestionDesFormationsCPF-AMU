package amu.ter.cpf.dao;

import amu.ter.cpf.model.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActionDAO extends JpaRepository<Action, String> {
    Optional<Action> findByCodeAction(String codeAction);
}