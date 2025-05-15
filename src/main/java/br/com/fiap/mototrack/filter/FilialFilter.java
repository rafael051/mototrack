package br.com.fiap.mototrack.filter;

/**
 * 📄 DTO de filtro para busca de filiais da Mottu.
 *
 * Permite aplicar critérios opcionais para listar filiais por nome, cidade, estado e outras informações geográficas.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public record FilialFilter(

        // 🔑 Identificador

        /** ID da filial */
        Long id,

        // 📋 Informações da filial

        /** Nome da filial */
        String nome,

        /** Bairro da filial */
        String bairro,

        /** Cidade da filial */
        String cidade,

        /** Estado da filial (ex: SP) */
        String estado,

        /** CEP da filial */
        String cep

) {}
