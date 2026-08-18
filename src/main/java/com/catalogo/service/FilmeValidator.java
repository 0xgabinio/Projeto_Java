package com.catalogo.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Validação dos dados de entrada de {@link com.catalogo.model.Filme}, compartilhada entre
 * {@code CadastrarFilmeServlet} e {@code EditarFilmeServlet} — evita duplicar a mesma lógica
 * de validação em cada Servlet (espírito da Situação-Problema 3 do PDF do enunciado:
 * reduzir duplicação, isolar responsabilidades em classes de serviço).
 */
public final class FilmeValidator {

    private static final int TITULO_MAX = 255;
    private static final int DIRETOR_MAX = 255;
    private static final int GENERO_MAX = 100;

    private FilmeValidator() {
    }

    /**
     * Valida os dados recebidos de um formulário de cadastro/edição de filme, antes de
     * qualquer tentativa de persistência.
     *
     * @param titulo           título já tratado (ver {@link #tratarEntrada(String)})
     * @param diretor          diretor já tratado, pode ser {@code null}
     * @param anoLancamentoStr ano de lançamento como string já tratada, pode ser {@code null}
     * @param genero           gênero já tratado, pode ser {@code null}
     * @return lista de mensagens de erro (vazia se os dados forem válidos)
     */
    public static List<String> validar(String titulo, String diretor, String anoLancamentoStr, String genero) {
        List<String> erros = new ArrayList<>();

        if (titulo == null) {
            erros.add("O campo Título é obrigatório.");
        } else if (titulo.length() > TITULO_MAX) {
            erros.add("O Título deve ter no máximo " + TITULO_MAX + " caracteres.");
        }

        if (diretor != null && diretor.length() > DIRETOR_MAX) {
            erros.add("Diretor deve ter no máximo " + DIRETOR_MAX + " caracteres.");
        }

        if (genero != null && genero.length() > GENERO_MAX) {
            erros.add("Gênero deve ter no máximo " + GENERO_MAX + " caracteres.");
        }

        if (anoLancamentoStr != null) {
            try {
                Integer.parseInt(anoLancamentoStr);
            } catch (NumberFormatException e) {
                erros.add("Ano de Lançamento deve ser um número inteiro.");
            }
        }

        return erros;
    }

    /** Remove espaços nas pontas e converte string vazia em {@code null}. */
    public static String tratarEntrada(String valor) {
        if (valor == null) {
            return null;
        }
        String tratado = valor.trim();
        return tratado.isEmpty() ? null : tratado;
    }
}
