<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Cadastrar Filme" scope="request"/>
<%@ include file="common/header.jspf" %>

<h1 class="mb-4">Cadastrar Filme</h1>

<%-- Toda saída de dado que veio do usuário passa por <c:out> — nunca EL cru — para
     prevenir XSS (ver docs/sdd/seguranca/checklist-owasp-top10.md, categoria A03/CWE-79). --%>

<c:if test="${not empty erros}">
    <div class="alert alert-danger">
        <ul class="mb-0">
            <c:forEach var="erro" items="${erros}">
                <li><c:out value="${erro}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<%-- Busca/importação do TMDB (specs/integrar-tmdb.md, RF-03/RF-04) — some silenciosamente se
     tmdb.properties não estiver configurado (RNF-04), sem afetar o cadastro manual. --%>
<c:if test="${tmdbConfigurado}">
    <div class="card mb-4">
        <div class="card-body">
            <h2 class="h5 card-title">Buscar no TMDB (opcional)</h2>
            <form action="${pageContext.request.contextPath}/cadastrarFilme" method="get" class="d-flex gap-2">
                <input type="text" name="buscaTmdb" class="form-control" maxlength="255"
                       value="<c:out value="${buscaTmdb}"/>" placeholder="Título do filme...">
                <button type="submit" class="btn btn-outline-primary">Buscar</button>
            </form>

            <c:if test="${not empty resultadosTmdb}">
                <div class="row row-cols-2 row-cols-md-5 g-3 mt-2">
                    <c:forEach var="resultado" items="${resultadosTmdb}">
                        <div class="col">
                            <div class="card h-100">
                                <c:if test="${not empty resultado.capaUrl}">
                                    <img src="<c:out value="${resultado.capaUrl}"/>"
                                         alt="Pôster de <c:out value="${resultado.titulo}"/>" class="card-img-top">
                                </c:if>
                                <div class="card-body p-2">
                                    <p class="card-text small mb-1">
                                        <c:out value="${resultado.titulo}"/>
                                        (${resultado.anoLancamento > 0 ? resultado.anoLancamento : '—'})
                                    </p>
                                    <a href="${pageContext.request.contextPath}/cadastrarFilme?tmdbId=${resultado.tmdbId}"
                                       class="btn btn-sm btn-outline-primary w-100">Usar este</a>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${not empty buscaTmdb and empty resultadosTmdb}">
                <p class="mt-2 mb-0">Nenhum resultado do TMDB para "<c:out value="${buscaTmdb}"/>".</p>
            </c:if>
        </div>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/cadastrarFilme" method="post" class="row g-3">
    <div class="col-md-8">
        <label for="titulo" class="form-label">Título *</label>
        <input type="text" id="titulo" name="titulo" class="form-control" maxlength="255"
               value="<c:out value="${titulo}"/>" required>
    </div>
    <div class="col-md-4">
        <label for="anoLancamento" class="form-label">Ano de Lançamento</label>
        <input type="text" id="anoLancamento" name="anoLancamento" class="form-control"
               value="<c:out value="${anoLancamento}"/>">
    </div>
    <div class="col-md-8">
        <label for="diretor" class="form-label">Diretor</label>
        <input type="text" id="diretor" name="diretor" class="form-control" maxlength="255"
               value="<c:out value="${diretor}"/>">
    </div>
    <div class="col-md-4">
        <label for="genero" class="form-label">Gênero</label>
        <input type="text" id="genero" name="genero" class="form-control" maxlength="100"
               value="<c:out value="${genero}"/>">
    </div>
    <div class="col-12">
        <label for="sinopse" class="form-label">Sinopse</label>
        <textarea id="sinopse" name="sinopse" class="form-control" rows="4"><c:out value="${sinopse}"/></textarea>
    </div>
    <div class="col-md-8">
        <label for="capaUrl" class="form-label">URL do Pôster (opcional)</label>
        <input type="text" id="capaUrl" name="capaUrl" class="form-control" maxlength="500"
               value="<c:out value="${capaUrl}"/>" placeholder="https://...">
    </div>
    <c:if test="${not empty capaUrl}">
        <div class="col-md-4">
            <img src="<c:out value="${capaUrl}"/>" alt="Pré-visualização do pôster" class="img-thumbnail" style="max-width:150px;">
        </div>
    </c:if>
    <div class="col-12">
        <button type="submit" class="btn btn-primary">Cadastrar</button>
    </div>
</form>

<%@ include file="common/footer.jspf" %>
