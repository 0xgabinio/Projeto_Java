<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Editar Filme — Catálogo Simples de Filmes</title>
</head>
<body>
<p><a href="${pageContext.request.contextPath}/listarFilmes">&larr; Voltar para a listagem</a></p>

<c:choose>
    <c:when test="${naoEncontrado}">
        <p>Filme não encontrado.</p>
    </c:when>
    <c:otherwise>
        <h1>Editar Filme</h1>

        <%-- Toda saída de dado que veio do usuário passa por <c:out> — nunca EL cru — para
             prevenir XSS (ver docs/sdd/seguranca/checklist-owasp-top10.md, categoria A03/CWE-79). --%>

        <c:if test="${not empty erros}">
            <ul style="color: red;">
                <c:forEach var="erro" items="${erros}">
                    <li><c:out value="${erro}"/></li>
                </c:forEach>
            </ul>
        </c:if>

        <form action="${pageContext.request.contextPath}/editarFilme" method="post">
            <input type="hidden" name="id" value="${id}">
            <p>
                <label for="titulo">Título *</label><br>
                <input type="text" id="titulo" name="titulo" maxlength="255"
                       value="<c:out value="${titulo}"/>" required>
            </p>
            <p>
                <label for="diretor">Diretor</label><br>
                <input type="text" id="diretor" name="diretor" maxlength="255"
                       value="<c:out value="${diretor}"/>">
            </p>
            <p>
                <label for="anoLancamento">Ano de Lançamento</label><br>
                <input type="text" id="anoLancamento" name="anoLancamento"
                       value="<c:out value="${anoLancamento}"/>">
            </p>
            <p>
                <label for="genero">Gênero</label><br>
                <input type="text" id="genero" name="genero" maxlength="100"
                       value="<c:out value="${genero}"/>">
            </p>
            <p>
                <label for="sinopse">Sinopse</label><br>
                <textarea id="sinopse" name="sinopse" rows="4" cols="50"><c:out value="${sinopse}"/></textarea>
            </p>
            <button type="submit">Salvar</button>
        </form>
    </c:otherwise>
</c:choose>
</body>
</html>
