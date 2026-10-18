<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:choose>
    <c:when test="${isEdit}">
        <c:url var="urlSaveSession" value="/session/${session.codeSession}/edit">
            <c:param name="codeAction" value="${action.codeAction}"/>
        </c:url>
    </c:when>
    <c:otherwise>
        <c:url var="urlSaveSession" value="/session/create">
            <c:param name="codeAction" value="${action.codeAction}"/>
        </c:url>
    </c:otherwise>
</c:choose>

<c:url var="urlBackSessions" value="/session/action/${action.codeAction}"/>


    <div class="mb-3">
        <h4>
            <c:choose>
                <c:when test="${not empty session.codeSession}">Modifier la session</c:when>
                <c:otherwise>Nouvelle session</c:otherwise>
            </c:choose>
        </h4>
        <p class="text-muted mb-0">
            Session pour l'action :
            <strong>${action.codeAction}</strong>
        </p>
    </div>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<form:form method="post" action="${urlSaveSession}" modelAttribute="session">

        <div class="mb-3">
            <label class="form-label">Code session</label>
            <c:choose>
                <c:when test="${isEdit}">
                    <p><c:out value="${session.codeSession}"/></p>
                </c:when>
                <c:otherwise>
                    <form:input path="codeSession" cssClass="form-control"/>
                    <form:errors path="codeSession" cssClass="text-danger"/>
                    <form:errors path="codeSessionStartWithCodeAction" cssClass="text-danger"/>
                </c:otherwise>
            </c:choose>
        </div>


    <div class="mb-3">
        <label class="form-label">Date début</label>
        <form:input path="dateDebut" type="date" cssClass="form-control"/>
        <small class="text-muted">
            Laissez les deux dates vides pour une session permanente.
        </small>
        <form:errors path="dateDebut" cssClass="text-danger d-block"/>
        <form:errors path="periodeValide" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Date fin</label>
        <form:input path="dateFin" type="date" cssClass="form-control"/>
        <small class="text-muted">
            Si vous renseignez une date de début, vous devez aussi renseigner une date de fin.
        </small>
        <form:errors path="dateFin" cssClass="text-danger d-block"/>
        <form:errors path="periodeValide" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label class="form-label">État du recrutement <span class="text-danger">*</span></label>
        <form:select path="etatRecrutement" cssClass="form-select">
            <form:option value="" label="-- Sélectionner --"/>
            <c:forEach var="etat" items="${recruitmentStatuses}">
                <form:option value="${etat.code}">
                    ${etat.code} - ${etat.description}
                </form:option>
            </c:forEach>
        </form:select>
        <form:errors path="etatRecrutement" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Garantie <span class="text-danger">*</span></label>
        <form:select path="garantie" cssClass="form-select">
            <form:option value="" label="-- Sélectionner --"/>
            <c:forEach var="garantie" items="${guarantees}">
                <form:option value="${garantie.code}">
                    ${garantie.code} - ${garantie.description}
                </form:option>
            </c:forEach>
        </form:select>
        <form:errors path="garantie" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Modalités particulières (3000 car. max)</label>
        <form:textarea path="modalitesParticulieres"
                       cssClass="form-control"
                       rows="5"/>
        <form:errors path="modalitesParticulieres" cssClass="text-danger d-block"/>
    </div>

    <div class="mt-3">
        <button type="submit" class="btn btn-primary">
            Enregistrer
        </button>

        <a href="${urlBackSessions}" class="btn btn-secondary ms-2">
            Annuler
        </a>
    </div>

</form:form>

<%@ include file="footer.jsp" %>