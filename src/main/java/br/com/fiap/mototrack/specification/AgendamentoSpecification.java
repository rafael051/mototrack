package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.AgendamentoFilter;
import br.com.fiap.mototrack.model.Agendamento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * # 🔍 AgendamentoSpecification
 *
 * Esta classe fornece uma **Specification JPA dinâmica** para a entidade `Agendamento`,
 * permitindo realizar buscas flexíveis com base nos filtros preenchidos no `AgendamentoFilter`.
 *
 * ---
 * ## ✅ Filtros Suportados
 *
 * - 🔑 `id` do agendamento
 * - 🛵 `id` da moto vinculada
 * - 📝 `descrição` (busca parcial, ignorando maiúsculas/minúsculas)
 * - 📅 Intervalo de `dataAgendada`
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class AgendamentoSpecification {

    /**
     * ## 🧠 Método principal: `comFiltros`
     *
     * Constrói dinamicamente uma `Specification<Agendamento>` com base nos filtros preenchidos.
     * Cada filtro é opcional e será aplicado somente se o valor estiver presente.
     */
    public static Specification<Agendamento> comFiltros(AgendamentoFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            /**
             * ### 🔍 Filtro por ID do Agendamento
             * Busca exata, usada para localizar um agendamento específico.
             */
            eq(p, cb, root.get("id"), f.id());

            /**
             * ### 🛵 Filtro por ID da Moto
             * Filtra agendamentos relacionados a uma determinada moto.
             */
            eq(p, cb, root.get("moto").get("id"), f.motoId());

            /**
             * ### 📝 Filtro por Descrição
             * Aplica um LIKE ignorando case para buscar termos contidos na descrição.
             */
            like(p, cb, root.get("descricao"), f.descricao());

            /**
             * ### 📅 Filtro por Intervalo de Datas Agendadas
             * Permite filtrar agendamentos dentro de um período específico.
             */
            range(p, cb, root.get("dataAgendada"), f.dataInicio(), f.dataFim());

            /**
             * ### 🔄 Combinação de todos os filtros
             * Todos os critérios são unidos com operador lógico AND.
             */
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // ============================================================================
    // ## 🔧 Métodos Auxiliares para Composição de Predicados (Filtros)
    // ============================================================================

    /**
     * ### 🧩 `eq` - Igualdade simples (`=`)
     * Aplica um filtro de igualdade apenas se o valor não for nulo.
     */
    private static <T> void eq(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                               jakarta.persistence.criteria.Path<T> path, T value) {
        if (value != null) {
            p.add(cb.equal(path, value));
        }
    }

    /**
     * ### 🧩 `like` - Busca textual parcial (`%valor%`)
     * Converte ambos os lados para lowercase para tornar a busca case-insensitive.
     */
    private static void like(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    /**
     * ### 🧩 `range` - Faixa de valores (`>= min`, `<= max`)
     * Aplica filtro de intervalo entre dois valores comparáveis, como datas.
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
