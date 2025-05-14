package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.model.Moto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * 📄 Specification dinâmica para consulta avançada de Motos.
 *
 * Permite filtros combinados por placa, modelo, status, ano, filial, data de criação etc.
 * Alinhado com os campos disponíveis em MotoFilter.
 *
 * ---
 * @author Rafael e Lucas
 * @since 1.0
 */
public class MotoSpecification {

    public static Specification<Moto> comFiltros(MotoFilter filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 🔑 Filtros básicos
            if (filtro.id() != null) {
                predicates.add(cb.equal(root.get("id"), filtro.id()));
            }

            if (filtro.placa() != null && !filtro.placa().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("placa")), "%" + filtro.placa().toLowerCase() + "%"));
            }

            if (filtro.modelo() != null && !filtro.modelo().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("modelo")), "%" + filtro.modelo().toLowerCase() + "%"));
            }

            if (filtro.marca() != null && !filtro.marca().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("marca")), "%" + filtro.marca().toLowerCase() + "%"));
            }

            if (filtro.status() != null && !filtro.status().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("status")), filtro.status().toLowerCase()));
            }

            // 📆 Ano de fabricação
            if (filtro.anoMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ano"), filtro.anoMin()));
            }
            if (filtro.anoMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ano"), filtro.anoMax()));
            }

            // 🔗 Filial
            if (filtro.filialId() != null) {
                predicates.add(cb.equal(root.get("filial").get("id"), filtro.filialId()));
            }

            // 📅 Data de criação (opcional)
            if (filtro.dataCriacaoInicio() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataCriacao"), filtro.dataCriacaoInicio()));
            }
            if (filtro.dataCriacaoFim() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataCriacao"), filtro.dataCriacaoFim()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
