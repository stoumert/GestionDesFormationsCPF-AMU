package amu.ter.cpf.dao;

import amu.ter.cpf.model.entity.Composante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ComposanteDAO extends JpaRepository<Composante, String> {

    Optional<Composante> findByName(String name);
    Optional<Composante> findByCodeComposante(String codeComposante);
    @Query("""
    SELECT c FROM Composante c
    LEFT JOIN FETCH c.users
    WHERE c.codeComposante = :code
""")
    Composante findByCodeWithUsers(@Param("code") String code);
}
