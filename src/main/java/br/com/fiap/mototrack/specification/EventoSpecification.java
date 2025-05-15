package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.EventoFilter;
import br.com.fiap.mototrack.model.Evento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * # 🔍 EventoSpecification
 *
 * Esta classe fornece uma **Specification JPA dinâmica** para a entidade `Evento`,
 * permitindo realizar **consultas filtradas e compostas** com base nos parâmetros
 * informados no DTO `EventoFilter`.
 *
 * ---
 * ## ✅ Filtros Suportados
 *
 * - 🔑 Identificadores: `id`, `moto.id`
 * - 🏷️ Atributos do evento: `tipo`, `motivo`, `localizacao`
 * - 📅 Intervalo de datas: `dataInicio`, `dataFim`
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class EventoSpecification {

    /**
     * ## 🧠 Método principal: `comFiltros`
     *
     * Constrói dinamicamente uma `Specification<Evento>` com base nos filtros preenchidos no `EventoFilter`.
     * Os campos são opcionais e adicionados somente quando informados.
     */
    public static Specification<Evento> comFiltros(EventoFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            /**
             * ### 🔍 Filtro por ID do evento
             * Busca exata por identificador único.
             */
            eq(p, cb, root.get("id"), f.id());

            /**
             * ### 🛵 Filtro por ID da moto
             * Permite buscar eventos relacionados a uma determinada moto.
             */
            eq(p, cb, root.get("moto").get("id"), f.motoId());

            /**
             * ### 🏷️ Filtro por Tipo de evento (case-insensitive)
             * Exemplo: "ENTRADA", "SAÍDA", "MANUTENÇÃO"
             */
            eqIgnoreCase(p, cb, root.get("tipo"), f.tipo());

            /**
             * ### 💬 Filtro por Motivo do evento (contém, case-insensitive)
             */
            like(p, cb, root.get("motivo"), f.motivo());

            /**
             * ### 🌍 Filtro por Localização (contém, case-insensitive)
             * Pode representar o nome do pátio, bairro ou área geográfica.
             */
            like(p, cb, root.get("localizacao"), f.localizacao());

            /**
             * ### 📅 Filtro por Data do Evento
             * Permite definir um intervalo entre dataInicio e dataFim.
             */
            range(p, cb, root.get("dataEvento"), f.dataInicio(), f.dataFim());

            /**
             * ### 🔄 Combinação de todos os predicados
             * Todos os filtros aplicáveis são combinados com operador lógico AND.
             */
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // ============================================================================
    // ## 🔧 Métodos auxiliares reutilizáveis para construção de predicados
    // ============================================================================

    /**
     * ### 🧩 `eq` - Igualdade simples
     * Adiciona um predicado do tipo `campo = valor`, se o valor não for nulo.
     */
    private static <T> void eq(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                               jakarta.persistence.criteria.Path<T> path, T value) {
        if (value != null) {
            p.add(cb.equal(path, value));
        }
    }

    /**
     * ### 🧩 `eqIgnoreCase` - Igualdade ignorando maiúsculas/minúsculas
     * Aplica um filtro `LOWER(campo) = LOWER(valor)` para campos de texto.
     */
    private static void eqIgnoreCase(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.equal(cb.lower(path), value.toLowerCase()));
        }
    }

    /**
     * ### 🧩 `like` - Filtro parcial com LIKE (contém), ignorando case
     * Aplica `%valor%` com `LOWER`, ideal para buscas textuais.
     */
    private static void like(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    /**
     * ### 🧩 `range` - Filtro por intervalo (min e max)
     * Adiciona `>=` para `min` e `<=` para `max`, se presentes.
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
