<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <title>Cadastrar Item — Catálogo Simples de Livros/Filmes</title>
</head>
<body>
<h1>Cadastrar Item de Mídia</h1>

<%-- Toda saída de dado que veio do usuário passa por <c:out> — nunca EL cru — para
     prevenir XSS (ver docs/sdd/seguranca/checklist-owasp-top10.md, categoria A03/CWE-79). --%>

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

<form action="${pageContext.request.contextPath}/cadastrarItem" method="post">
    <p>
        <label for="titulo">Título *</label><br>
        <input type="text" id="titulo" name="titulo" maxlength="255"
               value="<c:out value="${titulo}"/>" required>
    </p>
    <p>
        <label for="autorDiretor">Autor/Diretor</label><br>
        <input type="text" id="autorDiretor" name="autorDiretor" maxlength="255"
               value="<c:out value="${autorDiretor}"/>">
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
    <p>
        <label for="tipoMidia">Tipo de Mídia *</label><br>
        <select id="tipoMidia" name="tipoMidia" required>
            <option value="">Selecione...</option>
            <option value="Livro" ${tipoMidia == 'Livro' ? 'selected' : ''}>Livro</option>
            <option value="Filme" ${tipoMidia == 'Filme' ? 'selected' : ''}>Filme</option>
            <option value="Série" ${tipoMidia == 'Série' ? 'selected' : ''}>Série</option>
        </select>
    </p>
    <button type="submit">Cadastrar</button>
</form>
</body>
</html>
