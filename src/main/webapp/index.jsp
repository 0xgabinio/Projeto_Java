<%@ page contentType="text/html;charset=UTF-8" language="java" %><%
    // Único módulo implementado até agora é o cadastro (specs/cadastrar-filme.md).
    // Quando listar-filmes.md for implementada, trocar este redirect pela listagem.
    response.sendRedirect(request.getContextPath() + "/cadastrarFilme");
%>
