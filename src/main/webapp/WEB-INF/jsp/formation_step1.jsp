<%@include file="header.jsp"%>
<%@page pageEncoding="UTF-8" %>

<c:url value='/formation' var="back"/>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h2>Nouvelle formation</h2>
</div>

<div class="alert alert-warning">
    <strong>Attention</strong> — les informations saisies à cette étape ne pourront pas être modifiées par la suite.
</div>

<form:form modelAttribute="formation" action="/formation/create" method="post">
    <div class="mb-3">
        <label class="form-label">Code formation <span class="text-danger">*</span></label>
        <form:input class="form-control" path="codeFormation"
                    placeholder="Ex: HPS3AAb"/>
        <form:errors path="codeFormation" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Type de certification <span class="text-danger">*</span></label>
        <form:select class="form-select" path="typeCertification">
            <form:option value="" label="-- Sélectionner --"/>
            <form:option value="RNCP" label="RNCP"/>
            <form:option value="RS" label="RS"/>
            <form:option value="CPF" label="CPF"/>
        </form:select>
        <form:errors path="typeCertification" cssClass="text-danger d-block"/>
    </div>

    <div class="mb-3">
        <label class="form-label">Code certification <span class="text-danger">*</span> (ex : RNCP37507, RS5000, CPF202)</label>
        <form:input class="form-control" path="codeCertification"
                    placeholder="Ex: RNCP37507"/>
        <form:errors path="codeCertification" cssClass="text-danger d-block"/>
    </div>

    <c:if test="${not empty composantes}">
        <div class="mb-3">
            <label class="form-label">Composante <span class="text-danger">*</span></label>
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