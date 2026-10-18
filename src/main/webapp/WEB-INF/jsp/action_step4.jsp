<%@ include file="header.jsp" %>
<%@ page pageEncoding="UTF-8" %>

<c:url value="/action/step3?codeAction=${action.codeAction}" var="backToStep3"/>


    <div class="mb-4">
        <p class="text-muted mb-1">Étape 4 / 5</p>
        <div class="progress">
            <div class="progress-bar" style="width: 80%">Frais</div>
        </div>
    </div>

    <h4>Frais pédagogiques</h4>
    <p class="text-muted">Action : <strong>${action.codeAction}</strong></p>
    <hr>

    <form:form modelAttribute="action" method="post" action="/action/step4">
        <form:hidden path="codeAction"/>
        <form:hidden path="formation.codeFormation"/>

        <div class="mb-3">
            <label class="form-label">Taux TVA <span class="text-danger">*</span></label>
            <form:select path="tauxTva" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="tva" items="${tauxTva}">
                    <form:option value="${tva.value}">${tva.description}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="tauxTva" cssClass="text-danger small" element="div"/>
            <form:errors path="tauxTvaValide" cssClass="text-danger small" element="div"/>
        </div>

        <div class="row">
            <div class="col-md-6 mb-3">
                <label class="form-label">Frais HT (€) <span class="text-danger">*</span></label>
                <form:input path="fraisHt" cssClass="form-control" type="number" step="0.01" min="0"/>
                <form:errors path="fraisHt" cssClass="text-danger small" element="div"/>
            </div>
            <div class="col-md-6 mb-3">
                <label class="form-label">Frais TTC (€) <span class="text-danger">*</span></label>
                <form:input path="fraisTtc" cssClass="form-control" type="number" step="0.01" min="0"/>
                <form:errors path="fraisTtc" cssClass="text-danger small" element="div"/>
            </div>
        </div>
        <form:errors path="fraisComplets" cssClass="text-danger small" element="div"/>
        <form:errors path="fraisTtcCoherent" cssClass="text-danger small" element="div"/>

        <div class="mb-3">
            <label class="form-label">Modèle économique</label>
            <form:select path="codeModeleEconomique" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <c:forEach var="m" items="${modelesEconomiques}">
                    <form:option value="${m.code}">${m.description}</form:option>
                </c:forEach>
            </form:select>
            <form:errors path="codeModeleEconomique" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Frais certif inclus dans frais pédagogiques <span class="text-danger">*</span></label>
            <form:select path="fraisCertifInclusFraisAnpec" cssClass="form-select">
                <form:option value="">-- Sélectionner --</form:option>
                <form:option value="1">Oui</form:option>
                <form:option value="0">Non</form:option>
            </form:select>
            <form:errors path="fraisCertifInclusFraisAnpec" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Frais additionnels non pris en charge (€)</label>
            <form:input path="fraisAnpec" cssClass="form-control" type="number" step="0.01" min="0"/>
            <form:errors path="fraisAnpec" cssClass="text-danger small" element="div"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Détail des frais additionnels non pris en charge</label>
            <form:textarea path="detailFraisAnpec" cssClass="form-control" rows="3" maxlength="3000"/>
            <form:errors path="detailFraisAnpec" cssClass="text-danger small" element="div"/>
        </div>

        <hr>
        <div class="d-flex justify-content-between">
            <a href="${backToStep3}"
               class="btn btn-outline-secondary">← Précédent</a>
            <button type="submit" name="submitType" value="save" class="btn btn-secondary">Sauvegarder et quitter</button>
            <button type="submit" name="submitType" value="next" class="btn btn-primary">Suivant →</button>
        </div>
    </form:form>

<%@ include file="footer.jsp" %>
