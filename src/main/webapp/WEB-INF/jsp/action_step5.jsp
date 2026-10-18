<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:url value="/action/step4?codeAction=${action.codeAction}" var="backToStep4"/>

    <div class="mb-4">
        <p class="text-muted mb-1">Étape 5 / 5</p>
        <div class="progress">
            <div class="progress-bar bg-success" style="width: 100%">Logistique</div>
        </div>
    </div>

    <h4>Logistique</h4>
    <p class="text-muted">Action : <strong>${action.codeAction}</strong></p>
    <hr>
    <c:if test="${not empty publicationError}">
        <div class="alert alert-danger">
                ${publicationError}
            <ul class="mt-2 mb-0">
            </ul>
        </div>
    </c:if>

    <form:form modelAttribute="action" method="post" action="/action/step5">
        <form:hidden path="codeAction"/>
        <form:hidden path="formation.codeFormation"/>

        <div class="mb-3">
            <label class="form-label">Accès handicapés <span class="text-danger">*</span></label>
            <form:input path="accesHandicapes" cssClass="form-control" maxlength="250"/>
            <form:errors path="accesHandicapes" cssClass="text-danger small" element="div"/>
            <small class="text-muted">Obligatoire pour la publication. Max 250 caractères.</small>
        </div>

        <div class="row">
            <div class="col-md-4 mb-3">
                <label class="form-label">Restauration</label>
                <form:input path="restauration" cssClass="form-control" maxlength="250"/>
                <form:errors path="restauration" cssClass="text-danger small" element="div"/>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Hébergement</label>
                <form:input path="hebergement" cssClass="form-control" maxlength="250"/>
                <form:errors path="hebergement" cssClass="text-danger small" element="div"/>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Transport</label>
                <form:input path="transport" cssClass="form-control" maxlength="250"/>
                <form:errors path="transport" cssClass="text-danger small" element="div"/>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Autres services</label>
            <form:input path="autresServices" cssClass="form-control" maxlength="255"/>
            <form:errors path="autresServices" cssClass="text-danger small" element="div"/>
        </div>

        <hr>
        <div class="d-flex justify-content-between">
            <a href="${backToStep4}"
               class="btn btn-outline-secondary">← Précédent</a>
            <div class="d-flex gap-2">
                <button type="submit" name="submitType" value="save" class="btn btn-secondary">
                    Enregistrer
                </button>
                <button type="submit" name="submitType" value="validate" class="btn btn-success">
                    Valider
                </button>
            </div>
        </div>
    </form:form>

<%@ include file="footer.jsp" %>
