package br.com.fiap.mototrack.repository;


import br.com.fiap.mototrack.model.Filial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * 📁 Repositório JPA para a entidade Filial.
 */
@Repository
public interface FilialRepository extends JpaRepository<Filial, Long>, JpaSpecificationExecutor<Filial> {
}