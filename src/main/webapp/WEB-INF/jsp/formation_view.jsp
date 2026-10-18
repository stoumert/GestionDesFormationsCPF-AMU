<%@ include file="header.jsp" %>
<%@page pageEncoding="UTF-8" %>


<c:url value='/formation/${formation.codeFormation}/edit' var="edit"/>
<c:url value='/formation/${formation.codeFormation}/publish' var="publish"/>
<c:url value='/formation/${formation.codeFormation}/actions' var="getAction"/>
<c:url value='/formation/createDraft/${formation.codeFormation}' var="createDraft"/>
<c:url value='/formation/${formation.codeFormation}/delete' var="delete"/>
<c:url value='/formation/${formation.codeFormation}/assign-contact' var="assign"/>
<c:url value='/contact/create' var="createContact"/>
    <!-- HEADER -->

    <div class="formation-header">
        <c:if test="${not empty success}">
            <div class="alert alert-success">${success}</div>
        </c:if>
        <c:if test="${not empty error}">
            <div class="alert alert-danger">${error}</div>
        </c:if>

        <div class="d-flex justify-content-between align-items-start flex-wrap">

            <div>

                <h1 class="mb-2">
                    ${formation.intitule}
                </h1>

                <p class="text-muted mb-3">
                    Code : ${formation.codeFormation}
                </p>

                <span class="badge bg-primary">
                    ${formation.typeCertification}
                </span>

                <span class="badge bg-success">
                    ${formation.codeCertification}
                </span>

                <span class="badge bg-secondary">
                    ${formation.status}
                </span>

            </div>

            <sec:authorize access="hasAnyRole('ADMIN','MANAGER')">

                <div class="action-buttons">

                    <c:choose>

                        <c:when test="${formation.status == 'BROUILLON'}">

                            <a href="${edit}"
                               class="btn btn-warning btn-floating">
                                Modifier
                            </a>

                            <form:form method="post"
                                  action="${publish}">

                                <button class="btn btn-success btn-floating">
                                    Publier
                                </button>

                            </form:form>

                           <form:form method="get" action="${getAction}" style="display:inline;">
                               <input type="hidden" name="codeFormation" value="${formation.codeFormation}"/>
                               <button type="submit" class="btn btn-primary btn-floating">
                                   Action
                               </button>
                           </form:form>

                            <form:form method="post"
                                  action="${delete}">
                                <button class="btn btn-danger btn-floating">
                                    Supprimer
                                </button>

                            </form:form>

                        </c:when>
                        <c:when test="${currentUser.composante.codeComposante == formation.composante.codeComposante  || currentUser.role == 'ADMIN'}">
                            <form:form methode="post" action="${createDraft}">
                                <button type="submit" class="btn btn-warning btn-floating">Créer un brouillon</button>
                            </form:form>
                        </c:when>

                    </c:choose>

                </div>

            </sec:authorize>

        </div>

    </div>

    <!-- INFORMATIONS -->

    <div class="section-card">

        <h3 class="section-title">
            Informations générales
        </h3>

        <div class="row">

            <div class="col-md-6">
                <p>
                    <span class="info-label">
                        Code Formation :
                    </span>

                    ${formation.codeFormation}
                </p>
            </div>

            <div class="col-md-6">
                <p>
                    <span class="info-label">
                        Certification :
                    </span>

                    ${formation.codeCertification}
                </p>
            </div>

            <div class="col-md-6">
                <p>
                    <span class="info-label">
                        Type Certification :
                    </span>

                    ${formation.typeCertification}
                </p>
            </div>

            <div class="col-md-6">
                <p>
                    <span class="info-label">
                        Composante :
                    </span>

                    ${formation.composante.name}
                </p>
            </div>

        </div>

    </div>

    <!-- OBJECTIF -->

    <div class="section-card">

        <h3 class="section-title">
            Objectif de la formation
        </h3>

        <p>
            ${formation.objectifFormation}
        </p>

    </div>

    <!-- RESULTATS -->

    <div class="section-card">

        <h3 class="section-title">
            Résultats attendus
        </h3>

        <p>
            ${formation.resultatsAttendus}
        </p>

    </div>

    <!-- CONTENU -->

    <div class="section-card">

        <h3 class="section-title">
            Contenu de la formation
        </h3>

        <p style="white-space: pre-line;">
            ${formation.contenuFormation}
        </p>

    </div>

    <!-- RESUME -->

    <div class="section-card">

        <h3 class="section-title">
            Résumé du contenu
        </h3>

        <p>
            ${formation.resumeContenu}
        </p>

    </div>

    <!-- ACTIONS -->

    <h2 class="mb-4 mt-5">
        Actions de formation
    </h2>

    <c:forEach var="action" items="${formation.actions}">

        <div class="section-card action-card">

            <div class="d-flex justify-content-between align-items-center">

                <div>

                    <h4 class="mb-0">
                        ${action.codeAction}
                    </h4>

                </div>

                <button type="button"
                        class="btn btn-outline-primary toggle-btn">
                    Voir détails
                </button>

            </div>

            <div class="hidden-content mt-4">

                <hr>

                <p>
                    <strong>Langue :</strong>
                    ${action.langueFormation}
                </p>

                <p>
                    <strong>Conditions spécifiques :</strong>
                    ${action.conditionSpecifiques}
                </p>

                <p>
                    <strong>Admission :</strong>
                    ${action.infoAdmission}
                </p>

                <p>
                    <strong>Modalités pédagogiques :</strong>
                    ${action.modalitesPedagogiques}
                </p>

                <p>
                    <strong>Frais TTC :</strong>
                    ${action.fraisTtc} €
                </p>

                <p>
                    <strong>URL :</strong>

                    <c:if test="${not empty action.urlAction}">
                        <a href="${action.urlAction}" target="_blank">
                            Consulter le site
                        </a>
                    </c:if>
                </p>

                <h5 class="mt-4 mb-3">
                    Sessions
                </h5>

                <c:forEach var="session" items="${action.sessions}">

                    <div class="session-card p-3 mb-3 rounded bg-light">

                        <h6>
                            ${session.codeSession}
                        </h6>

                        <p>
                            <strong>Date début :</strong>
                            ${session.dateDebut}
                        </p>

                        <p>
                            <strong>Date fin :</strong>
                            ${session.dateFin}
                        </p>

                        <p>
                            <strong>Etat recrutement :</strong>
                            ${session.etatRecrutement}
                        </p>

                        <p>
                            <strong>Garantie :</strong>
                            ${session.garantie}
                        </p>

                    </div>

                </c:forEach>

            </div>

        </div>

    </c:forEach>

     <!-- CONTACTS -->

    <h2 class="mb-4 mt-5">Contact</h2>

    <div class="section-card">

        <c:choose>

            <%-- Cas 1 : un contact est déjà assigné --%>
            <c:when test="${not empty formation.contact}">

                <table class="table table-bordered table-striped">
                    <thead>
                    <tr>
                        <th>Code contact</th>
                        <th>Nom</th>
                        <th>Prénom</th>
                        <th>Téléphone</th>
                        <th>Courriel</th>
                    </tr>
                    </thead>
                    <tbody>
                    <tr>
                        <td>${formation.contact.codeContact}</td>
                        <td>${formation.contact.nom}</td>
                        <td>${formation.contact.prenom}</td>
                        <td>${formation.contact.telephone}</td>
                        <td>${formation.contact.courriel}</td>
                    </tr>
                    </tbody>
                </table>

                <%-- Permettre de changer le contact (brouillon uniquement) --%>
                <c:if test="${formation.status == 'BROUILLON' and not empty contactsDisponibles}">
                    <hr>
                    <form:form method="post" action="${assign}">
                        <div class="form-group mb-3">
                            <label class="info-label">Changer le contact :</label>
                            <select name="codeContact" class="form-control">
                                <c:forEach var="c" items="${contactsDisponibles}">
                                    <option value="${c.codeContact}"
                                            <c:if test="${c.codeContact == formation.contact.codeContact}">selected</c:if>>
                                            ${c.codeContact} - ${c.nom} ${c.prenom}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary btn-floating">Changer le contact</button>
                    </form:form>
                </c:if>

            </c:when>

            <%-- Cas 2 : aucun contact assigné mais des contacts disponibles --%>
            <c:when test="${empty formation.contact and not empty contactsDisponibles}">

                <div class="alert alert-warning">
                    Aucun contact n'est encore assigné à cette formation.
                </div>

                <c:if test="${formation.status == 'BROUILLON'}">
                    <form:form method="post" action="${assign}">
                        <div class="form-group mb-3">
                            <label class="info-label">Choisir un contact :</label>
                            <select name="codeContact" class="form-control">
                                <c:forEach var="c" items="${contactsDisponibles}">
                                    <option value="${c.codeContact}">
                                            ${c.codeContact} - ${c.nom} ${c.prenom}
                                    </option>
                                </c:forEach>
                            </select>
                        </div>
                        <button type="submit" class="btn btn-primary btn-floating">Assigner le contact</button>
                    </form:form>
                </c:if>

            </c:when>

            <%-- Cas 3 : aucun contact disponible --%>
            <c:otherwise>
                <div class="alert alert-info">
                    Aucun contact disponible pour la composante de cette formation.
                </div>
                <sec:authorize access="hasRole('MANAGER')">
                    <a href="${createContact}" class="btn btn-success btn-floating">
                        Créer un contact
                    </a>
                </sec:authorize>
            </c:otherwise>

        </c:choose>

    </div>

<script>

document.addEventListener("DOMContentLoaded", function() {

    document.querySelectorAll('.toggle-btn')
        .forEach(button => {

            button.addEventListener('click', function(){

                let content =
                    this.closest('.section-card')
                        .querySelector('.hidden-content');

                if(content.style.display === 'block'){

                    content.style.display = 'none';

                    this.innerText = 'Voir détails';

                }else{

                    content.style.display = 'block';

                    this.innerText = 'Masquer';

                }

            });

        });

});

</script>

<%@ include file="footer.jsp" %>