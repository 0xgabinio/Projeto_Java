<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Detalhe do Filme" scope="request"/>
<%@ include file="common/header.jspf" %>

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
    <c:when test="${naoEncontrado}">
        <p>Filme não encontrado.</p>
    </c:when>
    <c:when test="${not empty filme}">
        <%-- Todos os campos vêm do banco, mas foram inseridos originalmente via formulário de
             cadastro/edição — <c:out> previne XSS persistente/armazenado (CWE-79). --%>
        <div class="row g-4">
            <c:if test="${not empty filme.capaUrl}">
                <div class="col-md-4">
                    <img src="<c:out value="${filme.capaUrl}"/>" alt="Pôster de <c:out value="${filme.titulo}"/>"
                         class="img-fluid rounded shadow-sm">
                </div>
            </c:if>
            <div class="col-md-8">
                <h1><c:out value="${filme.titulo}"/></h1>
                <c:if test="${not empty filme.notaTmdb}">
                    <p><span class="badge text-bg-warning">Nota TMDB: ${filme.notaTmdb}/10</span></p>
                </c:if>
                <p><strong>Diretor:</strong> <c:out value="${filme.diretor}"/></p>
                <p><strong>Ano de Lançamento:</strong>
                    ${filme.anoLancamento > 0 ? filme.anoLancamento : '—'}</p>
                <p><strong>Gênero:</strong> <c:out value="${filme.genero}"/></p>
                <p><strong>Sinopse:</strong></p>
                <p><c:out value="${filme.sinopse}"/></p>

                <div class="d-flex gap-2">
                    <a href="${pageContext.request.contextPath}/editarFilme?id=${filme.id}"
                       class="btn btn-outline-primary">Editar</a>
                    <form action="${pageContext.request.contextPath}/excluirFilme" method="post"
                          onsubmit="return confirm('Tem certeza que deseja excluir este filme?');">
                        <input type="hidden" name="id" value="${filme.id}">
                        <button type="submit" class="btn btn-outline-danger">Excluir</button>
                    </form>
                </div>
            </div>
        </div>
    </c:when>
</c:choose>

<%@ include file="common/footer.jspf" %>
