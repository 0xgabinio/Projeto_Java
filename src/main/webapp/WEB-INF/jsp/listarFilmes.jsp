<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Filmes Catalogados — Catálogo Simples de Filmes</title>
</head>
<body>
<h1>Filmes Catalogados</h1>

<p><a href="${pageContext.request.contextPath}/cadastrarFilme">Cadastrar novo filme</a></p>

<c:if test="${not empty mensagemSucesso}">
    <p style="color: green;"><c:out value="${mensagemSucesso}"/></p>
</c:if>

<c:if test="${not empty erros}">
    <ul style="color: red;">
        <c:forEach var="erro" items="${erros}">
            <li><c:out value="${erro}"/></li>
        </c:forEach>
    </ul>
</c:if>

<c:choose>
    <c:when test="${empty filmes}">
        <p>Nenhum filme cadastrado ainda.</p>
    </c:when>
    <c:otherwise>
        <%-- Todos os campos vêm do banco, mas foram inseridos originalmente via formulário de
             cadastro — <c:out> aqui previne XSS persistente/armazenado (CWE-79). --%>
        <table border="1" cellpadding="4">
            <thead>
            <tr>
                <th>Título</th>
                <th>Diretor</th>
                <th>Ano</th>
                <th>Gênero</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="filme" items="${filmes}">
                <tr>
                    <td><c:out value="${filme.titulo}"/></td>
                    <td><c:out value="${filme.diretor}"/></td>
                    <td>${filme.anoLancamento > 0 ? filme.anoLancamento : '—'}</td>
                    <td><c:out value="${filme.genero}"/></td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
