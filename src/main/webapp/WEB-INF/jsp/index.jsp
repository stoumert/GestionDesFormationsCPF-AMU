<%@ include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Composantes</h2>
</div>

<table class="table table-bordered table-striped align-middle">
    <thead class="table-light">
        <tr>
            <th>Code composante</th>
            <th>Nom de la composante</th>
            <sec:authorize access="hasRole('ADMIN')">
                <th>Actions</th>
            </sec:authorize>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="composante" items="${composantes}">
            <tr>
                <td><strong>${composante.codeComposante}</strong></td>
                <td>${composante.name}</td>
                <sec:authorize access="hasRole('ADMIN')">
                    <td>
                        <a href="/composante/${composante.codeComposante}/assign"
                           class="btn btn-sm btn-outline-primary">
                            Assigner des gestionnaires
                        </a>
                    </td>
                </sec:authorize>
            </tr>
        </c:forEach>
    </tbody>
</table>

<%@include file="footer.jsp"%>
