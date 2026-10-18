package amu.ter.cpf.dao;

import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.model.entity.Contact;
import amu.ter.cpf.model.entity.Formation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContactDAO extends JpaRepository<Contact, String> {
  Optional<Contact> findByCodeContact(String CodeContact);
  List<Contact> findByComposante(Composante composante);


}
