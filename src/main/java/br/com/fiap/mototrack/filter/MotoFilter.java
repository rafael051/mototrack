package br.com.fiap.mototrack.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 📄 DTO de filtro para busca de motos.
 *
 * Permite aplicar múltiplos critérios opcionais para buscas dinâmicas e paginadas.
 * Todos os campos são opcionais e podem ser combinados.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public record MotoFilter(

        // 🔑 Identificadores

        /** ID da moto */
        Long id,

        // 📋 Características da moto

        /** Placa da moto */
        String placa,

        /** Modelo da moto */
        String modelo,

        /** Marca da moto */
        String marca,

        /** Status da moto (Disponível, Locada, Manutenção, etc.) */
        String status,

        /** Ano mínimo de fabricação */
        Integer anoMin,

        /** Ano máximo de fabricação */
        Integer anoMax,

        // 🔗 Relação com filial

        /** ID da filial vinculada */
        Long filialId,

        // 📆 Datas e localização (se necessário futuramente)

        /** Data de cadastro mínima */
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataCriacaoInicio,

        /** Data de cadastro máxima */
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataCriacaoFim

) {}
