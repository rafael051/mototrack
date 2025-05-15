package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.UsuarioFilter;
import br.com.fiap.mototrack.model.Usuario;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * # 🔍 UsuarioSpecification
 *
 * Classe responsável por criar dinamicamente uma `Specification<Usuario>` com base nos critérios definidos
 * no `UsuarioFilter`. Permite realizar buscas flexíveis por usuários no sistema.
 *
 * ---
 * ## ✅ Filtros Suportados
 *
 * - 🔑 Identificador: `id`
 * - 🧑 Dados pessoais: `nome`, `email`, `perfil`
 *
 * Todos os filtros são opcionais e combináveis entre si.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class UsuarioSpecification {

    /**
     * ## 🧠 Método principal: `comFiltros`
     *
     * Constrói uma `Specification<Usuario>` com base nos filtros preenchidos.
     * Campos nulos ou em branco são ignorados.
     */
    public static Specification<Usuario> comFiltros(UsuarioFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            /**
             * ### 🔍 Filtro por ID do usuário (igualdade exata)
             */
            eq(p, cb, root.get("id"), f.id());

            /**
             * ### 🧑 Filtro por Nome (busca parcial, case-insensitive)
             */
            like(p, cb, root.get("nome"), f.nome());

            /**
             * ### 📧 Filtro por E-mail (busca parcial, case-insensitive)
             * Permite buscar por fragmentos de e-mail.
             */
            like(p, cb, root.get("email"), f.email());

            /**
             * ### 🔐 Filtro por Perfil (igualdade ignorando maiúsculas/minúsculas)
             * Exemplo de perfis: `ADMINISTRADOR`, `GESTOR`, `OPERADOR`
             */
            eqIgnoreCase(p, cb, root.get("perfil"), f.perfil());

            /**
             * ### 🔄 Combinação de todos os critérios com AND
             */
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // ============================================================================
    // ## 🔧 Métodos utilitários para construção de filtros
    // ============================================================================

    /**
     * ### 🧩 `eq` - Igualdade simples
     * Aplica `= valor`, se o valor estiver presente.
     */
    private static <T> void eq(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                               jakarta.persistence.criteria.Path<T> path, T value) {
        if (value != null) {
            p.add(cb.equal(path, value));
        }
    }

    /**
     * ### 🧩 `eqIgnoreCase` - Igualdade sem diferenciar maiúsculas de minúsculas
     */
    private static void eqIgnoreCase(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.equal(cb.lower(path), value.toLowerCase()));
        }
    }

    /**
     * ### 🧩 `like` - Busca parcial com LIKE (`%valor%`) e insensível a caixa
     */
    private static void like(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }
}
