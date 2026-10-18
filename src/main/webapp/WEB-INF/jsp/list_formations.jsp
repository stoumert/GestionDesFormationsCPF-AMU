<%@ include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<c:if test="${not empty success}">
    <div class="alert alert-success">${success}</div>
</c:if>
<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>


    <div class="d-flex justify-content-between align-items-center mb-3">
        <h2>Catalogue des formations</h2>
        <sec:authorize access="hasAnyRole('ADMIN', 'MANAGER')">
            <a href="<c:url value='/formation/create'/>" class="btn btn-primary">
                Nouvelle formation
            </a>
        </sec:authorize>
    </div>

    <form method="get" action="<c:url value='/formation'/>" class="row g-2 mb-4">
        <div class="col-md-3">
            <input type="text" name="intitule" value="${intitule}"
                   class="form-control" placeholder="Intitulé"/>
        </div>
        <div class="col-md-2">
            <input type="text" name="code" value="${code}"
                   class="form-control" placeholder="Code formation"/>
        </div>
        <div class="col-md-2">
            <select name="codeComposante" class="form-select">
                <option value="">Toutes les composantes</option>
                <c:forEach var="comp" items="${composantes}">
                    <option value="${comp.codeComposante}"
                        ${codeComposante == comp.codeComposante ? 'selected' : ''}>
                            ${comp.name}
                    </option>
                </c:forEach>
            </select>
        </div>
        <div class="col-md-2">
            <select name="typeCertification" class="form-select">
                <option value="">Tous les types</option>
                <option value="RNCP" ${typeCertification == 'RNCP' ? 'selected' : ''}>RNCP</option>
                <option value="RS"   ${typeCertification == 'RS'   ? 'selected' : ''}>RS</option>
                <option value="CPF"  ${typeCertification == 'CPF'  ? 'selected' : ''}>CPF</option>
            </select>
        </div>
        <div class="col-md-2">
            <input type="text" name="codeCertification" value="${codeCertification}"
                   class="form-control" placeholder="Code certification"/>
        </div>
        <div class="col-md-1">
            <button type="submit" class="btn btn-secondary w-100">Filtrer</button>
        </div>
        <c:if test="${not empty intitule or not empty code or not empty codeComposante
                        or not empty typeCertification or not empty codeCertification}">
            <div class="col-auto">
                <a href="<c:url value='/formation'/>" class="btn btn-outline-secondary">✕</a>
            </div>
        </c:if>
    </form>

    <table class="table table-striped">
        <thead class="thead-dark">
        <tr>
            <th>Code</th>
            <th>Intitulé</th>
            <th>Statut</th>
            <th>Code certification</th>
            <th>Composante</th>

        </tr>
        </thead>
        <tbody>
        <c:forEach var="f" items="${formations}">
            <tr>
                <td>${f.codeFormation}</td>
                <td>
                    <a href="<c:url value='/formation/view/${f.codeFormation}'/>">
                        ${f.intitule}
                    </a>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${f.status == 'BROUILLON'}">
                            <span class="badge bg-warning text-dark">Brouillon</span>
                        </c:when>
                        <c:when test="${f.status == 'ACTIF'}">
                            <span class="badge bg-success">Actif</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-secondary">Archivé</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${f.codeCertification}</td>
                <td>${f.composante.name}</td>


            </tr>
        </c:forEach>
        <c:if test="${empty formations}">
            <tr>
                <td colspan="6" class="text-center text-muted">
                    Aucune formation disponible
                </td>
            </tr>
        </c:if>
        </tbody>
    </table>

<%@include file="footer.jsp"%>