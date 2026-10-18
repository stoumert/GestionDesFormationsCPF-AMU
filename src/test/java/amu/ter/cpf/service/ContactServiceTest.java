package amu.ter.cpf.service;

import amu.ter.cpf.dao.ComposanteDAO;
import amu.ter.cpf.dao.ContactDAO;
import amu.ter.cpf.dao.FormationDAO;
import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static amu.ter.cpf.model.entity.Status.ACTIF;
import static amu.ter.cpf.model.entity.Status.BROUILLON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class ContactServiceTest {

    @MockitoBean
    private ContactDAO contactDAO;

    @MockitoBean
    private ComposanteDAO composanteDAO;

    @MockitoBean
    private FormationDAO formationDAO;

    @MockitoBean
    private UserDAO userDAO;

    @Autowired
    private ContactService contactService;

    private Contact contact;
    private Composante composante;
    private User manager;

    @BeforeEach
    void setUp() {
        composante = new Composante();
        composante.setCodeComposante("SCI");
        composante.setName("Science");

        manager = new User();
        manager.setEmail("manager@test.com");
        manager.setRole(Role.MANAGER);
        manager.setComposante(composante);

        contact = new Contact();
        contact.setCodeContact("CONTACT001");
        contact.setNom("Dupont");
        contact.setPrenom("Jean");
        contact.setTelephone("0413949799");
        contact.setCourriel("dupont@univ-amu.fr");
        contact.setComposante(composante);
    }

    @Test
    void getContactByCodeShouldReturnContact() {
        when(contactDAO.findByCodeContact("CONTACT001"))
                .thenReturn(Optional.of(contact));

        Contact result = contactService.getContactByCodeContact("CONTACT001");

        assertNotNull(result);
        assertEquals("CONTACT001", result.getCodeContact());
    }

    @Test
    void getContactByCodeShouldReturnNullIfNotFound() {
        when(contactDAO.findByCodeContact("CONTACT404"))
                .thenReturn(Optional.empty());

        Contact result = contactService.getContactByCodeContact("CONTACT404");

        assertNull(result);
    }

    @Test
    void getContactsByComposanteShouldReturnList() {
        when(contactDAO.findByComposante(composante))
                .thenReturn(List.of(contact));

        List<Contact> result = contactService.getContactsByComposante(composante);

        assertEquals(1, result.size());
        assertEquals("CONTACT001", result.get(0).getCodeContact());
    }

    @Test
    void getContactsByComposanteShouldReturnEmptyIfComposanteIsNull() {
        List<Contact> result = contactService.getContactsByComposante(null);

        assertTrue(result.isEmpty());
        verify(contactDAO, never()).findByComposante(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createShouldSaveContactAndLinkToComposante() {
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));
        when(contactDAO.save(any(Contact.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(composanteDAO.save(any(Composante.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Contact result = contactService.create(contact, manager);

        assertNotNull(result);
        assertEquals(composante, result.getComposante());
        verify(contactDAO).save(any(Contact.class));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createShouldFailIfManagerIsNull() {
        assertThrows(IllegalArgumentException.class,
                () -> contactService.create(contact, null));

        verify(contactDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createShouldFailIfManagerHasNoComposante() {
        manager.setComposante(null);

        assertThrows(IllegalStateException.class,
                () -> contactService.create(contact, manager));

        verify(contactDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void createShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> contactService.create(contact, manager));

        verify(contactDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void updateShouldApplyChanges() {
        when(contactDAO.findByCodeContact("CONTACT001"))
                .thenReturn(Optional.of(contact));
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));
        when(contactDAO.save(any(Contact.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Contact submitted = new Contact();
        submitted.setCodeContact("CONTACT001");
        submitted.setNom("Martin");
        submitted.setPrenom("Paul");
        submitted.setTelephone("0491000000");
        submitted.setCourriel("martin@univ-amu.fr");

        contactService.update("CONTACT001", submitted);

        verify(contactDAO).save(any(Contact.class));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void updateShouldFailIfContactNotFound() {
        when(contactDAO.findByCodeContact("CONTACT404"))
                .thenReturn(Optional.empty());
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));

        assertThrows(IllegalArgumentException.class,
                () -> contactService.update("CONTACT404", contact));

        verify(contactDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void updateShouldFailIfContactBelongsToAnotherComposante() {
        Composante autre = new Composante();
        autre.setCodeComposante("DROIT");
        contact.setComposante(autre);

        when(contactDAO.findByCodeContact("CONTACT001"))
                .thenReturn(Optional.of(contact));
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));

        assertThrows(SecurityException.class,
                () -> contactService.update("CONTACT001", contact));

        verify(contactDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void updateShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> contactService.update("CONTACT001", contact));

        verify(contactDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void deleteShouldRemoveContact() {
        when(contactDAO.findByCodeContact("CONTACT001"))
                .thenReturn(Optional.of(contact));
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));

        contactService.delete("CONTACT001");

        verify(contactDAO).delete(contact);
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void deleteShouldFailIfContactNotFound() {
        when(contactDAO.findByCodeContact("CONTACT404"))
                .thenReturn(Optional.empty());
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));

        assertThrows(IllegalArgumentException.class,
                () -> contactService.delete("CONTACT404"));

        verify(contactDAO, never()).delete(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void deleteShouldFailIfContactBelongsToAnotherComposante() {
        Composante autre = new Composante();
        autre.setCodeComposante("DROIT");
        contact.setComposante(autre);

        when(contactDAO.findByCodeContact("CONTACT001"))
                .thenReturn(Optional.of(contact));
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));

        assertThrows(SecurityException.class,
                () -> contactService.delete("CONTACT001"));

        verify(contactDAO, never()).delete(any());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void deleteShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> contactService.delete("CONTACT001"));

        verify(contactDAO, never()).delete(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void assignContactToFormationShouldAssignContact() {
        when(contactDAO.findByCodeContact("CONTACT001"))
            .thenReturn(Optional.of(contact));
        Formation formation = new Formation();
        formation.setCodeFormation("FORM001");
        formation.setIntitule("Master Informatique");
        formation.setStatus(BROUILLON);
        formation.setComposante(composante);

        when(formationDAO.findByCodeFormation("FORM001"))
            .thenReturn(Optional.of(formation));
        contactService.assignContactToFormation("CONTACT001", "FORM001");
        assertEquals(contact, formation.getContact());
        verify(formationDAO).save(formation);
    }
    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void assignContactToFormationShouldFailIfContactNotFound() {
        when(contactDAO.findByCodeContact("CONTACT404"))
            .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
            () -> contactService.assignContactToFormation("CONTACT404", "FORM001"));
        verify(formationDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void assignContactToFormationShouldFailIfFormationIsNotDraft() {
        when(contactDAO.findByCodeContact("CONTACT001"))
            .thenReturn(Optional.of(contact));
        Formation formation = new Formation();
        formation.setCodeFormation("FORM001");
        formation.setIntitule("Master Informatique");
        formation.setStatus(ACTIF);
        formation.setComposante(composante);

        when(formationDAO.findByCodeFormation("FORM001"))
            .thenReturn(Optional.of(formation));

        assertThrows(IllegalStateException.class,
            () -> contactService.assignContactToFormation("CONTACT001", "FORM001"));
        verify(formationDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void assignContactToFormationShouldFailIfDifferentComposante() {
        Composante autreComposante = new Composante();
        autreComposante.setCodeComposante("DROIT");
        autreComposante.setName("Droit");

        contact.setComposante(composante);

        when(contactDAO.findByCodeContact("CONTACT001"))
            .thenReturn(Optional.of(contact));

        Formation formation = new Formation();
        formation.setCodeFormation("FORM001");
        formation.setIntitule("Licence Droit");
        formation.setStatus(BROUILLON);
        formation.setComposante(autreComposante);

        when(formationDAO.findByCodeFormation("FORM001"))
            .thenReturn(Optional.of(formation));

        assertThrows(SecurityException.class,
            () -> contactService.assignContactToFormation("CONTACT001", "FORM001"));

        verify(formationDAO, never()).save(any());
    }

}
