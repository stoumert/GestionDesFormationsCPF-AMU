<%@ include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<c:url value='/formation/view/${formation.codeFormation}' var="back"/>
<h2 class="mb-3">
    <c:choose>
        <c:when test="${isEdit}">Modifier la formation</c:when>
        <c:otherwise>Créer une formation</c:otherwise>
    </c:choose>
</h2>

<c:if test="${not empty success}">
    <div class="alert alert-success alert-dismissible fade show mt-3" role="alert">
        ${success}
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
</c:if>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<%-- Affiche toutes les erreurs de validation en haut du formulaire --%>
<form:form modelAttribute="formation" action="${formAction}" method="post">
    <div id="erreurs-globaux">
        <form:errors path="*" cssClass="alert alert-danger d-block" element="div"/>
    </div>

    <div class="mb-3">
        <label>Code formation :</label>
        <p><c:out value="${formation.codeFormation}"/></p>
    </div>

    <div class="mb-3">
        <label>Code certification</label>
        <p><c:out value="${formation.codeCertification}"/></p>
    </div>

    <div class="mb-3">
        <label>Intitulé * (255 car. max) :</label>
        <form:input class="form-control" path="intitule"/>
        <form:errors path="intitule" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label>Objectif de formation * (3000 car. max) :</label>
        <form:textarea class="form-control" path="objectifFormation" rows="5"/>
        <form:errors path="objectifFormation" cssClass="text-danger d-block"/>
    </div>



    <div class="mb-3">
        <label>Résultats attendus * (3000 car. max) :</label>
        <form:textarea class="form-control" path="resultatsAttendus" rows="5"/>
        <form:errors path="resultatsAttendus" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label>Contenu de formation * (3000 car. max) :</label>
        <form:textarea class="form-control" path="contenuFormation" rows="5"/>
        <form:errors path="contenuFormation" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label>Résumé du contenu * (255 car. max) :</label>
        <form:textarea class="form-control" path="resumeContenu" rows="3"/>
        <form:errors path="resumeContenu" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label>Type de parcours * :</label>
        <form:select class="form-select" path="parcoursDeFormation">
            <form:option value="" label="-- Sélectionner --"/>
            <form:option value="1" label="Collectif"/>
            <form:option value="2" label="Individualisé"/>
            <form:option value="3" label="Modulaire"/>
            <form:option value="4" label="Mixte"/>
        </form:select>
        <form:errors path="parcoursDeFormation" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label>Objectif général * :</label>
        <form:select class="form-select" path="objectifGeneral">
            <form:option value="" label="-- Sélectionner --"/>
            <form:option value="2" label="2 - Perfectionnement des compétences"/>
            <form:option value="4" label="4 - Création d'entreprise"/>
            <form:option value="5" label="5 - Remise à niveau"/>
            <form:option value="6" label="6 - Certification"/>
            <form:option value="7" label="7 - Professionnalisation"/>
            <form:option value="8" label="8 - Préparation à la qualification"/>
            <form:option value="9" label="9 - Remobilisation"/>
        </form:select>
        <form:errors path="objectifGeneral" cssClass="text-danger d-block"/>
    </div>


<div class="mb-3">
    <label>Catégorisation de l'offre :</label>
    <form:input class="form-control" path="categorisationOffre"/>
    <form:errors path="categorisationOffre" cssClass="text-danger d-block"/>
</div>

<div class="mb-3">
    <label>Blocs de compétences :</label>
    <form:select class="form-select" path="blocsCompetences">
        <form:option value="" label="-- Sélectionner --"/>
        <form:option value="0" label="Non"/>
        <form:option value="1" label="Oui"/>
    </form:select>
    <form:errors path="blocsCompetences" cssClass="text-danger d-block"/>
</div>

<div class="mb-3">
    <label>Code(s) bloc :</label>

    <form:input class="form-control mb-2" path="codeBloc[0]"
                placeholder="Ex : RNCP12345BC01"/>

    <form:input class="form-control mb-2" path="codeBloc[1]"
                placeholder="Ex : RNCP12345BC02"/>

    <form:input class="form-control mb-2" path="codeBloc[2]"
                placeholder="Ex : RNCP12345BC03"/>

    <form:errors path="codeBloc" cssClass="text-danger d-block"/>
</div>


    <div class="mb-3">
        <label>Blocs de compétences modifiables (uniquement pour RNCP) :</label>
        <form:select class="form-select" path="blocsModifiables">
            <form:option value="" label="-- Non applicable --"/>
            <form:option value="0" label=" Non"/>
            <form:option value="1" label=" Oui"/>
        </form:select>
        <form:errors path="blocsModifiables" cssClass="text-danger d-block"/>
    </div>

    <c:if test="${not empty composantes}">
        <div class="mb-3">
            <label>Composante * :</label>
            <select class="form-select" name="codeComposante">

                <c:forEach var="comp" items="${composantes}">
                    <option value="${comp.codeComposante}"
                        <c:if test="${formation.composante != null &&
                                      formation.composante.codeComposante == comp.codeComposante}">
                            selected
                        </c:if>
                    >
                        ${comp.name}
                    </option>
                </c:forEach>
            </select>
        </div>
    </c:if>

    <div class="mt-3">
        <button type="submit" class="btn btn-primary">Enregistrer</button>
        <a href="${back}" class="btn btn-secondary ms-2">Annuler</a>
    </div>

</form:form>

<%@include file="footer.jsp"%>