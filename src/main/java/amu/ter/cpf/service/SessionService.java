package amu.ter.cpf.service;
import amu.ter.cpf.dao.ActionDAO;
import amu.ter.cpf.dao.SessionDAO;
import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class SessionService {
  private final SessionDAO sessionDAO;

  private final ActionDAO actionDAO;

  public Action getAction(String codeAction) {
    Optional<Action> action = actionDAO.findByCodeAction(codeAction);

    if (action.isEmpty()) {
      throw new IllegalArgumentException("Action introuvable");
    }

    return action.orElse(null);
  }

  public List<Session> getSessionsByAction(String codeAction) {
    Action action = getAction(codeAction);
    return sessionDAO.findByAction(action);
  }

  public Session getByCode(String codeSession) {
    return sessionDAO.findByCodeSession(codeSession)
        .orElse(null);
  }

  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public Session prepareNewSession(String codeAction) {
    Action action = getAction(codeAction);
    Session session = new Session();
    session.setAction(action);
    session.setCodeSession(codeAction+"-");

    return session;
  }
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
  public void createSession(String codeAction, Session session) {
    Action action = getAction(codeAction);

    session.setAction(action);
    session.setDateCreation(LocalDate.now());
    session.setDateMaj(LocalDate.now());

    sessionDAO.save(session);
  }
  public void createDraftVersion(Session original, Action draftAction) {
    Session draft = new Session();
    draft.setCodeSession(original.getCodeSession()+"_*");
    draft.setSessionParente(original);

    draft.setAction(draftAction);
    draft.setDateCreation(LocalDate.now());
    draft.setDateMaj(LocalDate.now());
    copieSessionFields(draft, original);
    sessionDAO.save(draft);
    //sessionDAO.flush();
  }

  public void copieSessionFields(Session target, Session source) {
    target.setDateDebut(source.getDateDebut());
    target.setDateFin(source.getDateFin());
    target.setEtatRecrutement(source.getEtatRecrutement());
    target.setGarantie(source.getGarantie());
    target.setModalitesParticulieres(source.getModalitesParticulieres());
    target.setOuverture(source.getOuverture());
  }
  @Transactional
  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  public void saveSession(String codeSession, Session submitted) {
    Session existing = sessionDAO.findByCodeSession(codeSession)
            .orElseThrow(() -> new IllegalArgumentException("Session introuvable."));
    copieSessionFields(existing, submitted);
    existing.setDateMaj(LocalDate.now());
    sessionDAO.save(existing);
  }
  public void deleteSession(String codeSession) {
    sessionDAO.deleteById(codeSession);
  }
}



