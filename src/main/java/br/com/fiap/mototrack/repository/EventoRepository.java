package br.com.fiap.mototrack.repository;

import br.com.fiap.mototrack.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 📁 Repositório JPA para a entidade Evento.
 *
 * Permite operações CRUD e suporte a Specifications para consultas dinâmicas com filtros.
 */
@Repository
public interface EventoRepository extends JpaRepository<Evento, Long>, JpaSpecificationExecutor<Evento> {
}
