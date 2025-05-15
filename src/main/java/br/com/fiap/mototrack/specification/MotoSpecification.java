package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.model.Moto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * # 🔍 MotoSpecification
 *
 * Esta classe implementa uma **Specification JPA dinâmica** para a entidade `Moto`.
 * Ela permite aplicar **filtros combinados** com base nos parâmetros fornecidos por `MotoFilter`,
 * sem precisar escrever queries fixas no repositório.
 *
 * ---
 * ## ✅ Filtros Suportados
 *
 * - 🔑 Campos básicos: `id`, `placa`, `modelo`, `marca`, `status`
 * - 📅 Campos temporais: `ano` (min/max), `dataCriacao` (intervalo)
 * - 🔗 Relacionamento: `filial.id`
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class MotoSpecification {

    /**
     * ## 🧠 Método principal: `comFiltros`
     *
     * Constrói uma `Specification<Moto>` a partir de um `MotoFilter`.
     * Cada campo do filtro é opcional e, se estiver presente, será adicionado como critério.
     */
    public static Specification<Moto> comFiltros(MotoFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            /**
             * ### 🔍 Filtro por ID (igualdade exata)
             * Caso o ID esteja presente no filtro, a busca será feita diretamente por ele.
             */
            eq(p, cb, root.get("id"), f.id());

            /**
             * ### 🔍 Filtro por Placa (busca parcial, ignorando maiúsculas/minúsculas)
             * Permite buscar motos por parte da placa, ex: "abc" encontra "ABC1234"
             */
            like(p, cb, root.get("placa"), f.placa());

            /**
             * ### 🔍 Filtro por Modelo (busca parcial, ignorando case)
             */
            like(p, cb, root.get("modelo"), f.modelo());

            /**
             * ### 🔍 Filtro por Marca (busca parcial, ignorando case)
             */
            like(p, cb, root.get("marca"), f.marca());

            /**
             * ### 🔍 Filtro por Status (igualdade exata, ignorando case)
             * Exemplo de status: "DISPONÍVEL", "EM_MANUTENÇÃO", etc.
             */
            eqIgnoreCase(p, cb, root.get("status"), f.status());

            /**
             * ### 📅 Filtro por Faixa de Ano (min/max)
             * Permite filtrar motos com ano de fabricação dentro de um intervalo.
             */
            range(p, cb, root.get("ano"), f.anoMin(), f.anoMax());

            /**
             * ### 📅 Filtro por Data de Criação
             * Define um intervalo de datas para quando a moto foi cadastrada no sistema.
             */
            range(p, cb, root.get("dataCriacao"), f.dataCriacaoInicio(), f.dataCriacaoFim());

            /**
             * ### 🔗 Filtro por Filial
             * Permite buscar apenas motos vinculadas a uma determinada filial.
             */
            if (f.filialId() != null) {
                p.add(cb.equal(root.get("filial").get("id"), f.filialId()));
            }

            /**
             * ### 🔄 Combinação dos filtros
             * Todos os critérios são combinados com operador lógico AND.
             */
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // ============================================================================
    // ## 🔧 Métodos auxiliares para simplificar a criação dos predicados (filtros)
    // ============================================================================

    /**
     * ### 🧩 `eq` - Igualdade simples
     * Adiciona um predicado de igualdade (`=`) se o valor não for nulo.
     */
    private static <T> void eq(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                               jakarta.persistence.criteria.Path<T> path, T value) {
        if (value != null) {
            p.add(cb.equal(path, value));
        }
    }

    /**
     * ### 🧩 `eqIgnoreCase` - Igualdade ignorando letras maiúsculas
     * Converte o valor e o campo para minúsculo antes de comparar.
     * Útil para enums armazenados como texto, como `status`.
     */
    private static void eqIgnoreCase(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.equal(cb.lower(path), value.toLowerCase()));
        }
    }

    /**
     * ### 🧩 `like` - Busca parcial (contém), ignorando case
     * Aplica um `LIKE` com `%valor%`, para facilitar buscas por parte do texto.
     */
    private static void like(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    /**
     * ### 🧩 `range` - Faixa de valores
     * Permite aplicar `>=` e/ou `<=` para campos comparáveis como datas e números.
     */
    private static <T extends Comparable<? super T>> void range(List<Predicate> p,
                                                                jakarta.persistence.criteria.CriteriaBuilder cb, jakarta.persistence.criteria.Path<T> path,
                                                                T min, T max) {

        if (min != null) {
            p.add(cb.greaterThanOrEqualTo(path, min));
        }
        if (max != null) {
            p.add(cb.lessThanOrEqualTo(path, max));
        }
    }
}
