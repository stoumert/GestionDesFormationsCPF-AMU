<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<h2>
    <c:choose>
        <c:when test="${isEdit}">Modifier un contact</c:when>
        <c:otherwise>Créer un nouveau contact</c:otherwise>
    </c:choose>
</h2>

<hr>

<form:form modelAttribute="contact" method="post" action="${formAction}">

    <div id="erreurs-globaux">
        <form:errors path="*" cssClass="alert alert-danger d-block" element="div"/>
    </div>

    <h5 class="mt-4 mb-3 text-secondary">Identification</h5>

    <div class="mb-3">
        <label class="form-label fw-semibold">
            Identifiant du contact <span class="text-danger">*</span>
        </label>
        <c:choose>
            <c:when test="${isEdit}">
                <p class="form-control-plaintext"><strong><c:out value="${contact.codeContact}"/></strong></p>
                <form:hidden path="codeContact"/>
            </c:when>
            <c:otherwise>
                <form:input path="codeContact" cssClass="form-control"
                            placeholder="Ex : ALLSH-BUREAU-FC-AIX-CR"/>
                <div class="form-text text-muted">
                    Identifiant unique.
                </div>
                <form:errors path="codeContact" cssClass="text-danger small" element="div"/>
            </c:otherwise>
        </c:choose>
    </div>

    <h5 class="mt-4 mb-3 text-secondary">Identité</h5>

    <div class="row">
        <div class="col-md-6 mb-3">
            <label class="form-label fw-semibold">
                Nom <span class="text-danger">*</span>
            </label>
            <form:input path="nom" cssClass="form-control"
                        placeholder="Ex : Bureau de la formation continue / LIASSINE"/>
            <form:errors path="nom" cssClass="text-danger small" element="div"/>
        </div>
        <div class="col-md-6 mb-3">
            <label class="form-label fw-semibold">
                Prénom <span class="text-danger">*</span>
            </label>
            <form:input path="prenom" cssClass="form-control"
                        placeholder="Ex : Aix en Provence / Yanis"/>
            <form:errors path="prenom" cssClass="text-danger small" element="div"/>
        </div>
    </div>

    <div class="mb-3">
        <label class="form-label fw-semibold">Fonction</label>
        <form:input path="fonction" cssClass="form-control"
                    placeholder="Ex : Responsable pédagogique"/>
        <form:errors path="fonction" cssClass="text-danger small" element="div"/>
    </div>

    <h5 class="mt-4 mb-3 text-secondary">Coordonnées</h5>

    <div class="mb-3">
        <label class="form-label fw-semibold">
            Téléphone <span class="text-danger">*</span>
        </label>
        <form:input path="telephone" cssClass="form-control"
                    placeholder="Ex : 0413949799"/>
        <div class="form-text text-muted">
            Chiffres uniquement, + autorisé en première position.
        </div>
        <form:errors path="telephone" cssClass="text-danger small" element="div"/>
    </div>

    <div class="mb-3">
        <label class="form-label fw-semibold">
            Courriel <span class="text-danger">*</span>
        </label>
        <form:input path="courriel" cssClass="form-control"
                    placeholder="Ex : allsh-fc@univ-amu.fr"/>
        <div class="form-text text-muted">
            Adresse email valide au format XXX@YYY.ZZ.
        </div>
        <form:errors path="courriel" cssClass="text-danger small" element="div"/>
    </div>

    <h5 class="mt-4 mb-3 text-secondary">
        Adresse
        <span class="badge bg-warning text-dark ms-2">
            Optionnelle
        </span>
    </h5>


    <div class="row">
        <div class="col-md-3 mb-3">
            <label class="form-label fw-semibold">
                Numéro de voie
            </label>
            <form:input path="numeroVoie" cssClass="form-control" placeholder="Ex : 29"/>
            <form:errors path="numeroVoie" cssClass="text-danger small" element="div"/>
        </div>
        <div class="col-md-3 mb-3">
            <label class="form-label fw-semibold">Indice de répétition</label>
            <form:select path="codeIndiceRepetition" cssClass="form-select">
                <form:option value="" label="Aucun "/>
                 <c:forEach var="indice" items="${indicesRepetition}">
                        <form:option value="${indice.code}" label="${indice.indiceRepetition}"/>
                    </c:forEach>
            </form:select>
            <form:errors path="indiceValid" cssClass="text-danger small" element="div"/>
        </div>
        <div class="col-md-3 mb-3">
            <label class="form-label fw-semibold">
                Nature de voie
            </label>
            <form:select path="codeNatureVoie" cssClass="form-select">
                <form:option value="" label="Sélectionner"/>
                <c:forEach var="nature" items="${naturesVoie}">
                        <form:option value="${nature}" label="${nature.natureVoie}"/>
                    </c:forEach>
            </form:select>
            <form:errors path="codeNatureVoie" cssClass="text-danger small" element="div"/>
        </div>
        <div class="col-md-3 mb-3">
            <label class="form-label fw-semibold">
                Libellé de voie
            </label>
            <form:input path="libelleVoie" cssClass="form-control"
                        placeholder="Ex : Robert Schuman"/>
            <form:errors path="libelleVoie" cssClass="text-danger small" element="div"/>
        </div>

    </div>

    <div class="row">
        <div class="col-md-4 mb-3">
            <label class="form-label fw-semibold">
                Code postal <span class="text-danger">*</span>
            </label>
            <form:input path="codePostal" cssClass="form-control" placeholder="Ex : 13621"/>
            <div class="form-text text-muted">5 chiffres.</div>
            <form:errors path="codePostal" cssClass="text-danger small" element="div"/>
        </div>
        <div class="col-md-8 mb-3">
            <label class="form-label fw-semibold">
                Ville <span class="text-danger">*</span>
            </label>
            <form:input path="ville" cssClass="form-control"
                        placeholder="Ex : AIX EN PROVENCE"/>
            <form:errors path="ville" cssClass="text-danger small" element="div"/>
        </div>
    </div>
    <div class="mb-3">
        <label class="form-label fw-semibold">
            Ligne 1 <span class="text-danger">*</span>
        </label>
        <form:input path="adresseLigne1" cssClass="form-control"
                    placeholder="Ex : BAT A"/>
        <form:errors path="adresseLigne1" cssClass="text-danger small" element="div"/>
    </div>

    <div class="mb-3">
        <label class="form-label fw-semibold">Ligne 2</label>
        <form:input path="adresseLigne2" cssClass="form-control"
                    placeholder="Ex : Bureau 23)"/>
        <form:errors path="adresseLigne2" cssClass="text-danger small" element="div"/>
    </div>



    <h5 class="mt-4 mb-3 text-secondary">Conformité réglementaire</h5>

    <div class="mb-3">
        <label class="form-label fw-semibold">Conformité réglementaire <span class="text-danger">*</span></label>
        <form:select path="conformiteReglementaire" cssClass="form-select">
            <form:option value=""  label="Sélectionner"/>
            <form:option value="0" label="Non"/>
            <form:option value="1" label="Oui"/>
        </form:select>
        <form:errors path="conformiteReglementaire" cssClass="text-danger small" element="div"/>
    </div>

    <hr class="mt-4">

    <div class="d-flex gap-2">
        <button type="submit" class="btn btn-primary">Enregistrer</button>
        <a href="<c:url value='/contact'/>" class="btn btn-secondary">Annuler</a>
    </div>

</form:form>

<%@ include file="footer.jsp" %>