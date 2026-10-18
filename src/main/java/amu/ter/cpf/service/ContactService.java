package amu.ter.cpf.service;
import amu.ter.cpf.dao.ComposanteDAO;
import amu.ter.cpf.dao.ContactDAO;
import amu.ter.cpf.dao.FormationDAO;
import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.*;
import jakarta.transaction.Transactional;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Objects;

import static amu.ter.cpf.model.entity.Status.BROUILLON;


@Service
@RequiredArgsConstructor

public class ContactService {

  private final ContactDAO contactDAO;
  private final ComposanteDAO composanteDAO;
  private final FormationDAO formationDAO;
  private final UserDAO userDAO;

  public Contact getContactByCodeContact(String codeContact) {
    return contactDAO.findByCodeContact(codeContact).orElse(null);
  }

  public List<Contact> getContactsByComposante(Composante composante) {
    if (composante == null) {
      return List.of();
    }
    return contactDAO.findByComposante(composante);
  }


  @PreAuthorize("hasRole('MANAGER')")
  @Transactional
  public Contact create(Contact contact, User manager) {

    if (manager == null) {
      throw new IllegalArgumentException("Gestionnaire introuvable");
    }

    if (manager.getComposante() == null) {
      throw new IllegalStateException(
              "Le gestionnaire n'est rattaché à aucune composante");
    }

    Composante composante = manager.getComposante();

    contact.setComposante(composante);

    Contact savedContact = contactDAO.save(contact);

    composante.getContacts().add(savedContact);

    composanteDAO.save(composante);

    return savedContact;
  }


public User getManager(){
  Authentication authentication =
          SecurityContextHolder.getContext().getAuthentication();

    return userDAO.findByEmail(
            Objects.requireNonNull(authentication).getName()
    ).orElseThrow();
}




  @PreAuthorize("hasRole('MANAGER')")
  @Transactional
  public void update(String oldCode, Contact submitted) {

    Contact existing = contactDAO.findByCodeContact(oldCode)
            .orElseThrow(() -> new IllegalArgumentException("Contact introuvable: " + oldCode));

    User manager = getManager();


    if (!existing.getComposante().getCodeComposante()
            .equals(manager.getComposante().getCodeComposante())) {
      throw new SecurityException("Modification interdite");
    }


    submitted.setComposante(existing.getComposante());
    submitted.setContactParent(existing.getContactParent());


    if (!oldCode.equals(submitted.getCodeContact())) {


      Contact newContact = new Contact();

      newContact.setCodeContact(submitted.getCodeContact());
      applyAllFields(newContact, submitted);


      newContact.setComposante(existing.getComposante());
      newContact.setContactParent(existing.getContactParent());

      contactDAO.delete(existing);
      contactDAO.flush();

      contactDAO.save(newContact);
      return;
    }

    applyAllFields(existing, submitted);


    existing.setComposante(existing.getComposante());

    contactDAO.save(existing);
  }

  @PreAuthorize("hasRole('MANAGER')")
  public void delete(
          String codeContact) {

    Contact contact = loadOrThrow(codeContact);
    User manager = getManager();

    if (!contact.getComposante()
            .getCodeComposante()
            .equals(
                    manager.getComposante()
                            .getCodeComposante())) {

      throw new SecurityException(
              "Suppression interdite");
    }
    List<Formation> formations = formationDAO.findByContact(contact);
    for(Formation formation : formations) {
      formation.setContact(null);
      formation.setStatus(BROUILLON);
      formationDAO.save(formation);
    }
    Composante composante = manager.getComposante();
    composante.removeContact(contact);

    contactDAO.delete(contact);
  }



  private Contact loadOrThrow(String CodeContact) {
    return contactDAO.findByCodeContact(CodeContact)
        .orElseThrow(() -> new IllegalArgumentException("Contact not found: " + CodeContact));
  }



  private void applyAllFields(Contact target, Contact source) {

    target.setNom(source.getNom());
    target.setPrenom(source.getPrenom());
    target.setTelephone(source.getTelephone());
    target.setCourriel(source.getCourriel());
    target.setFonction(source.getFonction());

    target.setAdresseLigne1(source.getAdresseLigne1());
    target.setAdresseLigne2(source.getAdresseLigne2());
    target.setNumeroVoie(source.getNumeroVoie());
    target.setCodeIndiceRepetition(source.getCodeIndiceRepetition());
    target.setCodeNatureVoie(source.getCodeNatureVoie());
    target.setLibelleVoie(source.getLibelleVoie());
    target.setCodePostal(source.getCodePostal());
    target.setVille(source.getVille());
    target.setComposante(source.getComposante());
  }
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @Transactional
  public void assignContactToFormation(String codeContact, String codeFormation) {

    Contact contact = contactDAO.findByCodeContact(codeContact)
        .orElseThrow(() -> new IllegalArgumentException(
            "Contact introuvable : " + codeContact
        ));

    Formation formation = formationDAO.findByCodeFormation(codeFormation)
        .orElseThrow(() -> new IllegalArgumentException(
            "Formation introuvable : " + codeFormation
        ));

    if (formation.getStatus() != BROUILLON) {
      throw new IllegalStateException(
          "Vous pouvez assigner un contact à une formation déjà publier , créer un brouillons en premier ."
      );
    }

    if (contact.getComposante() == null || formation.getComposante() == null) {
      throw new IllegalStateException(
          "Le contact ou la formation n'est rattaché à aucune composante."
      );
    }

    String contactComponentCode = contact.getComposante().getCodeComposante();
    String formationComponentCode = formation.getComposante().getCodeComposante();

    if (!contactComponentCode.equals(formationComponentCode)) {
      throw new SecurityException(
          "Le contact ne peut être assigné qu'à une formation de la même composante."
      );
    }
    formation.setContact(contact);
    formationDAO.save(formation);
  }

}
