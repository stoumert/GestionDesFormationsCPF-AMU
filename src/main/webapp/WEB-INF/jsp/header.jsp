<!DOCTYPE html>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@page pageEncoding="UTF-8" %>

<c:url var="homeUrl" value="/"/>
<c:url var="logout" value="/logout"/>
<c:url var="export" value="/export"/>

<html>
<head>
    <meta charset="UTF-8">
    <title>CPF</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <link href="/css/style.css" rel="stylesheet">
    <link href="https://cdn.jsdelivr.net/npm/quill@2.0.3/dist/quill.snow.css" rel="stylesheet">
    <script src="https://cdn.jsdelivr.net/npm/quill@2.0.3/dist/quill.js"></script>
    <script src="${pageContext.request.contextPath}/js/quill-init.js" defer></script>
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container-fluid">
        <a class="navbar-brand" href="${homeUrl}">CPF - AMU</a>
        <button class="navbar-toggler" type="button"
                data-bs-toggle="collapse"
                data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">

                <%-- Visible par tous les utilisateurs authentifiés --%>
                <li class="nav-item">
                    <a class="nav-link" href="<c:url value='/formation'/>">
                        Formations
                    </a>
                </li>

                <%-- Visible par admin uniquement --%>
                <sec:authorize access="hasRole('ADMIN')">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/composante/create'/>">
                            Nouvelle composante
                        </a>
                    </li>
                </sec:authorize>

                <sec:authorize access="hasRole('MANAGER')">
                    <li class="nav-item">
                        <a class="nav-link" href="<c:url value='/contact'/>">
                            Contacts
                        </a>
                    </li>
                </sec:authorize>

            </ul>
            <div class="hstack gap-3">
                <sec:authorize access="hasRole('ADMIN')">
                <a class="btn btn-success" href="${export}">
                    Exporter
                </a>
                </sec:authorize>
                <div class="vr"></div>
                <a class="btn btn-danger" href="${logout}">
                    Se déconnecter
                </a>
            </div>
        </div>
    </div>
</nav>
<div class="container mt-4">