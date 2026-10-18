package amu.ter.cpf.service;

import amu.ter.cpf.dao.ActionDAO;
import amu.ter.cpf.dao.SessionDAO;
import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Session;
import amu.ter.cpf.model.entity.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class SessionServiceTest {

    @MockitoBean
    private SessionDAO sessionDAO;

    @MockitoBean
    private ActionService actionService;

    @Autowired
    private SessionService sessionService;

    private Action action;
    private Session session;
    @MockitoBean
    private ActionDAO actionDAO;

    @BeforeEach
    void setUp() {
        action = new Action();
        action.setCodeAction("ACT001");
        //action.setStatus(Status.ACTIF);

        session = new Session();
        session.setCodeSession("SES001");
        session.setEtatRecrutement(1);
        session.setGarantie(0);
        session.setAction(action);
        session.setDateCreation(LocalDate.now());
        session.setDateMaj(LocalDate.now());
    }

    @Test
    void getActionShouldReturnAction() {
        when(actionDAO.findByCodeAction("ACT001"))
            .thenReturn(Optional.of(action));

        Action result = sessionService.getAction("ACT001");

        assertNotNull(result);
        assertEquals("ACT001", result.getCodeAction());
    }



    @Test
    void getActionShouldThrowIfNotFound() {
        when(actionService.getByCode("UNKNOWN")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> sessionService.getAction("UNKNOWN"));
    }

    @Test
    void getByCodeShouldReturnSession() {
        when(sessionDAO.findByCodeSession("SES001")).thenReturn(Optional.of(session));

        Session result = sessionService.getByCode("SES001");

        assertNotNull(result);
        assertEquals("SES001", result.getCodeSession());
    }

    @Test
    void getByCodeShouldReturnNullIfNotFound() {
        when(sessionDAO.findByCodeSession("UNKNOWN")).thenReturn(Optional.empty());

        assertNull(sessionService.getByCode("UNKNOWN"));
    }


    @Test
    void getSessionsByActionShouldReturnList() {
        when(actionDAO.findByCodeAction("ACT001")).thenReturn(Optional.of(action));
        when(sessionDAO.findByAction(action)).thenReturn(List.of(session));

        List<Session> result = sessionService.getSessionsByAction("ACT001");

        assertEquals(1, result.size());
        verify(sessionDAO).findByAction(action);
    }


    @Test
    void getSessionsByActionShouldThrowIfActionNotFound() {
        when(actionService.getByCode("UNKNOWN")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> sessionService.getSessionsByAction("UNKNOWN"));

        verify(sessionDAO, never()).findByAction(any());
    }


    @Test
   @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
   void prepareNewSessionShouldReturnSessionWithAction() {
        when(actionDAO.findByCodeAction("ACT001")).thenReturn(Optional.of(action));
        Session result = sessionService.prepareNewSession("ACT001");

        assertNotNull(result);
        assertEquals(action, result.getAction());
        assertEquals("ACT001-", result.getCodeSession());
        verify(actionDAO).findByCodeAction("ACT001");
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void prepareNewSessionShouldThrowIfActionNotFound() {
        when(actionService.getByCode("UNKNOWN")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> sessionService.prepareNewSession("UNKNOWN"));
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void prepareNewSessionShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> sessionService.prepareNewSession("ACT001"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createSessionShouldSetStatusAndDates() {
        when(actionDAO.findByCodeAction("ACT001")).thenReturn(Optional.of(action));
        when(sessionDAO.save(any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        session.setDateCreation(null);
        session.setDateMaj(null);

        sessionService.createSession("ACT001", session);

        assertNotNull(session.getDateCreation());
        assertNotNull(session.getDateMaj());
        assertEquals(action, session.getAction());
        verify(sessionDAO).save(session);
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void createSessionShouldThrowIfActionNotFound() {
        when(actionService.getByCode("UNKNOWN")).thenReturn(null);

        assertThrows(IllegalArgumentException.class,
                () -> sessionService.createSession("UNKNOWN", session));

        verify(sessionDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void createSessionShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> sessionService.createSession("ACT001", session));

        verify(sessionDAO, never()).save(any());
    }



    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void saveSessionShouldUpdateAllFields() {
        when(sessionDAO.findByCodeSession("SES001")).thenReturn(Optional.of(session));
        when(sessionDAO.save(any(Session.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Session submitted = new Session();
        submitted.setDateDebut(LocalDate.of(2026, 9, 1));
        submitted.setDateFin(LocalDate.of(2026, 9, 30));
        submitted.setEtatRecrutement(2);
        submitted.setGarantie(1);
        submitted.setModalitesParticulieres("Nouvelles modalités");
        submitted.setOuverture(false);

        sessionService.saveSession("SES001", submitted);

        assertEquals(LocalDate.of(2026, 9, 1), session.getDateDebut());
        assertEquals(LocalDate.of(2026, 9, 30), session.getDateFin());
        assertEquals(2, session.getEtatRecrutement());
        assertEquals(1, session.getGarantie());
        assertEquals("Nouvelles modalités", session.getModalitesParticulieres());
        assertFalse(session.getOuverture());
        assertNotNull(session.getDateMaj());
        verify(sessionDAO).save(session);
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void saveSessionShouldThrowIfNotFound() {
        when(sessionDAO.findByCodeSession("UNKNOWN")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> sessionService.saveSession("UNKNOWN", new Session()));

        verify(sessionDAO, never()).save(any());
    }

    @Test
    @WithMockUser(username = "user@test.com", authorities = "ROLE_USER")
    void saveSessionShouldFailForUser() {
        assertThrows(AccessDeniedException.class,
                () -> sessionService.saveSession("SES001", session));

        verify(sessionDAO, never()).save(any());
    }

    @Test
    void createDraftVersionShouldCreateDraftWithCorrectValues() {
        Session original = new Session();
        original.setCodeSession("SES001");
        original.setDateDebut(LocalDate.of(2026, 9, 1));
        original.setDateFin(LocalDate.of(2026, 9, 30));
        original.setEtatRecrutement(2);
        original.setGarantie(1);
        original.setModalitesParticulieres("Modalités test");
        original.setOuverture(true);

        Action draftAction = new Action();
        draftAction.setCodeAction("ACT001_DRAFT");

        sessionService.createDraftVersion(original, draftAction);

        verify(sessionDAO).save(argThat(draft ->
            draft.getCodeSession().equals("SES001_*")
                && draft.getSessionParente().equals(original)
                && draft.getAction().equals(draftAction)
                && draft.getDateCreation() != null
                && draft.getDateMaj() != null
                && draft.getDateDebut().equals(LocalDate.of(2026, 9, 1))
                && draft.getDateFin().equals(LocalDate.of(2026, 9, 30))
                && draft.getEtatRecrutement() == 2
                && draft.getGarantie() == 1
                && draft.getModalitesParticulieres().equals("Modalités test")
                && draft.getOuverture().equals(true)
        ));
    }


    @Test
    void deleteSessionShouldDeleteByCodeSession() {
        sessionService.deleteSession("SES001");
        verify(sessionDAO).deleteById("SES001");
    }





}
