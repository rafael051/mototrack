package br.com.fiap.mototrack.filter;

/**
 * 📄 DTO de filtro para busca de usuários.
 *
 * Permite aplicar filtros opcionais por nome, e-mail e perfil do usuário.
 * Pode ser usado em buscas dinâmicas com paginação e ordenação.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public record UsuarioFilter(

        // 🔑 Identificadores

        /** ID do usuário */
        Long id,

        // 🧑 Dados pessoais

        /** Nome do usuário */
        String nome,

        /** E-mail do usuário */
        String email,

        /** Perfil de acesso (ex: ADMINISTRADOR, GESTOR, OPERADOR) */
        String perfil

) {}
