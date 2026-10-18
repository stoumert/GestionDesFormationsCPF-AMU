<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:url value="/action/step2?codeAction=${action.codeAction}" var="backToStep2"/>


    <div class="mb-4">
        <p class="text-muted mb-1">Étape 3 / 5</p>
        <div class="progress">
            <div class="progress-bar" style="width: 60%">Horaires & Durée</div>
        </div>
    </div>

    <h4>Horaires, durée & recrutement</h4>
    <p class="text-muted">Action : <strong>${action.codeAction}</strong></p>
    <hr>

    <form:form modelAttribute="action" method="post" action="/action/step3">
        <form:hidden path="codeAction"/>
        <form:hidden path="formation.codeFormation"/>

        <div class="mb-3">
            <label class="form-label">Type d'horaires</label>
            <form:select path="codeTypeHoraires" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="h" items="${typesHoraires}">
                    <form:option value="${h.code}">${h.description}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="codeTypeHoraires" cssClass="text-danger small" element="div"/>
        </div>

        <div class="row">
            <div class="col-md-4 mb-3">
                <label class="form-label">Heures en centre</label>
                <form:input path="nombreHeuresCentre" cssClass="form-control" type="number" min="0" max="99999"/>
                <form:errors path="nombreHeuresCentre" cssClass="text-danger small" element="div"/>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Heures en entreprise</label>
                <form:input path="nombreHeuresEntreprise" cssClass="form-control" type="number" min="0" max="99999"/>
                <form:errors path="nombreHeuresEntreprise" cssClass="text-danger small" element="div"/>
            </div>
            <div class="col-md-4 mb-3">
                <label class="form-label">Durée d'apprentissage (h)</label>
                <form:input path="dureeApprentissage" cssClass="form-control" type="number" min="0" max="99999"/>
                <form:errors path="dureeApprentissage" cssClass="text-danger small" element="div"/>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Groupe Formation Emploi</label>
            <form:select path="codeGfe" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="gfe" items="${groupesFormationEmploi}">
                    <form:option value="${gfe.code}">${gfe.description}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="codeGfe" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Périmètre de recrutement</label>
            <form:select path="codePerimetreRecrutement" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="p" items="${perimetresRecrutement}">
                    <form:option value="${p.code}">${p.value}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="codePerimetreRecrutement" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Informations périmètre de recrutement</label>
            <form:input path="infosPerimetreRecrutement" cssClass="form-control" maxlength="50"/>
            <form:errors path="infosPerimetreRecrutement" cssClass="text-danger small" element="div"/>
        </div>

        <hr>
        <div class="d-flex justify-content-between">
            <a href="${backToStep2}"
               class="btn btn-outline-secondary">← Précédent</a>
            <button type="submit" name="submitType" value="save" class="btn btn-secondary">Sauvegarder et quitter</button>
            <button type="submit" name="submitType" value="next" class="btn btn-primary">Suivant →</button>
        </div>
    </form:form>

<%@ include file="footer.jsp" %>
