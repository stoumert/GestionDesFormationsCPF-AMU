<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:url var="urlCreateSession" value="/session/create">
    <c:param name="codeAction" value="${action.codeAction}"/>
</c:url>

<c:url var="urlBackActions" value="/formation/${action.formation.codeFormation}/actions"/>


    <c:if test="${not empty success}">
        <div class="alert alert-success">${success}</div>
    </c:if>
    <c:if test="${not empty error}">
        <div class="alert alert-danger">${error}</div>
    </c:if>

    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h4>Sessions de l'action</h4>
            <p class="text-muted mb-0">
                Action : <strong>${action.codeAction}</strong>
                —
                Formation : <strong>${action.formation.codeFormation}</strong>
            </p>
        </div>

        <c:if test="${action.formation.status == 'BROUILLON'}">
            <a href="${urlCreateSession}" class="btn btn-primary">
                Nouvelle session
            </a>
        </c:if>
    </div>

    <hr>

    <c:choose>
        <c:when test="${empty sessions}">
            <div class="alert alert-info">
                Aucune session pour cette action.
                <a href="${urlCreateSession}">
                    Créer la première session.
                </a>
            </div>
        </c:when>

        <c:otherwise>
            <table class="table table-bordered table-hover align-middle">
                <thead class="table-light">
                <tr>
                    <th>Code action</th>
                    <th>Code session</th>
                    <th>Date début</th>
                    <th>Date fin</th>
                    <th>État recrutement</th>
                    <th>Garantie</th>
                    <th>Date MAJ</th>
                    <th>Actions</th>

                </tr>
                </thead>

                <tbody>
                <c:forEach var="session" items="${sessions}">
                    <tr>
                        <td><strong>${action.codeAction}</strong></td>

                        <td><strong>${session.codeSession}</strong></td>

                        <td>${not empty session.dateDebut ? session.dateDebut : '—'}</td>

                        <td>${not empty session.dateFin ? session.dateFin : '—'}</td>

                        <td>
                            <c:choose>
                                <c:when test="${session.etatRecrutement == 1}">
                                    Ouvert
                                </c:when>
                                <c:when test="${session.etatRecrutement == 2}">
                                    Fermé
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <c:choose>
                                <c:when test="${session.garantie == 1}">
                                    Oui
                                </c:when>
                                <c:when test="${session.garantie == 0}">
                                    Non
                                </c:when>
                                <c:otherwise>—</c:otherwise>
                            </c:choose>
                        </td>


                        <td>${session.dateMaj}</td>
                        <c:choose>

                            <c:when test="${session.action.formation.status == 'BROUILLON'}">
                                <td>
                                    <c:url var="urlEditSession" value="/session/${session.codeSession}/edit">
                                        <c:param name="codeAction" value="${action.codeAction}"/>
                                    </c:url>
                                    <a href="${urlEditSession}" class="btn btn-sm btn-outline-primary">Modifier</a>
                                    <form:form method="post" action="/session/${session.codeSession}/delete" >
                                        <button class="btn btn-sm btn-outline-danger">Supprimer</button>
                                    </form:form>
                                </td>
                            </c:when>
                            <c:otherwise>
                                <td>Aucune action n'est disponible</td>
                            </c:otherwise>
                        </c:choose>





                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>

    <div class="mt-3">
        <a href="${urlBackActions}" class="btn btn-outline-secondary">
            ← Retour aux actions
        </a>
    </div>

<%@ include file="footer.jsp" %>