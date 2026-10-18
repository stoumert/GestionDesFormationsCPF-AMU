package amu.ter.cpf.service;

import amu.ter.cpf.dao.ActionDAO;
import amu.ter.cpf.dao.FormationDAO;
import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.model.entity.Session;
import amu.ter.cpf.validation.PublicationValidation;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ActionServiceTest {

    @Mock
    private ActionDAO actionDAO;

    @Mock
    private FormationDAO formationDAO;

    @Mock
    private Validator validator;

    @Mock
    private SessionService sessionService;

    @InjectMocks
    private ActionService actionService;

    @Test
    void shouldGetAllActions() {

        List<Action> actions = List.of(
                new Action(),
                new Action()
        );

        when(actionDAO.findAll()).thenReturn(actions);

        List<Action> result = actionService.getAll();

        assertEquals(2, result.size());

        verify(actionDAO).findAll();
    }

    @Test
    void shouldGetActionByCode() {

        Action action = new Action();
        action.setCodeAction("ACT001");

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(action));

        Action result = actionService.getByCode("ACT001");

        assertNotNull(result);
        assertEquals("ACT001", result.getCodeAction());
    }

    @Test
    void shouldReturnNullWhenActionNotFound() {

        when(actionDAO.findByCodeAction("UNKNOWN"))
                .thenReturn(Optional.empty());

        Action result = actionService.getByCode("UNKNOWN");

        assertNull(result);
    }

    @Test
    void shouldCreateAndAssignFormation() {

        Formation formation = new Formation();
        formation.setCodeFormation("FORM001");

        when(formationDAO.findByCodeFormation("FORM001"))
                .thenReturn(Optional.of(formation));

        Action result =
                actionService.createAndAssignFormation("FORM001");

        assertNotNull(result);
        assertEquals(formation, result.getFormation());
        assertEquals("FORM001-", result.getCodeAction());
    }

    @Test
    void shouldThrowExceptionWhenFormationNotFound() {

        when(formationDAO.findByCodeFormation("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> actionService.createAndAssignFormation("UNKNOWN")
        );
    }

    @Test
    void shouldSaveNewActionIdentity() {

        Formation formation = new Formation();
        formation.setCodeFormation("FORM001");

        Action action = new Action();
        action.setCodeAction("ACT001");
        action.setFormation(formation);

        when(actionDAO.existsById("ACT001"))
                .thenReturn(false);

        actionService.saveActionIdentity(action);

        verify(actionDAO).save(action);

        assertNotNull(action.getDateCreation());
        assertNotNull(action.getDateMaj());
    }

    @Test
    void shouldUpdateExistingActionIdentity() {

        Action existing = new Action();
        existing.setCodeAction("ACT001");

        Action submitted = new Action();
        submitted.setCodeAction("ACT001");
        submitted.setLangueFormation("FR");

        when(actionDAO.existsById("ACT001"))
                .thenReturn(true);

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        actionService.saveActionIdentity(submitted);

        verify(actionDAO).save(existing);

        assertEquals("FR", existing.getLangueFormation());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingUnknownAction() {

        Action submitted = new Action();
        submitted.setCodeAction("UNKNOWN");

        when(actionDAO.existsById("UNKNOWN"))
                .thenReturn(true);

        when(actionDAO.findByCodeAction("UNKNOWN"))
                .thenReturn(Optional.empty());

        assertThrows(
                IllegalArgumentException.class,
                () -> actionService.saveActionIdentity(submitted)
        );
    }

    @Test
    void shouldSaveEducationalContent() {

        Action existing = new Action();
        existing.setCodeAction("ACT001");

        Action submitted = new Action();
        submitted.setConditionSpecifiques("Condition");

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        actionService.saveEducationalContent(
                "ACT001",
                submitted
        );

        verify(actionDAO).save(existing);

        assertEquals(
                "Condition",
                existing.getConditionSpecifiques()
        );
    }

    @Test
    void shouldSaveTimesAndDuration() {

        Action existing = new Action();
        existing.setCodeAction("ACT001");

        Action submitted = new Action();
        submitted.setDureeApprentissage(500);

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        actionService.saveTimesAndDuration(
                "ACT001",
                submitted
        );

        verify(actionDAO).save(existing);

        assertEquals(
                500,
                existing.getDureeApprentissage()
        );
    }

    @Test
    void shouldSaveCosts() {

        Action existing = new Action();

        Action submitted = new Action();
        submitted.setFraisTtc(BigDecimal.valueOf(2500.0));

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        actionService.saveCosts(
                "ACT001",
                submitted
        );

        verify(actionDAO).save(existing);

        assertEquals(
                new BigDecimal("2500.0"),
                existing.getFraisTtc()
        );
    }

    @Test
    void shouldSaveLogistics() {

        Action existing = new Action();

        Action submitted = new Action();
        submitted.setAccesHandicapes("Oui");

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        actionService.saveLogistics(
                "ACT001",
                submitted
        );

        verify(actionDAO).save(existing);

        assertEquals(
                "Oui",
                existing.getAccesHandicapes()
        );
    }

    @Test
    void shouldPublishWithoutViolations() {

        Action existing = new Action();
        existing.setCodeAction("ACT001");

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        when(validator.validate(
                eq(existing),
                eq(PublicationValidation.class)
        )).thenReturn(Collections.emptySet());

        Action submitted = new Action();

        assertDoesNotThrow(
                () -> actionService.publish(
                        "ACT001",
                        submitted
                )
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    void shouldThrowConstraintViolationExceptionDuringPublish() {

        Action existing = new Action();

        ConstraintViolation<Action> violation =
                mock(ConstraintViolation.class);

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(existing));

        when(validator.validate(
                eq(existing),
                eq(PublicationValidation.class)
        )).thenReturn(Set.of(violation));

        assertThrows(
                ConstraintViolationException.class,
                () -> actionService.publish(
                        "ACT001",
                        new Action()
                )
        );
    }

    @Test
    void shouldDeleteActionAndSessions() {

        Session session1 = new Session();
        session1.setCodeSession("S1");

        Session session2 = new Session();
        session2.setCodeSession("S2");

        Action action = new Action();
        action.setCodeAction("ACT001");
        action.setSessions(List.of(session1, session2));

        when(actionDAO.findByCodeAction("ACT001"))
                .thenReturn(Optional.of(action));

        actionService.delete("ACT001");

        verify(sessionService)
                .deleteSession("S1");

        verify(sessionService)
                .deleteSession("S2");

        verify(actionDAO)
                .deleteById("ACT001");
    }

    @Test
    void shouldCopyActionFields() {

        Action source = new Action();

        source.setLangueFormation("FR");
        source.setConditionSpecifiques("Condition");
        source.setDureeApprentissage(120);
        source.setFraisTtc(BigDecimal.valueOf(1500.0));
        source.setAccesHandicapes("Oui");

        Action target = new Action();

        actionService.copyActionFields(
                target,
                source
        );

        assertEquals(
                source.getLangueFormation(),
                target.getLangueFormation()
        );

        assertEquals(
                source.getConditionSpecifiques(),
                target.getConditionSpecifiques()
        );

        assertEquals(
                source.getDureeApprentissage(),
                target.getDureeApprentissage()
        );

        assertEquals(
                source.getFraisTtc(),
                target.getFraisTtc()
        );

        assertEquals(
                source.getAccesHandicapes(),
                target.getAccesHandicapes()
        );
    }

    @Test
    void shouldCreateDraftVersion() {

        Formation draftFormation = new Formation();

        Action original = new Action();
        original.setCodeAction("ACT001");
        original.setSessions(List.of());

        when(actionDAO.save(any(Action.class)))
                .thenAnswer(i -> i.getArgument(0));

        actionService.createDraftVersion(
                original,
                draftFormation
        );

        verify(actionDAO).save(any(Action.class));
        verify(actionDAO).flush();
    }

    @Test
    void shouldCreateDraftVersionForEachSession() {

        Formation formation = new Formation();

        Action original = new Action();
        original.setCodeAction("ACT001");

        Session session1 = new Session();
        session1.setCodeSession("S1");

        Session session2 = new Session();
        session2.setCodeSession("S2");

        original.setSessions(List.of(session1, session2));

        when(actionDAO.save(any(Action.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));


        actionService.createDraftVersion(original, formation);


        verify(sessionService, times(1))
                .createDraftVersion(eq(session1), any(Action.class));

        verify(sessionService, times(1))
                .createDraftVersion(eq(session2), any(Action.class));

        verify(actionDAO).save(any(Action.class));
        verify(actionDAO).flush();
    }

    @Test
    void shouldPassSameDraftToAllSessions() {

        Formation formation = new Formation();

        Action original = new Action();
        original.setCodeAction("ACT001");

        Session session1 = new Session();
        session1.setCodeSession("S1");

        Session session2 = new Session();
        session2.setCodeSession("S2");

        original.setSessions(List.of(session1, session2));

        Action savedDraft = new Action();
        savedDraft.setCodeAction("DRAFT");

        when(actionDAO.save(any(Action.class)))
                .thenReturn(savedDraft);

        actionService.createDraftVersion(original, formation);

        verify(sessionService).createDraftVersion(session1, savedDraft);
        verify(sessionService).createDraftVersion(session2, savedDraft);
    }
}