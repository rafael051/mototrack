package br.com.fiap.mototrack.repository;

import br.com.fiap.mototrack.model.Agendamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 📁 Repositório JPA para a entidade Agendamento.
 *
 * Permite operações CRUD e suporte a Specifications para consultas dinâmicas com filtros.
 */
@Repository
public interface AgendamentoRepository extends JpaRepository<Agendamento, Long>, JpaSpecificationExecutor<Agendamento> {
}
