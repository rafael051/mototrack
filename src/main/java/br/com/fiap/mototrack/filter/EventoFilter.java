package br.com.fiap.mototrack.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 📄 DTO de filtro para busca de eventos de movimentação.
 *
 * Permite aplicar múltiplos critérios opcionais para consultas de eventos registrados pelas motos.
 * Todos os campos são opcionais e podem ser combinados livremente.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public record EventoFilter(

        // 🔑 Identificadores

        /** ID do evento */
        Long id,

        /** ID da moto vinculada ao evento */
        Long motoId,

        // 🏷️ Características do evento

        /** Tipo do evento (ex: Entrada, Saída, Manutenção) */
        String tipo,

        /** Motivo do evento */
        String motivo,

        /** Localização textual do evento (bairro, pátio, etc.) */
        String localizacao,

        // 📆 Filtros por intervalo de datas

        /** Data mínima do evento */
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicio,

        /** Data máxima do evento */
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFim

) {}
