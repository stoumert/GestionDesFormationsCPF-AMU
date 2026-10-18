<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:url value="/formation/${action.formation.codeFormation}/actions" var="back"/>


    <div class="mb-4">
        <p class="text-muted mb-1">Étape 1 / 5</p>
        <div class="progress">
            <div class="progress-bar" style="width: 20%">Identification</div>
        </div>
    </div>

    <h4>Identification de l'action</h4>
    <p class="text-muted">Formation : <strong>${action.formation.codeFormation}</strong></p>
    <hr>

    <form:form modelAttribute="action" method="post" action="/action/step1">
        <form:hidden path="formation.codeFormation"/>

        <div class="mb-3">
            <label class="form-label">Code Action <span class="text-danger">*</span></label>
            <c:choose>
                <c:when test="${isEdit}">
                    <p class="form-control-plaintext"><strong><c:out value="${action.codeAction}"/></strong></p>
                    <form:hidden path="codeAction"/>
                </c:when>
                <c:otherwise>
                    <form:input path="codeAction" cssClass="form-control" maxlength="30"/>
                    <form:errors path="codeAction" cssClass="text-danger small" element="div"/>
                    <form:errors path="codeActionStartWithCodeFormation" cssClass="text-danger small" element="div"/>
                    <small class="text-muted">1 à 30 caractères, identifiant unique de l'action.</small>
                </c:otherwise>
            </c:choose>

        </div>

        <div class="mb-3">
            <label class="form-label">Modalités d'enseignement <span class="text-danger">*</span></label>
            <form:select path="modalitesEnseignement" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="m" items="${modalitesEnseignement}">
                    <form:option value="${m.code}">${m.description}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="modalitesEnseignement" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Modalités d'entrées / sorties <span class="text-danger">*</span></label>
            <form:select path="modalitesEntreesSorties" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="m" items="${modalitesEntreesSorties}">
                    <form:option value="${m.code}">${m.description}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="modalitesEntreesSorties" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Niveau d'entrée obligatoire <span class="text-danger">*</span></label>
            <form:select path="niveauEntreeObligatoire" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <form:option value="0">Non</form:option>
                <form:option value="1">Oui</form:option>
            </form:select>
            <form:errors path="niveauEntreeObligatoire" cssClass="text-danger small" element="div"/>
        </div>
        <div class="mb-3">
            <label class="form-label">Modalités d’admission <span class="text-danger">*</span></label>
            <form:errors path="valideCodesModaliteAdmissions" cssClass="text-danger small" element="div"/>
            <div class="dropdown">
                <button class="btn btn-outline-secondary dropdown-toggle"
                        type="button"
                        data-bs-toggle="dropdown"
                        aria-expanded="false">
                    Sélectionner les modalités d’admission
                </button>
                <ul class="dropdown-menu p-2">
                    <c:forEach var="codeAdmission" items="${modaliteAdmission}">
                        <li class="dropdown-check-item">
                            <input type="checkbox"
                                   name="codeModaliteAdmissions"
                                   value="${codeAdmission.code}"
                                   id="admission_${codeAdmission.code}"
                                   <c:if test="${not empty action.codeModaliteAdmissions and action.codeModaliteAdmissions.contains(codeAdmission.code)}">
                                       checked="checked"
                                   </c:if>>
                            <label for="admission_${codeAdmission.code}">${codeAdmission.description}</label>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>

        <div class="mb-3">
            <label class="form-label">Rythme de la formation <span class="text-danger">*</span></label>
            <form:errors path="valideCodesRythmeFormations" cssClass="text-danger small" element="div"/>
            <div class="dropdown">
                <button class="btn btn-outline-secondary dropdown-toggle"
                        type="button"
                        data-bs-toggle="dropdown"
                        aria-expanded="false">
                    Sélectionner les rythmes de la formation
                </button>
                <ul class="dropdown-menu p-2">
                    <c:forEach var="rythme" items="${rythmeFormation}">
                        <li class="dropdown-check-item">
                            <input type="checkbox"
                                   name="codeRythmeFormations"
                                   value="${rythme.code}"
                                   id="rythme_${rythme.code}"
                                   <c:if test="${not empty action.codeRythmeFormations and action.codeRythmeFormations.contains(rythme.code)}">
                                       checked="checked"
                                   </c:if>>
                            <label for="rythme_${rythme.code}">${rythme.libelle}</label>
                        </li>
                    </c:forEach>
                </ul>
            </div>
        </div>
        <div class="mb-3">
            <label class="form-label">Langue de formation <span class="text-danger">*</span></label>
            <form:select path="langueFormation" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="langue" items="${langues}">
                    <form:option value="${langue}">${langue.language}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="langueFormation" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">URL Action</label>
            <form:input path="urlAction" cssClass="form-control" maxlength="400" placeholder="https://..."/>
            <form:errors path="urlAction" cssClass="text-danger small" element="div"/>
            <form:errors path="urlActionValide" cssClass="text-danger small" element="div"/>
        </div>

        <hr>
        <div class="d-flex justify-content-between">
            <a href="${back}"
               class="btn btn-outline-secondary">Annuler</a>
            <button type="submit" name="submitType" value="save" class="btn btn-secondary">Sauvegarder et quitter</button>
            <button type="submit" name="submitType" value="next" class="btn btn-primary">Suivant →</button>
        </div>
    </form:form>


<%@ include file="footer.jsp" %>
