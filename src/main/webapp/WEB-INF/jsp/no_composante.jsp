<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<div class="alert alert-warning mt-4">
    <h5 class="alert-heading">Action impossible</h5>
    <p class="mb-0"><c:out value="${message}"/></p>
</div>

<a href="<c:url value='/'/>" class="btn btn-secondary">Retour à l'accueil</a>

<%@ include file="footer.jsp" %>