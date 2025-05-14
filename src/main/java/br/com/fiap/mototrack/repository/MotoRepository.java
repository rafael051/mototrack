package br.com.fiap.mototrack.repository;

import br.com.fiap.mototrack.model.Moto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 📁 Repositório JPA para a entidade Moto.
 * Permite operações CRUD e suporte a Specifications para filtros dinâmicos.
 */
@Repository
public interface MotoRepository extends JpaRepository<Moto, Long>, JpaSpecificationExecutor<Moto> {
}