<%@include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<h2>Créer une nouvelle composante</h2>
<hr>

<form:form modelAttribute="composante" method="post">
    <div class="mb-3">
        <label class="form-label">Code composante <span class="text-danger">*</span></label>
        <form:input class="form-control" path="codeComposante" />
        <form:errors path="codeComposante" cssClass="text-danger small" element="div" />
    </div>
    <div class="mb-3">
        <label class="form-label">Nom <span class="text-danger">*</span></label>
        <form:input class="form-control" path="name" />
        <form:errors path="name" cssClass="text-danger small" element="div" />
    </div>
    <div class="mb-3">
        <label class="form-label">Gestionnaires</label>
        <div class="dropdown">
            <button class="btn btn-outline-secondary dropdown-toggle"
                    type="button"
                    data-bs-toggle="dropdown"
                    aria-expanded="false">
                Sélectionner des managers
            </button>
            <ul class="dropdown-menu p-2">
                <c:forEach var="manager" items="${managers}">
                    <li class="dropdown-check-item">
                        <input type="checkbox"
                               name="managerIds"
                               value="${manager.id}"
                               id="manager${manager.id}"
                               <c:if test="${not empty composanteManagersIds and composanteManagersIds.contains(manager.id)}">
                                   checked="checked"
                               </c:if>>
                        <label for="manager${manager.id}">${manager.firstName} ${manager.lastName}</label>
                    </li>
                </c:forEach>
            </ul>
        </div>
    </div>
    <button type="submit" class="btn btn-primary">Enregistrer</button>
</form:form>

<%@include file="footer.jsp"%>