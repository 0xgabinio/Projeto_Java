<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%-- Página de erro genérica registrada em web.xml (<error-page>) — rede de segurança para
     qualquer exceção não tratada explicitamente por um Servlet (defesa em profundidade
     adicional às mensagens de erro já tratadas caso a caso). Nunca exibe stack trace ou
     detalhes técnicos ao usuário final (Situação-Problema 2 do PDF) — cada Servlet já loga a
     causa raiz via getServletContext().log(...) antes de chegar aqui, quando aplicável. --%>
<c:set var="pageTitle" value="Erro" scope="request"/>
<%@ include file="common/header.jspf" %>

<div class="alert alert-danger">
    <h1 class="h4">Ocorreu um erro inesperado</h1>
    <p class="mb-0">
        Não foi possível concluir sua solicitação. Tente novamente em instantes ou volte para o
        <a href="${pageContext.request.contextPath}/listarFilmes">catálogo</a>.
    </p>
</div>

<%@ include file="common/footer.jspf" %>
