<%@ include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Contacts</h2>
    <a class="btn btn-primary" href="<c:url value='/contact/create'/>">
        Nouveau contact
    </a>
</div>

<c:if test="${empty contacts}">
    <div class="alert alert-info">
        Aucun contact trouvé.
    </div>
</c:if>

<c:if test="${not empty contacts}">

    <table class="table table-bordered table-striped align-middle">

        <thead class="table-light">

        <tr>
            <th>Code</th>
            <th>Nom</th>
            <th>Prénom</th>
            <th>Téléphone</th>
            <th>Courriel</th>
            <th>Ville</th>
            <th>Actions</th>
        </tr>

        </thead>

        <tbody>

        <c:forEach var="contact" items="${contacts}">

            <tr>

                <td>${contact.codeContact}</td>

                <td>${contact.nom}</td>

                <td>${contact.prenom}</td>

                <td>${contact.telephone}</td>

                <td>${contact.courriel}</td>

                <td>${contact.ville}</td>

                <td>


                    <a class="btn btn-sm btn-warning"
                       href="<c:url value='/contact/edit/${contact.codeContact}'/>">
                        Modifier
                    </a>

                   <form method="post"
                         action="<c:url value='/contact/delete/${contact.codeContact}'/>"
                         style="display:inline;">

                       <input type="hidden"
                              name="${_csrf.parameterName}"
                              value="${_csrf.token}" />

                       <button type="submit"
                               class="btn btn-sm btn-danger"
                               onclick="return confirm('Supprimer ce contact ?');">

                           Supprimer

                       </button>

                   </form>

                </td>

            </tr>

        </c:forEach>

        </tbody>

    </table>

</c:if>

<%@ include file="footer.jsp"%>