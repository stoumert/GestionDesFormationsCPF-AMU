package amu.ter.cpf.dao;

import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionDAO extends JpaRepository<Session, String> {
  List<Session> findByAction(Action action);
  Optional<Session>findByCodeSession(String codeSession);
}
