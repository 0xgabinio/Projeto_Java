/*
 * Alternância de modo claro/escuro (specs/estilizar-interface.md) usando o suporte nativo do
 * Bootstrap 5.3+ a temas via atributo "data-bs-theme". A escolha do usuário fica salva em
 * localStorage (só no navegador, nunca enviada ao servidor — não é dado do usuário que passe
 * por validação/persistência no backend).
 *
 * A DETECÇÃO e APLICAÇÃO inicial do tema roda inline no <head> (ver header.jspf), de forma
 * síncrona, antes da página desenhar — evita o "flash" de tema errado. Este arquivo só cuida
 * do botão de alternância, que precisa do DOM já carregado.
 */
document.addEventListener("DOMContentLoaded", function () {
    var botao = document.getElementById("alternarTema");
    if (!botao) {
        return;
    }

    var html = document.documentElement;

    function atualizarBotao() {
        var escuro = html.getAttribute("data-bs-theme") === "dark";
        botao.textContent = escuro ? "☀️" : "🌙";
        botao.setAttribute("aria-label", escuro ? "Mudar para modo claro" : "Mudar para modo escuro");
    }

    atualizarBotao();

    botao.addEventListener("click", function () {
        var novoTema = html.getAttribute("data-bs-theme") === "dark" ? "light" : "dark";
        html.setAttribute("data-bs-theme", novoTema);
        try {
            localStorage.setItem("catalogoTema", novoTema);
        } catch (erroArmazenamento) {
            // localStorage pode estar indisponível (modo privado restrito, etc.) — a troca de
            // tema ainda funciona nesta sessão, só não persiste entre visitas.
        }
        atualizarBotao();
    });
});
