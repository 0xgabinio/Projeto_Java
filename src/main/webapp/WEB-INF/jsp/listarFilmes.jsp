<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Filmes Catalogados" scope="request"/>
<%@ include file="common/header.jspf" %>

<h1 class="mb-4">Filmes Catalogados</h1>

<div class="d-flex justify-content-between align-items-start flex-wrap gap-3 mb-3">
    <%-- Busca por título ou diretor (specs/buscar-filme.md) — GET simples na própria listagem;
         "termo" é reexibido via <c:out> (previne XSS refletido). --%>
    <form action="${pageContext.request.contextPath}/listarFilmes" method="get" class="d-flex gap-2">
        <input type="text" name="termo" class="form-control" maxlength="255"
               value="<c:out value="${termo}"/>" placeholder="Buscar por título ou diretor">
        <button type="submit" class="btn btn-outline-primary">Buscar</button>
        <c:if test="${not empty termo}">
            <a href="${pageContext.request.contextPath}/listarFilmes" class="btn btn-outline-secondary">Limpar</a>
        </c:if>
    </form>
    <a href="${pageContext.request.contextPath}/cadastrarFilme" class="btn btn-primary">Cadastrar novo filme</a>
</div>

<c:if test="${not empty mensagemSucesso}">
    <div class="alert alert-success"><c:out value="${mensagemSucesso}"/></div>
</c:if>

<c:if test="${not empty erros}">
    <div class="alert alert-danger">
        <ul class="mb-0">
            <c:forEach var="erro" items="${erros}">
                <li><c:out value="${erro}"/></li>
            </c:forEach>
        </ul>
    </div>
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
        <div class="table-responsive">
            <table class="table table-striped align-middle">
                <thead>
                <tr>
                    <th>Pôster</th>
                    <th>Título</th>
                    <th>Diretor</th>
                    <th>Ano</th>
                    <th>Gênero</th>
                    <th>Nota TMDB</th>
                    <th></th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="filme" items="${filmes}">
                    <tr>
                        <td>
                            <c:if test="${not empty filme.capaUrl}">
                                <img src="<c:out value="${filme.capaUrl}"/>" alt="Pôster" class="rounded" style="width:48px;">
                            </c:if>
                        </td>
                        <td><c:out value="${filme.titulo}"/></td>
                        <td><c:out value="${filme.diretor}"/></td>
                        <td>${filme.anoLancamento > 0 ? filme.anoLancamento : '—'}</td>
                        <td><c:out value="${filme.genero}"/></td>
                        <td>
                            <c:if test="${not empty filme.notaTmdb}">
                                <span class="badge text-bg-warning">${filme.notaTmdb}/10</span>
                            </c:if>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/detalharFilme?id=${filme.id}"
                               class="btn btn-sm btn-outline-primary">Ver detalhes</a>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf" %>
