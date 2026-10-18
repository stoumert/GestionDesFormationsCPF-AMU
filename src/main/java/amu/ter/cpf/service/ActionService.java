package amu.ter.cpf.service;

import amu.ter.cpf.dao.ActionDAO;
import amu.ter.cpf.dao.FormationDAO;
import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Formation;
import amu.ter.cpf.model.entity.Session;
import amu.ter.cpf.validation.PublicationValidation;
import jakarta.transaction.Transactional;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ActionService {

    private final ActionDAO actionDAO;
    private final FormationDAO formationDAO;
    private final Validator validator;
    private final SessionService sessionService;

    public List<Action> getAll() {
        return actionDAO.findAll();
    }

    public Action getByCode(String actionCode) {
        return actionDAO.findByCodeAction(actionCode).orElse(null);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public Action createAndAssignFormation(String formationCode) {
        Formation formation = formationDAO.findByCodeFormation(formationCode).orElse(null);
        if (formation == null) throw new IllegalArgumentException("Formation not found: " + formationCode);
        Action action = new Action();
        action.setFormation(formation);
        action.setCodeAction(formationCode+"-");
        return action;
    }
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public void createDraftVersion(Action original, Formation brouillon) {

        Action draft = new Action();

        draft.setCodeAction(original.getCodeAction()+"_*");
        draft.setActionParente(original);
        draft.setFormation(brouillon);
        draft.setDateCreation(LocalDate.now());
        draft.setDateMaj(LocalDate.now());
        copyActionFields(draft, original);

        draft = actionDAO.save(draft);
        actionDAO.flush();

        List<Session> sessions = original.getSessions();
        for(Session session : sessions) {
            sessionService.createDraftVersion(session, draft);
        }
    }

    public void copyActionFields(Action target, Action source) {
        applyIdentityFields(target, source);
        applyEducationalContentFields(target, source);
        applyTimesAndDurationFields(target, source);
        applyCostFields(target, source);
        applyLogisticsFields(target, source);
        target.setCodeModaliteAdmissions(source.getCodeModaliteAdmissions());
        target.setCodeRythmeFormations(source.getCodeRythmeFormations());
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void saveActionIdentity(Action submitted) {
        if (!actionDAO.existsById(submitted.getCodeAction())) {
            initializeNewAction(submitted);
            actionDAO.save(submitted);
        } else {
            Action existing = loadOrThrow(submitted.getCodeAction());
            applyIdentityFields(existing, submitted);
            actionDAO.save(existing);
        }
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void saveEducationalContent(String actionCode, Action submitted) {
        Action existing = loadOrThrow(actionCode);
        applyEducationalContentFields(existing, submitted);
        actionDAO.save(existing);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void saveTimesAndDuration(String actionCode, Action submitted) {
        Action existing = loadOrThrow(actionCode);
        applyTimesAndDurationFields(existing, submitted);
        actionDAO.save(existing);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void saveCosts(String actionCode, Action submitted) {
        Action existing = loadOrThrow(actionCode);
        applyCostFields(existing, submitted);
        actionDAO.save(existing);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public void saveLogistics(String actionCode, Action submitted) {
        Action existing = loadOrThrow(actionCode);
        applyLogisticsFields(existing, submitted);
        actionDAO.save(existing);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @Transactional
    public void publish(String actionCode, Action submitted) {
        saveLogistics(actionCode, submitted);
        Action existing = loadOrThrow(actionCode);
        validateForPublication(existing);

    }

    private void applyIdentityFields(Action target, Action source) {
        target.setModalitesEnseignement(source.getModalitesEnseignement());
        target.setModalitesEntreesSorties(source.getModalitesEntreesSorties());
        target.setNiveauEntreeObligatoire(source.getNiveauEntreeObligatoire());
        target.setLangueFormation(source.getLangueFormation());
        target.setUrlAction(source.getUrlAction());
        target.setDateMaj(LocalDate.now());
        target.setCodeRythmeFormations(source.getCodeRythmeFormations());
        target.setCodeModaliteAdmissions(source.getCodeModaliteAdmissions());
    }

    private void applyEducationalContentFields(Action target, Action source) {
        target.setConditionSpecifiques(source.getConditionSpecifiques());
        target.setExistencePrerequis(source.getExistencePrerequis());
        target.setInfoAdmission(source.getInfoAdmission());
        target.setModalitesPedagogiques(source.getModalitesPedagogiques());
        target.setModalitesHandicap(source.getModalitesHandicap());
        target.setModalitesRecrutement(source.getModalitesRecrutement());
        target.setDateMaj(LocalDate.now());
    }

    private void applyTimesAndDurationFields(Action target, Action source) {
        target.setCodeTypeHoraires(source.getCodeTypeHoraires());
        target.setNombreHeuresCentre(source.getNombreHeuresCentre());
        target.setNombreHeuresEntreprise(source.getNombreHeuresEntreprise());
        target.setDureeApprentissage(source.getDureeApprentissage());
        target.setCodeGfe(source.getCodeGfe());
        target.setCodePerimetreRecrutement(source.getCodePerimetreRecrutement());
        target.setInfosPerimetreRecrutement(source.getInfosPerimetreRecrutement());
        target.setDateMaj(LocalDate.now());
    }

    private void applyCostFields(Action target, Action source) {
        target.setTauxTva(source.getTauxTva());
        target.setFraisHt(source.getFraisHt());
        target.setFraisTtc(source.getFraisTtc());
        target.setCodeModeleEconomique(source.getCodeModeleEconomique());
        target.setFraisCertifInclusFraisAnpec(source.getFraisCertifInclusFraisAnpec());
        target.setFraisAnpec(source.getFraisAnpec());
        target.setDetailFraisAnpec(source.getDetailFraisAnpec());
        target.setDateMaj(LocalDate.now());
    }

    private void applyLogisticsFields(Action target, Action source) {
        target.setAccesHandicapes(source.getAccesHandicapes());
        target.setRestauration(source.getRestauration());
        target.setHebergement(source.getHebergement());
        target.setTransport(source.getTransport());
        target.setAutresServices(source.getAutresServices());
        target.setDateMaj(LocalDate.now());
    }

    private void validateForPublication(Action action) {
        Set<ConstraintViolation<Action>> violations =
                validator.validate(action, PublicationValidation.class);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }

    private Action loadOrThrow(String actionCode) {
        return actionDAO.findByCodeAction(actionCode)
                .orElseThrow(() -> new IllegalArgumentException("Action not found: " + actionCode));
    }

    private void initializeNewAction(Action action) {
        action.setCodeAction(action.getCodeAction());
        action.setDateCreation(LocalDate.now());
        action.setDateMaj(LocalDate.now());
    }

    public void delete(String codeAction) {
        Action action = loadOrThrow(codeAction);
        for (Session session : action.getSessions()) {
            sessionService.deleteSession(session.getCodeSession());
        }
        actionDAO.deleteById(codeAction);
    }
}