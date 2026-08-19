<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Descobrir Filmes" scope="request"/>
<%@ include file="common/header.jspf" %>

<h1 class="mb-1">Descobrir Filmes Populares</h1>
<p class="text-muted">
    <small>Dados fornecidos por <a href="https://www.themoviedb.org/" target="_blank" rel="noopener">TMDB</a>.</small>
</p>

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
    <c:when test="${tmdbIndisponivel}">
        <div class="alert alert-warning">
            Não foi possível carregar os filmes populares do TMDB agora. Tente novamente mais
            tarde — isso não afeta o seu catálogo (cadastro, listagem, edição e exclusão
            continuam funcionando normalmente).
        </div>
    </c:when>
    <c:when test="${not empty filmes}">
        <%-- Componente Carousel do Bootstrap (specs/estilizar-interface.md, RNF-04). Todo dado
             do TMDB passa por <c:out>, mesmo padrão de XSS do restante do projeto. --%>
        <div id="carrosselFilmes" class="carousel slide bg-light rounded p-4" data-bs-ride="carousel">
            <div class="carousel-indicators">
                <c:forEach var="filme" items="${filmes}" varStatus="status">
                    <button type="button" data-bs-target="#carrosselFilmes"
                            data-bs-slide-to="${status.index}"
                            class="${status.first ? 'active' : ''}"
                            aria-label="Slide ${status.index + 1}"></button>
                </c:forEach>
            </div>
            <div class="carousel-inner">
                <c:forEach var="filme" items="${filmes}" varStatus="status">
                    <div class="carousel-item ${status.first ? 'active' : ''}">
                        <div class="d-flex justify-content-center">
                            <div class="card" style="width: 18rem;">
                                <c:if test="${not empty filme.capaUrl}">
                                    <img src="<c:out value="${filme.capaUrl}"/>"
                                         alt="Pôster de <c:out value="${filme.titulo}"/>"
                                         class="card-img-top" style="height:420px; object-fit:cover;">
                                </c:if>
                                <div class="card-body">
                                    <h5 class="card-title"><c:out value="${filme.titulo}"/></h5>
                                    <p class="card-text">
                                        ${filme.anoLancamento > 0 ? filme.anoLancamento : '—'}
                                        &middot;
                                        <span class="badge text-bg-warning">
                                            ${not empty filme.notaTmdb ? filme.notaTmdb : '—'}/10
                                        </span>
                                    </p>
                                    <form action="${pageContext.request.contextPath}/importarFilmeTmdb" method="post">
                                        <input type="hidden" name="tmdbId" value="${filme.tmdbId}">
                                        <button type="submit" class="btn btn-primary w-100">Adicionar ao catálogo</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
            <button class="carousel-control-prev" type="button" data-bs-target="#carrosselFilmes" data-bs-slide="prev">
                <span class="carousel-control-prev-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Anterior</span>
            </button>
            <button class="carousel-control-next" type="button" data-bs-target="#carrosselFilmes" data-bs-slide="next">
                <span class="carousel-control-next-icon" aria-hidden="true"></span>
                <span class="visually-hidden">Próximo</span>
            </button>
        </div>
    </c:when>
    <c:otherwise>
        <p>Nenhum filme popular encontrado no momento.</p>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf" %>
