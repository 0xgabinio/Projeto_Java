<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Detalhe do Filme — Catálogo Simples de Filmes</title>
</head>
<body>
<p><a href="${pageContext.request.contextPath}/listarFilmes">&larr; Voltar para a listagem</a></p>

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
    <c:when test="${naoEncontrado}">
        <p>Filme não encontrado.</p>
    </c:when>
    <c:when test="${not empty filme}">
        <%-- Todos os campos vêm do banco, mas foram inseridos originalmente via formulário de
             cadastro/edição — <c:out> previne XSS persistente/armazenado (CWE-79). --%>
        <h1><c:out value="${filme.titulo}"/></h1>
        <p><strong>Diretor:</strong> <c:out value="${filme.diretor}"/></p>
        <p><strong>Ano de Lançamento:</strong>
            ${filme.anoLancamento > 0 ? filme.anoLancamento : '—'}</p>
        <p><strong>Gênero:</strong> <c:out value="${filme.genero}"/></p>
        <p><strong>Sinopse:</strong></p>
        <p><c:out value="${filme.sinopse}"/></p>

        <p>
            <a href="${pageContext.request.contextPath}/editarFilme?id=${filme.id}">Editar</a>
        </p>
        <form action="${pageContext.request.contextPath}/excluirFilme" method="post"
              onsubmit="return confirm('Tem certeza que deseja excluir este filme?');">
            <input type="hidden" name="id" value="${filme.id}">
            <button type="submit">Excluir</button>
        </form>
    </c:when>
</c:choose>
</body>
</html>
