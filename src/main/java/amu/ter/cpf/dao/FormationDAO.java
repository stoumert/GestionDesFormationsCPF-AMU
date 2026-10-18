package amu.ter.cpf.dao;

import amu.ter.cpf.model.entity.Contact;
import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.model.entity.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FormationDAO extends JpaRepository<Formation, String> {

    Optional<Formation> findByCodeFormation(String codeFormation);

    Optional<Formation> findByIntitule(String intitule);
    @Query("SELECT f FROM Formation f LEFT JOIN FETCH f.actions WHERE f.codeFormation = :codeFormation")
    Formation findByCodeFormationWithActions(@Param("codeFormation") String codeFormation);


    List<Formation> findByStatus(Status statut);

    @Query("""
    SELECT f FROM Formation f
    JOIN f.composante c
    WHERE (:intitule IS NULL OR LOWER(f.intitule) LIKE LOWER(CONCAT('%', :intitule, '%')))
    AND   (:code IS NULL OR LOWER(f.codeFormation) LIKE LOWER(CONCAT('%', :code, '%')))
    AND   (:codeComposante IS NULL OR c.codeComposante = :codeComposante)
    AND   (:typeCertification IS NULL OR f.typeCertification = :typeCertification)
    AND   (:codeCertification IS NULL OR LOWER(f.codeCertification) LIKE LOWER(CONCAT('%', :codeCertification, '%')))
  """)
    List<Formation> search(
            @Param("intitule") String intitule,
            @Param("code") String code,
            @Param("codeComposante") String codeComposante,
            @Param("typeCertification") String typeCertification,
            @Param("codeCertification") String codeCertification
    );


    List<Formation> findByContact(Contact contact);
    Optional<Formation> findByFormationParente(Formation formation);
}

