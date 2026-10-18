package amu.ter.cpf.service;

import amu.ter.cpf.dao.FormationDAO;
import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.validation.PublicationValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import jakarta.validation.Validator;

@SpringBootTest
public class FormationServiceTest {
    @MockitoBean
    private FormationDAO formationDAO;

  @MockitoBean
  private Validator validator;
    @MockitoBean
    private UserDAO userDAO;

    @Autowired
    private FormationService formationService;

    @MockitoBean
    private ActionService actionService;
    private Formation formation;
    private User manager;

    private String codeFormation;

    @BeforeEach
    void setUp() {
        codeFormation="FORM001";
        Composante composante = new Composante();
        composante.setCodeComposante("SCN");

        formation = new Formation();
        formation.setCodeFormation("FORM001");
        formation.setIntitule("Master Informatique");
        formation.setResumeContenu("Formation informatique");
        formation.setStatus(Status.BROUILLON);
        formation.setComposante(composante);
        formation.setDateCreation(LocalDate.now());
        formation.setDateMaj(LocalDate.now());

        manager = new User();
        manager.setEmail("manager@test.com");
        manager.setRole(Role.MANAGER);
        manager.setComposante(composante);

    }


   @Test
    void getFormationByCodeShouldReturnFormation() {
        when(formationDAO.findByCodeFormation("FORM001"))
                .thenReturn(Optional.of(formation));

        Formation result = formationService.getFormationByCode("FORM001");

        assertNotNull(result);
        assertEquals("FORM001", result.getCodeFormation());
    }

    @Test
    void getFormationByCodeShouldReturnNullIfNotFound() {
        when(formationDAO.findByCodeFormation("FORM404"))
                .thenReturn(Optional.empty());

        Formation result = formationService.getFormationByCode("FORM404");

        assertNull(result);
    }

    @Test
    void getFormationByIntituleShouldReturnFormation() {
        when(formationDAO.findByIntitule("Master Informatique"))
                .thenReturn(Optional.of(formation));

        Formation result =
                formationService.getFormationByIntitule("Master Informatique");

        assertNotNull(result);
        assertEquals("Master Informatique", result.getIntitule());
    }

    @Test
    void getAllShouldReturnList() {
        when(formationDAO.findAll()).thenReturn(List.of(formation));

        List<Formation> formations = formationService.getAll();

        assertEquals(1, formations.size());
    }

    @Test
    void getAllActifShouldReturnOnlyActif() {
      formation.setStatus(Status.ACTIF);
        when(formationDAO.findByStatus(Status.ACTIF))
                .thenReturn(List.of(formation));

        List<Formation> formations = formationService.getAllActif();

        assertEquals(1, formations.size());
        assertEquals(Status.ACTIF, formations.get(0).getStatus());
    }

    @Test
    void getAllBrouillonShouldReturnOnlyBrouillon() {
        formation.setStatus(Status.BROUILLON);
        when(formationDAO.findByStatus(Status.BROUILLON))
                .thenReturn(List.of(formation));

        List<Formation> formations = formationService.getAllBrouillon();

        assertEquals(1, formations.size());
        assertEquals(Status.BROUILLON, formations.get(0).getStatus());
    }

  @Test
  void getFormationByCodeWithActionsShouldReturnFormation() {
    when(formationDAO.findByCodeFormationWithActions("FORM001"))
        .thenReturn(formation);

    Formation result = formationService.getFormationByCodeWithActions("FORM001");

    assertNotNull(result);
    assertEquals("FORM001", result.getCodeFormation());
  }


    @Test
    @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
    void createShouldWorkForAdmin() {
        User admin = new User();
        admin.setEmail("admin@test.com");
        admin.setRole(Role.ADMIN);

        when(userDAO.findByEmail("admin@test.com"))
                .thenReturn(Optional.of(admin));
        when(formationDAO.save(any(Formation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Formation result = formationService.createFormationIdentity(formation);

        assertNotNull(result);
        assertEquals(Status.BROUILLON, result.getStatus());
        assertNotNull(result.getDateCreation());
        assertNotNull(result.getDateMaj());
        verify(formationDAO).save(any(Formation.class));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createShouldWorkForManagerInHisComposante() {
        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));
        when(formationDAO.save(any(Formation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Formation result = formationService.createFormationIdentity(formation);

        assertNotNull(result);
        assertEquals(Status.BROUILLON, result.getStatus());
        assertNotNull(result.getDateCreation());
        assertNotNull(result.getDateMaj());
        verify(formationDAO).save(any(Formation.class));
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createShouldFailIfFormationNotInManagerComposante() {
        Composante autre = new Composante();
        autre.setCodeComposante("DROIT");
        formation.setComposante(autre);

        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));

        assertThrows(AccessDeniedException.class,
                () -> formationService.createFormationIdentity(formation));

        verify(formationDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void createShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> formationService.createFormationIdentity(formation));

        verify(formationDAO, never()).save(any());
    }


  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void createShouldFailIfManagerHasNoComposante() {
    manager.setComposante(null);

    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));

    assertThrows(AccessDeniedException.class,
        () -> formationService.createFormationIdentity(formation));

    verify(formationDAO, never()).save(any());
  }

  @Test
  @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
  void publishFormationShouldFailForUser() {
    assertThrows(AccessDeniedException.class,
        () -> formationService.publishFormation(codeFormation));

    verify(formationDAO, never()).findByCodeFormationWithActions(any());
    verify(formationDAO, never()).save(any());
  }

  @Test
  @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
  void publishShouldWorkForAdmin() {
    User admin = new User();
    admin.setEmail("admin@test.com");
    admin.setRole(Role.ADMIN);

    Action action = new Action();
    action.setCodeAction("A1");

    Session session = new Session();
    session.setCodeSession("S1");

    action.setSessions(List.of(session));

    formation.setActions(List.of(action));
    formation.setFormationParente(null);
    formation.setStatus(Status.BROUILLON);


    when(userDAO.findByEmail("admin@test.com"))
        .thenReturn(Optional.of(admin));
    when(formationDAO.findByCodeFormationWithActions(codeFormation))
        .thenReturn(formation);
    when(validator.validate(any(), eq(PublicationValidation.class)))
        .thenReturn(Collections.emptySet());
    when(formationDAO.save(any(Formation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));


    formationService.publishFormation(codeFormation);

    assertEquals(Status.ACTIF, formation.getStatus());
    verify(formationDAO).findByCodeFormationWithActions(codeFormation);
    verify(formationDAO).save(any(Formation.class));
  }

  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void publishShouldWorkForManagerInHisComposante() {

    Action action = new Action();
    action.setCodeAction("A1");

    Session session = new Session();
    session.setCodeSession("S1");

    action.setSessions(List.of(session));

    formation.setActions(List.of(action));
    formation.setFormationParente(null);
    formation.setStatus(Status.BROUILLON);


    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));
    when(formationDAO.findByCodeFormationWithActions(codeFormation))
        .thenReturn(formation);
    when(validator.validate(any(), eq(PublicationValidation.class)))
        .thenReturn(Collections.emptySet());
    when(formationDAO.save(any(Formation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));


    formationService.publishFormation(codeFormation);

    assertEquals(Status.ACTIF, formation.getStatus());
    verify(formationDAO).findByCodeFormationWithActions(codeFormation);
    verify(formationDAO).save(any(Formation.class));
  }
  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void publishShouldWorkForManagerInOtherComposante() {

    Composante autreComposante = new Composante();
    autreComposante.setCodeComposante("DROIT");
    formation.setComposante(autreComposante);

    Action action = new Action();
    action.setCodeAction("A1");

    Session session = new Session();
    session.setCodeSession("S1");

    action.setSessions(List.of(session));

    formation.setActions(List.of(action));
    formation.setFormationParente(null);
    formation.setStatus(Status.BROUILLON);


    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));
    when(formationDAO.findByCodeFormationWithActions(codeFormation))
        .thenReturn(formation);
    assertThrows(AccessDeniedException.class,
        () -> formationService.publishFormation(codeFormation));

    verify(formationDAO).findByCodeFormationWithActions(codeFormation);

    verify(formationDAO, never()).save(any());
  }

  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void publishShouldNotWorkWithoutSession() {

    Action action = new Action();
    action.setCodeAction("A1");
    action.setSessions(List.of());

    formation.setActions(List.of(action));
    formation.setFormationParente(null);


    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));
    when(formationDAO.findByCodeFormationWithActions(codeFormation))
        .thenReturn(formation);
    when(validator.validate(any(), eq(PublicationValidation.class)))
        .thenReturn(Collections.emptySet());

    assertThrows(IllegalStateException.class,
        () -> formationService.publishFormation(codeFormation));
    verify(formationDAO).findByCodeFormationWithActions(codeFormation);
    verify(formationDAO, never()).save(any(Formation.class));
  }

  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void publishShouldNotWorkWithoutAction() {
    formation.setFormationParente(null);

    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));
    when(formationDAO.findByCodeFormationWithActions(codeFormation))
        .thenReturn(formation);
    when(validator.validate(any(), eq(PublicationValidation.class)))
        .thenReturn(Collections.emptySet());

    assertThrows(IllegalStateException.class,
        () -> formationService.publishFormation(codeFormation));
    verify(formationDAO).findByCodeFormationWithActions(codeFormation);
    verify(formationDAO, never()).save(any(Formation.class));
  }

  @Test
  @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
  void publishFormationShouldFailIfFormationNotFound() {
    when(formationDAO.findByCodeFormationWithActions("FORM404"))
        .thenReturn(null);

    assertThrows(IllegalArgumentException.class,
        () -> formationService.publishFormation("FORM404"));

    verify(formationDAO, never()).save(any());
  }

  @Test
  @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
  void publishFormationShouldFailIfStatusIsNotBrouillon() {
    formation.setStatus(Status.ACTIF);

    when(formationDAO.findByCodeFormationWithActions(codeFormation))
        .thenReturn(formation);

    assertThrows(IllegalStateException.class,
        () -> formationService.publishFormation(codeFormation));

    verify(formationDAO, never()).save(any());
  }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void alterFormationActiveShouldCreateDraftAndApplyChanges() {
      formation.setStatus(Status.ACTIF);
      formation.setActions(List.of());

      when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));
        when(formationDAO.findByCodeFormationWithActions("FORM001"))
                .thenReturn(formation);

        when(formationDAO.save(any(Formation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Formation updatedFormation = new Formation();
        updatedFormation.setIntitule("Master IDL");
        updatedFormation.setResumeContenu("Master IDL");

        formationService.alterFormation("FORM001", updatedFormation);


}

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void alterFormationBrouillonShouldApplyChangesDirectly() {
        formation.setStatus(Status.BROUILLON);

        when(userDAO.findByEmail("manager@test.com"))
                .thenReturn(Optional.of(manager));
        when(formationDAO.findByCodeFormationWithActions("FORM001"))
                .thenReturn(formation);
        when(formationDAO.save(any(Formation.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Formation updatedFormation = new Formation();
        updatedFormation.setIntitule("Master IDL");
        updatedFormation.setResumeContenu("Master IDL");

        formationService.alterFormation("FORM001", updatedFormation);

        assertEquals("Master IDL", formation.getIntitule());
        assertEquals("Master IDL", formation.getResumeContenu());
        assertEquals(Status.BROUILLON, formation.getStatus());
        verify(formationDAO, times(1)).save(any(Formation.class));
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void alterFormationNotFoundShouldThrow() {
      when(formationDAO.findByCodeFormationWithActions("FORM404"))
          .thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> formationService.alterFormation("FORM404", new Formation()));

        verify(formationDAO, never()).save(any());
    }



  @Test
  void replaceAndActivateShouldCreateNewActiveFormation() {
    Formation ancienne = new Formation();
    ancienne.setCodeFormation("FORM001");
    ancienne.setStatus(Status.ACTIF);
    ancienne.setDateCreation(LocalDate.of(2026, 5, 10));
    ancienne.setActions(List.of());

    Formation draft = new Formation();
    draft.setCodeFormation("FORM001_*");
    draft.setStatus(Status.BROUILLON);
    draft.setFormationParente(ancienne);
    draft.setDateCreation(LocalDate.now());
    draft.setDateMaj(LocalDate.now());
    draft.setActions(List.of());

    when(formationDAO.findByCodeFormationWithActions("FORM001_*"))
        .thenReturn(draft);

    when(formationDAO.findByCodeFormationWithActions("FORM001"))
        .thenReturn(ancienne);

    formationService.replaceAndActivate(draft);

    verify(formationDAO).delete(ancienne);
    verify(formationDAO).delete(draft);
    verify(formationDAO, atLeastOnce()).save(any(Formation.class));
  }


  @Test
  void createDraftVersionShouldCreateDraftFromOriginalFormation() {
    formation.setStatus(Status.ACTIF);
    formation.setActions(List.of());

    when(formationDAO.findByCodeFormationWithActions("FORM001"))
        .thenReturn(formation);

    when(formationDAO.save(any(Formation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Formation draft = formationService.createDraftVersion("FORM001");
    assertNotNull(draft);
    assertEquals("FORM001_*", draft.getCodeFormation());
    assertEquals(Status.BROUILLON, draft.getStatus());
    assertEquals(formation, draft.getFormationParente());
    assertEquals("Master Informatique", draft.getIntitule());
    assertEquals("Formation informatique", draft.getResumeContenu());
    assertNotNull(draft.getDateCreation());
    assertNotNull(draft.getDateMaj());
    verify(formationDAO).save(any(Formation.class));
    verify(formationDAO).flush();
  }

  @Test
  void createDraftVersionShouldCopyContactFromOriginal() {
    Contact contact = new Contact();
    contact.setCodeContact("C001");
    formation.setCodeFormation("FORM001");
    formation.setStatus(Status.ACTIF);
    formation.setContact(contact);
    formation.setActions(List.of());

    when(formationDAO.findByCodeFormationWithActions("FORM001"))
        .thenReturn(formation);

    when(formationDAO.save(any(Formation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Formation draft = formationService.createDraftVersion("FORM001");

    assertEquals(contact, draft.getContact());
  }

  @Test
  void createDraftVersionShouldCreateDraftActionsForEachOriginalAction() {
    Action action1 = new Action();
    action1.setCodeAction("A1");

    Action action2 = new Action();
    action2.setCodeAction("A2");

    formation.setStatus(Status.ACTIF);
    formation.setActions(List.of(action1, action2));

    when(formationDAO.findByCodeFormationWithActions("FORM001"))
        .thenReturn(formation);

    when(formationDAO.save(any(Formation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Formation draft = formationService.createDraftVersion("FORM001");
    verify(actionService).createDraftVersion(action1, draft);
    verify(actionService).createDraftVersion(action2, draft);
  }

  @Test
  void createDraftVersionShouldWorkWhenOriginalHasNoActions() {
    formation.setStatus(Status.ACTIF);
    formation.setActions(List.of());

    when(formationDAO.findByCodeFormationWithActions("FORM001"))
        .thenReturn(formation);

    when(formationDAO.save(any(Formation.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));

    Formation draft = formationService.createDraftVersion("FORM001");

    assertNotNull(draft);
    assertEquals("FORM001_*", draft.getCodeFormation());
    assertEquals(Status.BROUILLON, draft.getStatus());

    verify(actionService, never()).createDraftVersion(any(Action.class), any(Formation.class));
  }


  @Test
  @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
  void deleteFormationShouldWorkForAdmin() {
    User admin = new User();
    admin.setEmail("admin@test.com");
    admin.setRole(Role.ADMIN);


    Action action = new Action();
    action.setCodeAction("A1");
    formation.setActions(List.of(action));

    when(userDAO.findByEmail("admin@test.com"))
        .thenReturn(Optional.of(admin));

    when(formationDAO.findByCodeFormation("FORM001"))
        .thenReturn(Optional.of(formation));

    formationService.deleteFormation("FORM001");

    verify(actionService).delete("A1");
    verify(formationDAO).delete(formation);
  }

  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void deleteFormationShouldWorkForManagerInHisComposante() {
    formation.setComposante(manager.getComposante());

    Action action = new Action();
    action.setCodeAction("A1");
    formation.setActions(List.of(action));

    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));

    when(formationDAO.findByCodeFormation("FORM001"))
        .thenReturn(Optional.of(formation));

    formationService.deleteFormation("FORM001");

    verify(actionService).delete("A1");
    verify(formationDAO).delete(formation);
  }

  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void deleteFormationShouldFailForManagerInOtherComposante() {
    Composante autre = new Composante();
    autre.setCodeComposante("DROIT");

    formation.setComposante(autre);
    formation.setActions(List.of());

    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));

    when(formationDAO.findByCodeFormation("FORM001"))
        .thenReturn(Optional.of(formation));

    assertThrows(AccessDeniedException.class,
        () -> formationService.deleteFormation("FORM001"));

    verify(actionService, never()).delete(any());
    verify(formationDAO, never()).delete(any());
  }

  @Test
  @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
  void deleteFormationShouldFailForUser() {
    assertThrows(AccessDeniedException.class,
        () -> formationService.deleteFormation("FORM001"));

    verify(formationDAO, never()).findByCodeFormation(any());
    verify(actionService, never()).delete(any());
    verify(formationDAO, never()).delete(any());
  }


  @Test
  @WithMockUser(username = "admin@test.com", authorities = "ROLE_ADMIN")
  void searchShouldReturnAllResultsForAdmin() {
    User admin = new User();
    admin.setEmail("admin@test.com");
    admin.setRole(Role.ADMIN);

    Composante autre = new Composante();
    autre.setCodeComposante("DROIT");
    Formation autreFormation = new Formation();
    autreFormation.setCodeFormation("FORM002");
    autreFormation.setComposante(autre);

    when(userDAO.findByEmail("admin@test.com"))
        .thenReturn(Optional.of(admin));
    when(formationDAO.search(eq("Master"), isNull(), isNull(), isNull(), isNull()))
        .thenReturn(List.of(formation, autreFormation));

    List<Formation> results = formationService.search("Master", null, null, null, null);

    assertEquals(2, results.size());
    verify(formationDAO).search("Master", null, null, null, null);
  }

  @Test
  @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
  void searchShouldFilterResultsByManagerComposante() {
    Composante autre = new Composante();
    autre.setCodeComposante("DROIT");
    Formation autreFormation = new Formation();
    autreFormation.setCodeFormation("FORM002");
    autreFormation.setComposante(autre);

    when(userDAO.findByEmail("manager@test.com"))
        .thenReturn(Optional.of(manager));
    when(formationDAO.search(isNull(), isNull(), isNull(), isNull(), isNull()))
        .thenReturn(List.of(formation, autreFormation));

    List<Formation> results = formationService.search(null, null, null, null, null);

    assertEquals(1, results.size());
    assertEquals("FORM001", results.get(0).getCodeFormation());
  }

}