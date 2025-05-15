package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.FilialFilter;
import br.com.fiap.mototrack.model.Filial;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * # 🔍 FilialSpecification
 *
 * Classe responsável por construir dinamicamente uma `Specification<Filial>` com base nos filtros
 * definidos no DTO `FilialFilter`. Ideal para buscas flexíveis via API.
 *
 * ---
 * ## ✅ Filtros Suportados
 *
 * - 🔑 Identificador: `id`
 * - 🏷️ Dados da filial: `nome`, `bairro`, `cidade`, `estado`, `cep`
 *
 * Todos os campos são opcionais e podem ser combinados livremente.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class FilialSpecification {

    /**
     * ## 🧠 Método principal: `comFiltros`
     *
     * Cria a `Specification<Filial>` baseada nos parâmetros preenchidos do filtro.
     * Cada campo, se presente, é adicionado como critério de busca.
     */
    public static Specification<Filial> comFiltros(FilialFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            /**
             * ### 🔍 Filtro por ID da filial
             * Busca exata pelo identificador único.
             */
            eq(p, cb, root.get("id"), f.id());

            /**
             * ### 🏷️ Filtro por Nome da filial (parcial, case-insensitive)
             */
            like(p, cb, root.get("nome"), f.nome());

            /**
             * ### 🏷️ Filtro por Bairro da filial (parcial, case-insensitive)
             */
            like(p, cb, root.get("bairro"), f.bairro());

            /**
             * ### 🏷️ Filtro por Cidade da filial (parcial, case-insensitive)
             */
            like(p, cb, root.get("cidade"), f.cidade());

            /**
             * ### 🏷️ Filtro por Estado da filial (igualdade ignorando case)
             * Ex: "SP", "RJ", "MG"
             */
            eqIgnoreCase(p, cb, root.get("estado"), f.estado());

            /**
             * ### 🏷️ Filtro por CEP da filial (parcial, case-insensitive)
             */
            like(p, cb, root.get("cep"), f.cep());

            /**
             * ### 🔄 Combinação dos predicados com operador AND
             */
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // ============================================================================
    // ## 🔧 Métodos auxiliares reutilizáveis
    // ============================================================================

    /**
     * ### 🧩 `eq` - Igualdade simples
     */
    private static <T> void eq(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                               jakarta.persistence.criteria.Path<T> path, T value) {
        if (value != null) {
            p.add(cb.equal(path, value));
        }
    }

    /**
     * ### 🧩 `eqIgnoreCase` - Igualdade sem considerar maiúsculas/minúsculas
     */
    private static void eqIgnoreCase(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.equal(cb.lower(path), value.toLowerCase()));
        }
    }

    /**
     * ### 🧩 `like` - Busca parcial com LIKE (case-insensitive)
     */
    private static void like(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }
}
