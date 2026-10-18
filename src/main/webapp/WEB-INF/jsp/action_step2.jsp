<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>
<c:url value="/action/step1?codeAction=${action.codeAction}" var="backToStep1"/>


    <div class="mb-4">
        <p class="text-muted mb-1">Étape 2 / 5</p>
        <div class="progress">
            <div class="progress-bar" style="width: 40%">Contenu pédagogique</div>
        </div>
    </div>

    <h4>Contenu pédagogique</h4>
    <p class="text-muted">Action : <strong>${action.codeAction}</strong></p>
    <hr>

    <form:form modelAttribute="action" method="post" action="/action/step2">
        <form:hidden path="codeAction"/>
        <form:hidden path="formation.codeFormation"/>

        <div class="mb-3">
            <label class="form-label">Conditions spécifiques</label>
            <form:textarea path="conditionSpecifiques" cssClass="form-control" rows="4" maxlength="3000"/>
            <form:errors path="conditionSpecifiques" cssClass="text-danger small" element="div"/>
            <small class="text-muted">Laisser vide s'il n'y a pas de conditions ni prérequis. Max 3000 caractères.</small>
        </div>

        <div class="mb-3">
            <label class="form-label">Prérequis à l'inscription <span class="text-danger">*</span></label>
            <form:select path="existencePrerequis" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <form:option value="1">Oui</form:option>
                <form:option value="0">Non</form:option>
            </form:select>
            <form:errors path="existencePrerequis" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Information sur l'admission</label>
            <form:input path="infoAdmission" cssClass="form-control" maxlength="250"/>
            <form:errors path="infoAdmission" cssClass="text-danger small" element="div"/>
            <small class="text-muted">Max 250 caractères.</small>
        </div>

        <div class="mb-3">
            <label class="form-label">Modalités pédagogiques</label>
            <form:textarea path="modalitesPedagogiques" cssClass="form-control" rows="3" maxlength="200"/>
            <form:errors path="modalitesPedagogiques" cssClass="text-danger small" element="div"/>
            <small class="text-muted">Max 200 caractères.</small>
        </div>

        <div class="mb-3">
            <label class="form-label">Modalités handicap</label>
            <form:textarea path="modalitesHandicap" cssClass="form-control" rows="3" maxlength="3000"/>
            <form:errors path="modalitesHandicap" cssClass="text-danger small" element="div"/>
            <small class="text-muted">Modalités adaptées aux personnes en situation de handicap. Max 3000 caractères.</small>
        </div>

        <div class="mb-3">
            <label class="form-label">Modalités de recrutement</label>
            <form:textarea path="modalitesRecrutement" cssClass="form-control" rows="3" maxlength="3000"/>
            <form:errors path="modalitesRecrutement" cssClass="text-danger small" element="div"/>
            <small class="text-muted">Max 3000 caractères.</small>
        </div>

        <hr>
        <div class="d-flex justify-content-between">
            <a href="${backToStep1}"
               class="btn btn-outline-secondary">← Précédent</a>
            <button type="submit" name="submitType" value="save" class="btn btn-secondary">Sauvegarder et quitter</button>
            <button type="submit" name="submitType" value="next" class="btn btn-primary">Suivant →</button>
        </div>
    </form:form>

<%@ include file="footer.jsp" %>
