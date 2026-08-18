<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastrar Filme — Catálogo Simples de Filmes</title>
</head>
<body>
<h1>Cadastrar Filme</h1>

<p><a href="${pageContext.request.contextPath}/listarFilmes">Ver filmes catalogados</a></p>

<%-- Toda saída de dado que veio do usuário passa por <c:out> — nunca EL cru — para
     prevenir XSS (ver docs/sdd/seguranca/checklist-owasp-top10.md, categoria A03/CWE-79). --%>

<c:if test="${not empty erros}">
    <ul style="color: red;">
        <c:forEach var="erro" items="${erros}">
            <li><c:out value="${erro}"/></li>
        </c:forEach>
    </ul>
</c:if>

<form action="${pageContext.request.contextPath}/cadastrarFilme" method="post">
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
    <button type="submit">Cadastrar</button>
</form>
</body>
</html>
