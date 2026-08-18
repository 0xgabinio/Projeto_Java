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

<%-- Busca por título ou diretor (specs/buscar-filme.md) — GET simples na própria listagem;
     "termo" é reexibido via <c:out> (previne XSS refletido). --%>
<form action="${pageContext.request.contextPath}/listarFilmes" method="get">
    <label for="termo">Buscar por título ou diretor</label><br>
    <input type="text" id="termo" name="termo" maxlength="255" value="<c:out value="${termo}"/>">
    <button type="submit">Buscar</button>
    <c:if test="${not empty termo}">
        <a href="${pageContext.request.contextPath}/listarFilmes">Limpar busca</a>
    </c:if>
</form>

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
    <c:when test="${empty filmes and not empty termo}">
        <p>Nenhum filme encontrado para "<c:out value="${termo}"/>".</p>
    </c:when>
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
                <th></th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="filme" items="${filmes}">
                <tr>
                    <td><c:out value="${filme.titulo}"/></td>
                    <td><c:out value="${filme.diretor}"/></td>
                    <td>${filme.anoLancamento > 0 ? filme.anoLancamento : '—'}</td>
                    <td><c:out value="${filme.genero}"/></td>
                    <td>
                        <a href="${pageContext.request.contextPath}/detalharFilme?id=${filme.id}">Ver detalhes</a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:otherwise>
</c:choose>
</body>
</html>
