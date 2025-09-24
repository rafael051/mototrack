package br.com.fiap.mototrack.repository;

import br.com.fiap.mototrack.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 📁 Repositório JPA para a entidade Usuario.
 *
 * Permite operações CRUD, suporte a filtros dinâmicos via Specifications
 * e busca por e-mail (case-insensitive).
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    /**
     * 🔍 Busca um usuário pelo e-mail (ignorando maiúsculas/minúsculas).
     *
     * @param email e-mail do usuário
     * @return Optional com o usuário encontrado (se existir)
     */
    Optional<Usuario> findByEmailIgnoreCase(String email);

    /**
     * ✅ Útil para validações/idempotência (opcional).
     */
    boolean existsByEmailIgnoreCase(String email);
}
