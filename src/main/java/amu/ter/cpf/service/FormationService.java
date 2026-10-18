package amu.ter.cpf.service;

import amu.ter.cpf.dao.ActionDAO;
import amu.ter.cpf.dao.FormationDAO;
import amu.ter.cpf.dao.SessionDAO;
import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.validation.PublicationValidation;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

import static amu.ter.cpf.model.entity.Status.*;

@Service
@RequiredArgsConstructor
public class FormationService {

    private final FormationDAO formationDAO;
    private final UserDAO userDAO;
    private final Validator validator;
    private final ActionService actionService;
    private final SessionDAO sessionDAO;
    private final ActionDAO actionDAO;
    private final SessionService sessionService;

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Formation createFormationIdentity(Formation formation) {
        checkComposanteAccess(formation.getComposante().getCodeComposante());

        formation.setStatus(BROUILLON);
        formation.setDateCreation(LocalDate.now());
        formation.setDateMaj(LocalDate.now());
        return formationDAO.save(formation);
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void publishFormation(String codeFormation) {
        Formation formation = formationDAO.findByCodeFormationWithActions(codeFormation);
        if (formation == null) {
            throw new IllegalArgumentException("Formation introuvable : " + codeFormation);
        }
        checkComposanteAccess(formation.getComposante().getCodeComposante());
        if (formation.getStatus() != BROUILLON) {
            throw new IllegalStateException("Seul un brouillon peut être publié");
        }
        validateForPublication(formation);

        List<Action> actions = formation.getActions();
        if (actions == null || actions.isEmpty()) {
            throw new IllegalStateException(
                    "La formation doit avoir au moins une action"
            );
        }

        for (Action action : actions) {
            validateForPublication(action);


            List<Session> sessions = action.getSessions();
            if (sessions == null || sessions.isEmpty()) {
                throw new IllegalStateException(
                        "L'action " + action.getCodeAction()
                                + " doit avoir au moins une session"
                );
            }
            for (Session session : sessions) {
                validateForPublication(session);
            }
        }
        if (formation.getFormationParente() == null) {
            activateFormation(formation);
            return;
        }
        replaceAndActivate(formation);
    }
    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void alterFormation(String codeFormation, Formation updatedFormation) {
        Formation formation = formationDAO.findByCodeFormationWithActions(codeFormation);

        if (formation == null) {
            throw new IllegalArgumentException("Formation introuvable : " + codeFormation);
        }

        if (formation.getStatus() == ACTIF) {
            formation = createDraftVersion(codeFormation);
        }

        applyUpdates(updatedFormation, formation);
        formationDAO.save(formation);
    }
    @Transactional
    public void replaceAndActivate(Formation draft) {

        draft = formationDAO.findByCodeFormationWithActions(draft.getCodeFormation());

        Formation ancienne = formationDAO.findByCodeFormationWithActions(
                draft.getFormationParente().getCodeFormation()
        );

        String originalCode = ancienne.getCodeFormation();
        LocalDate dateCreation = ancienne.getDateCreation();

        List<Action> actionsDraft = new ArrayList<>(draft.getActions());
        List<Action> actionsAncienne = new ArrayList<>(ancienne.getActions());

        List<List<Session>> sessionDraft = new ArrayList<>();
        List<List<Session>> sessionAncienne = new ArrayList<>();

        Map<String, Action> actionDraftAncienne = new HashMap<>();
        Map<String, Session> sessionDraftAncienne = new HashMap<>();

        for(Action action : actionsDraft) {
            Action ancienneAction = action.getActionParente();
            if (ancienneAction != null){
                Action action1 = new Action();
                action1.setCodeAction(ancienneAction.getCodeAction());
                action1.setDateCreation(ancienneAction.getDateCreation());
                action1.setSessions(ancienneAction.getSessions());
                actionDraftAncienne.put(action.getCodeAction(),action1);
            }
            sessionDraft.add(new ArrayList<>(action.getSessions()));
            for(Session session : action.getSessions()) {
                Session ancienneSession = session.getSessionParente();
                if (ancienneSession != null){
                    Session session1 = new Session();
                    session1.setCodeSession(ancienneSession.getCodeSession());
                    session1.setDateCreation(ancienneSession.getDateCreation());
                    sessionDraftAncienne.put(session.getCodeSession(),session1);
                    session.setSessionParente(null);
                }
                sessionDAO.save(session);
            }
            action.setActionParente(null);
            actionDAO.save(action);
        }
        for(Action action : actionsAncienne) {
            sessionAncienne.add(new ArrayList<>(action.getSessions()));
            sessionDAO.deleteAll(action.getSessions());
        }
        actionDAO.deleteAll(actionsAncienne);
        draft.setFormationParente(null);
        formationDAO.save(draft);

        formationDAO.delete(ancienne);
        formationDAO.flush();
        Formation newFormation = buildNouvelleVersion(draft, originalCode, dateCreation);
        formationDAO.save(newFormation);
        formationDAO.flush();
        for (Action action : actionsDraft) {
            Action newAction = new Action();
            Action ancienneAction = actionDraftAncienne.get(action.getCodeAction());

            if (ancienneAction != null) {
                newAction.setCodeAction(ancienneAction.getCodeAction());
                newAction.setDateCreation(ancienneAction.getDateCreation());
            } else {
                String codeNettoye = action.getCodeAction()
                        .replace(draft.getCodeFormation(), originalCode);
                newAction.setCodeAction(codeNettoye);
                newAction.setDateCreation(action.getDateCreation());
            }

            newAction.setDateMaj(LocalDate.now());
            newAction.setFormation(newFormation);
            actionService.copyActionFields(newAction, action);
            actionDAO.save(newAction);
            actionDAO.flush();

            for (Session session : action.getSessions()) {
                Session newSession = new Session();
                Session ancienneSession = sessionDraftAncienne.get(session.getCodeSession());

                String codeFinal;
                if (ancienneSession != null) {
                    codeFinal = ancienneSession.getCodeSession();
                    newSession.setDateCreation(ancienneSession.getDateCreation());
                } else {
                    codeFinal = session.getCodeSession().replace("_*", "");
                    newSession.setDateCreation(session.getDateCreation());
                }
                newSession.setCodeSession(codeFinal);
                newSession.setDateMaj(LocalDate.now());
                sessionService.copieSessionFields(newSession, session);
                newSession.setSessionParente(null);
                newSession.setAction(newAction);

                sessionDAO.save(newSession);
                if (!codeFinal.equals(session.getCodeSession())) {
                    sessionDAO.delete(session);
                }
            }

            actionDAO.delete(action);
        }
        formationDAO.delete(draft);
    }
    public Formation getFormationByCode(String codeFormation) {
        return formationDAO.findByCodeFormation(codeFormation).orElse(null);
    }

    public Formation getFormationByCodeWithActions(String codeFormation) {
        return formationDAO.findByCodeFormationWithActions(codeFormation);
    }

    public Formation getFormationByIntitule(String intitule) {
        return formationDAO.findByIntitule(intitule).orElse(null);
    }

    public List<Formation> getAll() {
        return formationDAO.findAll();
    }

    public List<Formation> getAllActif() {
        return formationDAO.findByStatus(ACTIF);
    }

    public List<Formation> getAllBrouillon() {
        return formationDAO.findByStatus(BROUILLON);
    }

    @Transactional
    public Formation createDraftVersion(String codeFormation) {
        Formation original = formationDAO.findByCodeFormationWithActions(codeFormation);

        Formation brouillon = new Formation();
        brouillon.setCodeFormation(codeFormation + "_*");
        copyFields(original, brouillon);
        brouillon.setFormationParente(original);
        brouillon.setStatus(BROUILLON);
        brouillon.setDateCreation(LocalDate.now());
        brouillon.setDateMaj(LocalDate.now());
        brouillon.setContact(original.getContact());

        brouillon = formationDAO.save(brouillon);
        formationDAO.flush();

        for (Action action : original.getActions()) {
            actionService.createDraftVersion(action, brouillon);
        }

        return brouillon;
    }

    private Formation buildNouvelleVersion(
            Formation draft,
            String originalCode,
            LocalDate dateCreation
    ) {
        Formation nouvelleVersion = new Formation();
        nouvelleVersion.setCodeFormation(originalCode);
        copyFields(draft, nouvelleVersion);
        nouvelleVersion.setStatus(ACTIF);
        nouvelleVersion.setDateCreation(dateCreation);
        nouvelleVersion.setDateMaj(LocalDate.now());
        nouvelleVersion.setContact(draft.getContact());
        return nouvelleVersion;
    }


    private void copyFields(Formation source, Formation target) {
        target.setTypeCertification(source.getTypeCertification());
        target.setCodeCertification(source.getCodeCertification());
        copyAllFields(source, target);
        target.setComposante(source.getComposante());
    }

    private void applyUpdates(Formation source, Formation target ) {
        copyAllFields(source, target);
    }

    private void activateFormation(Formation formation) {
        formation.setStatus(ACTIF);
        formation.setDateMaj(LocalDate.now());
        formationDAO.save(formation);
    }

    private <T> void validateForPublication(T object) {
        Set<ConstraintViolation<T>> violations =
                validator.validate(object, PublicationValidation.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private void checkComposanteAccess(String codeComposante) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDAO.findByEmail(Objects.requireNonNull(authentication).getName())
                .orElseThrow(() ->
                        new IllegalStateException("Utilisateur introuvable")
                );

        if (currentUser.getRole() == Role.ADMIN) {
            return;
        }
        if (currentUser.getComposante() == null
                || !currentUser.getComposante()
                .getCodeComposante()
                .equals(codeComposante)) {
            throw new AccessDeniedException(
                    "Vous n'avez pas accès à cette composante"
            );
        }
    }

    public void createFormationWithAllOtherFields(Formation formation, String codeFormationSource) {
        Formation current = getFormationByCode(codeFormationSource);
        copyAllFields(formation, current);
        formationDAO.save(current);
    }

    private void copyAllFields(Formation source, Formation target) {
        target.setIntitule(source.getIntitule());
        target.setObjectifFormation(source.getObjectifFormation());
        target.setResultatsAttendus(source.getResultatsAttendus());
        target.setContenuFormation(source.getContenuFormation());
        target.setParcoursDeFormation(source.getParcoursDeFormation());
        target.setObjectifGeneral(source.getObjectifGeneral());
        target.setResumeContenu(source.getResumeContenu());
        target.setBlocsCompetences(source.getBlocsCompetences());
        target.setCodeBloc(source.getCodeBloc());
        target.setBlocsModifiables(source.getBlocsModifiables());
        target.setCategorisationOffre(source.getCategorisationOffre());
        target.setDateMaj(LocalDate.now());
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void deleteFormation(String codeFormation) {
        Formation formation = getFormationByCode(codeFormation);
        checkComposanteAccess(formation.getComposante().getCodeComposante());

        for(Action action : formation.getActions()) {
            actionService.delete(action.getCodeAction());
        }
        formationDAO.delete(formation);
    }

    public List<Formation> search(String intitule, String code, String codeComposante,
                                  String typeCertification, String codeCertification) {
        String i    = (intitule != null && !intitule.isBlank()) ? intitule : null;
        String c    = (code != null && !code.isBlank()) ? code : null;
        String comp = (codeComposante != null && !codeComposante.isBlank()) ? codeComposante : null;
        String type = (typeCertification != null && !typeCertification.isBlank()) ? typeCertification : null;
        String cert = (codeCertification != null && !codeCertification.isBlank()) ? codeCertification : null;

        List<Formation> results = formationDAO.search(i, c, comp, type, cert);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userDAO.findByEmail(Objects.requireNonNull(authentication).getName()).orElseThrow();

        return results.stream()
                .filter(f -> canSee(f, currentUser))
                .toList();
    }

    private boolean canSee(Formation f, User user) {
        if (user.getRole() == Role.ADMIN) {
            return true;
        }
        if (f.getStatus() == Status.ACTIF) {
            return true;
        }
        if (user.getRole() == Role.MANAGER
                && user.getComposante() != null
                && f.getStatus() == Status.BROUILLON) {
            return f.getComposante().getCodeComposante()
                    .equals(user.getComposante().getCodeComposante());
        }
        return false;
    }

    public boolean existsFormationByFormationParente(Formation formation) {
        return formationDAO.findByFormationParente(formation).isPresent();
    }

}