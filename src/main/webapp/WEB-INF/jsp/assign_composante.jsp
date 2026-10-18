<%@include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<c:url value="/composante/${composante.codeComposante}/assign/add" var="add"/>
<c:url value="/composante/${composante.codeComposante}/assign/remove" var="remove"/>

    <h2>Assigner des gestionnaires</h2>
    <p class="text-muted">Composante : ${composante.name} (${composante.codeComposante})</p>
    <hr>

    <div class="row">

        <div class="col-md-6">
            <div class="card">
                <div class="card-header bg-secondary text-white">
                    <strong>Gestionnaires disponibles</strong>
                </div>
                <ul class="list-group list-group-flush">
                    <c:forEach var="manager" items="${managers}">
                        <c:if test="${!composanteManagersIds.contains(manager.id)}">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span>${manager.lastName} ${manager.firstName}<br>
                                    <small class="text-muted">${manager.email}</small>
                                </span>
                                <form:form action="${add}" method="post">
                                    <input type="hidden" name="managerId" value="${manager.id}" />
                                    <button type="submit" class="btn btn-success btn-sm">Ajouter</button>
                                </form:form>
                            </li>
                        </c:if>
                    </c:forEach>
                    <c:if test="${empty managers}">
                        <li class="list-group-item text-muted">Aucun gestionnaire disponible</li>
                    </c:if>
                </ul>
            </div>
        </div>

        <div class="col-md-6">
            <div class="card">
                <div class="card-header bg-primary text-white">
                    <strong>Gestionnaires assignés</strong>
                </div>
                <ul class="list-group list-group-flush">
                    <c:forEach var="manager" items="${managers}">
                        <c:if test="${composanteManagersIds.contains(manager.id)}">
                            <li class="list-group-item d-flex justify-content-between align-items-center">
                                <span>${manager.lastName} ${manager.firstName}<br>
                                    <small class="text-muted">${manager.email}</small>
                                </span>
                                <form:form action="${remove}" method="post">
                                    <input type="hidden" name="managerId" value="${manager.id}" />
                                    <button type="submit" class="btn btn-danger btn-sm">Retirer</button>
                                </form:form>
                            </li>
                        </c:if>
                    </c:forEach>
                    <c:if test="${empty composanteManagersIds}">
                        <li class="list-group-item text-muted">Aucun gestionnaire assigné</li>
                    </c:if>
                </ul>
            </div>
        </div>

    </div>

    <div class="mt-3">
        <a href="/" class="btn btn-outline-secondary">← Retour</a>
    </div>



<%@include file="footer.jsp"%>
