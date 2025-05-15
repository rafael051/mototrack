package br.com.fiap.mototrack.specification;

import br.com.fiap.mototrack.filter.MotoFilter;
import br.com.fiap.mototrack.model.Moto;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * ## 🔍 Specification: MotoSpecification
 *
 * Realiza filtros dinâmicos para a entidade `Moto`, aplicando os critérios enviados em `MotoFilter`.
 *
 * Suporta:
 * - 🔑 Filtros básicos: ID, placa, modelo, marca, status
 * - 📅 Ano mínimo/máximo e data de criação
 * - 🔗 Filial vinculada
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public class MotoSpecification {

    public static Specification<Moto> comFiltros(MotoFilter f) {
        return (root, query, cb) -> {
            List<Predicate> p = new ArrayList<>();

            // 🔑 Básico
            eq(p, cb, root.get("id"), f.id());
            like(p, cb, root.get("placa"), f.placa());
            like(p, cb, root.get("modelo"), f.modelo());
            like(p, cb, root.get("marca"), f.marca());
            eqIgnoreCase(p, cb, root.get("status"), f.status());

            // 📅 Ano e criação
            range(p, cb, root.get("ano"), f.anoMin(), f.anoMax());
            range(p, cb, root.get("dataCriacao"), f.dataCriacaoInicio(), f.dataCriacaoFim());

            // 🔗 Filial
            if (f.filialId() != null) p.add(cb.equal(root.get("filial").get("id"), f.filialId()));

            return cb.and(p.toArray(new Predicate[0]));
        };
    }

    // ===============================
    // 🔧 Métodos utilitários
    // ===============================

    /** Igualdade simples */
    private static <T> void eq(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb, jakarta.persistence.criteria.Path<T> path, T value) {
        if (value != null) p.add(cb.equal(path, value));
    }

    /** Igualdade ignorando case */
    private static void eqIgnoreCase(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb, jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) p.add(cb.equal(cb.lower(path), value.toLowerCase()));
    }

    /** LIKE com case-insensitive */
    private static void like(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb, jakarta.persistence.criteria.Path<String> path, String value) {
        if (value != null && !value.isBlank()) p.add(cb.like(cb.lower(path), "%" + value.toLowerCase() + "%"));
    }

    /** Faixa entre dois valores */
    private static <T extends Comparable<? super T>> void range(List<Predicate> p, jakarta.persistence.criteria.CriteriaBuilder cb, jakarta.persistence.criteria.Path<T> path, T min, T max) {
        if (min != null) p.add(cb.greaterThanOrEqualTo(path, min));
        if (max != null) p.add(cb.lessThanOrEqualTo(path, max));
    }
}
