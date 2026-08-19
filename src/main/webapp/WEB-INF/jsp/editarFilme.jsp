<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Editar Filme" scope="request"/>
<%@ include file="common/header.jspf" %>

<c:choose>
    <c:when test="${naoEncontrado}">
        <p>Filme não encontrado.</p>
    </c:when>
    <c:otherwise>
        <h1 class="mb-4">Editar Filme</h1>

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

        <form action="${pageContext.request.contextPath}/editarFilme" method="post" class="row g-3">
            <input type="hidden" name="id" value="${id}">
            <%-- notaTmdb não é editável aqui — só sobrevive à edição via campo oculto
                 (ver EditarFilmeServlet e specs/integrar-tmdb.md). --%>
            <input type="hidden" name="notaTmdb" value="<c:out value="${notaTmdb}"/>">

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
                <button type="submit" class="btn btn-primary">Salvar</button>
            </div>
        </form>
    </c:otherwise>
</c:choose>

<%@ include file="common/footer.jspf" %>
