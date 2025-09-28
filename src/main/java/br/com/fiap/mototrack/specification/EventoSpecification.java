package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.EventoFilter;
import br.com.fiap.mototrack.model.Evento;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * # 🔍 EventoSpecification
 *
 * Monta dinamicamente uma {@link Specification} para a entidade {@link Evento}
 * a partir dos valores (opcionais) recebidos em {@link EventoFilter}.
 *
 * ## Filtros suportados
 * - 🔑 Identificadores: `id` (Evento) e `moto.id`
 * - 🏷️ Atributos textuais: `tipo` (igualdade, case-insensitive), `motivo` e `localizacao` (LIKE, case-insensitive)
 * - 📅 Período: `dataInicio` e `dataFim` (ambos `LocalDate`) aplicados sobre o campo **dataHora** (`LocalDateTime`)
 *
 * Obs.: O atributo de data na entidade chama-se **dataHora**. Evite usar nomes inexistentes como "dataEvento".
 *
 * @author Rafael
 * @since 1.0
 */
public class EventoSpecification {

    /**
     * ## 🧠 Método principal: comFiltros
     *
     * Constrói a Specification com base nos campos presentes no filtro.
     * Cada critério é adicionado somente se houver valor no filtro.
     */
    public static Specification<Evento> comFiltros(EventoFilter f) {
        return (root, query, cb) -> {
            // Se por algum motivo o filtro vier nulo, não aplicar restrições
            if (f == null) {
                return cb.conjunction();
            }

            List<Predicate> p = new ArrayList<>();

            // -----------------------------------------------------------------
            // 🔑 IDs
            // -----------------------------------------------------------------

            // id do evento (igualdade exata)
            eq(p, cb, root.get("id"), f.id());

            // id da moto (igualdade exata no relacionamento)
            eq(p, cb, root.get("moto").get("id"), f.motoId());

            // -----------------------------------------------------------------
            // 🏷️ Atributos textuais
            // -----------------------------------------------------------------

            // tipo: igualdade ignorando maiúsculas/minúsculas
            eqIgnoreCase(p, cb, root.get("tipo"), f.tipo());

            // motivo: busca parcial (LIKE %valor%), case-insensitive
            like(p, cb, root.get("motivo"), f.motivo());

            // localizacao: busca parcial (LIKE %valor%), case-insensitive
            like(p, cb, root.get("localizacao"), f.localizacao());

            // -----------------------------------------------------------------
            // 📅 Período (dataInicio/dataFim são LocalDate; campo é LocalDateTime)
            // -----------------------------------------------------------------
            // - Convertemos:
            //   * dataInicio -> atStartOfDay() (00:00:00)
            //   * dataFim    -> atTime(LocalTime.MAX) (23:59:59.999999999)
            //   Assim, o intervalo inclui o dia inteiro.
            LocalDateTime dtMin = (f.dataInicio() != null) ? f.dataInicio().atStartOfDay() : null;
            LocalDateTime dtMax = (f.dataFim() != null) ? f.dataFim().atTime(LocalTime.MAX) : null;

            // Campo correto na entidade: "dataHora"
            range(p, cb, root.get("dataHora"), dtMin, dtMax);

            // Combina todos os predicados com AND
            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // =========================================================================
    // 🔧 Helpers reutilizáveis
    // =========================================================================

    /**
     * Igualdade simples (path = value), somente se value != null.
     */
    private static <T> void eq(List<Predicate> p,
                               jakarta.persistence.criteria.CriteriaBuilder cb,
                               jakarta.persistence.criteria.Path<T> path,
                               T value) {
        if (value != null) {
            p.add(cb.equal(path, value));
        }
    }

    /**
     * Igualdade ignorando maiúsculas/minúsculas para Strings.
     * Usa LOWER(path) = LOWER(value).
     */
    private static void eqIgnoreCase(List<Predicate> p,
                                     jakarta.persistence.criteria.CriteriaBuilder cb,
                                     jakarta.persistence.criteria.Path<String> path,
                                     String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.equal(cb.lower(path), value.toLowerCase()));
        }
    }

    /**
     * LIKE case-insensitive: LOWER(path) LIKE %lower(value)%.
     * Ótimo para buscas parciais em textos.
     */
    private static void like(List<Predicate> p,
                             jakarta.persistence.criteria.CriteriaBuilder cb,
                             jakarta.persistence.criteria.Path<String> path,
                             String value) {
        if (value != null && !value.isBlank()) {
            p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
        }
    }

    /**
     * Intervalo [min, max] para campos comparáveis (>= min e <= max).
     * Adiciona cada lado somente se o valor existir.
     */
    private static <T extends Comparable<? super T>> void range(List<Predicate> p,
                                                                jakarta.persistence.criteria.CriteriaBuilder cb,
                                                                jakarta.persistence.criteria.Path<T> path,
                                                                T min, T max) {
        if (min != null) {
            p.add(cb.greaterThanOrEqualTo(path, min));
        }
        if (max != null) {
            p.add(cb.lessThanOrEqualTo(path, max));
        }
    }
}
