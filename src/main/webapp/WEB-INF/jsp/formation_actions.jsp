<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:url var="urlFormationList" value="/formation/view/${formation.codeFormation}"/>
<c:url var="urlCreateAction" value="/action/create">
    <c:param name="codeFormation" value="${formation.codeFormation}"/>
</c:url>


    <div class="d-flex justify-content-between align-items-center mb-3">
        <div>
            <h4>Actions de la formation</h4>
            <p class="text-muted mb-0">
                <strong>${formation.codeFormation}</strong> — ${formation.intitule}
            </p>
        </div>
        <c:if test="${formation.status == 'BROUILLON'}">
            <a href="${urlCreateAction}" class="btn btn-primary">
                Nouvelle action
            </a>
        </c:if>
    </div>
    <hr>

    <c:choose>
        <c:when test="${empty formation.actions}">
            <div class="alert alert-info">
                Aucune action pour cette formation.
                <a href= ${urlCreateAction}>
                    Créer la première action.
                </a>
            </div>
        </c:when>
        <c:otherwise>
            <table class="table table-bordered table-hover align-middle">
                <thead class="table-light">
                    <tr>
                        <th>Code action</th>
                        <th>Modalité d'enseignement</th>
                        <th>Langue</th>
                        <th>Frais TTC (€)</th>
                        <th>Date MAJ</th>
                        <c:if test="${formation.status == 'BROUILLON'}">
                            <th>Actions</th>
                        </c:if>
                        <th>Sessions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="action" items="${formation.actions}">
                        <c:url value="/action/step1" var="actionStep1Url">
                            <c:param name="codeAction" value="${action.codeAction}"/>
                        </c:url>
                        <tr>
                            <td><strong>${action.codeAction}</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${action.modalitesEnseignement == 0}">Présentielle</c:when>
                                    <c:when test="${action.modalitesEnseignement == 1}">Mixte</c:when>
                                    <c:when test="${action.modalitesEnseignement == 2}">À distance</c:when>
                                    <c:otherwise>—</c:otherwise>
                                </c:choose>
                            </td>
                            <td>${not empty action.langueFormation ? action.langueFormation : '—'}</td>
                            <td>${not empty action.fraisTtc ? action.fraisTtc : '—'}</td>
                            <td>${action.dateMaj}</td>
                            <c:if test="${formation.status == 'BROUILLON'}">
                                <td>
                                    <a href="${actionStep1Url}"
                                       class="btn btn-sm btn-outline-primary">Modifier</a>
                                    <form:form method="post" action="/action/${action.codeAction}/delete" >
                                        <button class="btn btn-sm btn-outline-danger">Supprimer</button>
                                    </form:form>
                                </td>

                            </c:if>
                            <td>
                                <c:url var="urlSessions" value="/session/action/${action.codeAction}"/>
                                <a href="${urlSessions}" class="btn btn-sm btn-primary">
                                    Sessions
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:otherwise>
    </c:choose>



    <div class="mt-3">
        <a href="${urlFormationList}" class="btn btn-outline-secondary">
            ← Retour à la formation
        </a>
    </div>

<%@ include file="footer.jsp" %>
