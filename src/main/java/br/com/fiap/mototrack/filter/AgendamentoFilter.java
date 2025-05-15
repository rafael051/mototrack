package br.com.fiap.mototrack.filter;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 📄 DTO de filtro para busca de agendamentos.
 *
 * Permite aplicar múltiplos critérios opcionais para consultas dinâmicas de agendamentos.
 * Todos os campos são opcionais e podem ser combinados.
 *
 * ---
 * @author Rafael
 * @since 1.0
 */
public record AgendamentoFilter(

        // 🔑 Identificadores

        /** ID do agendamento */
        Long id,

        /** ID da moto vinculada ao agendamento */
        Long motoId,

        // 📝 Descrição

        /** Texto parcial ou completo da descrição */
        String descricao,

        // 📆 Intervalo de datas agendadas

        /** Data mínima do agendamento */
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataInicio,

        /** Data máxima do agendamento */
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dataFim

) {}
