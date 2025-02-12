package tim.field.application.TarefasWFM.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.JpaSort;
import org.springframework.stereotype.Service;
import tim.field.application.TarefasWFM.dto.DataTablesResponse;
import tim.field.application.TarefasWFM.dto.TarefaWFMDTO;
import tim.field.application.TarefasWFM.repository.BacklogActivitiesHistoricRepository;
import tim.field.application.TarefasWFM.repository.BacklogActivitiesRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TarefaWFMService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TarefaWFMService.class);

    @Autowired
    private BacklogActivitiesRepository backlogActivitiesRepository;

    @Autowired
    private BacklogActivitiesHistoricRepository backlogActivitiesHistoricRepository;

    public DataTablesResponse<TarefaWFMDTO> getTarefas(int draw, int start, int length, String search, String startDate, String endDate) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);

        search = (search == null || search.isEmpty()) ? null : search;
        startDate = (startDate == null || startDate.isEmpty()) ? null : startDate;
        endDate = (endDate == null || endDate.isEmpty()) ? null : endDate;

        LOGGER.info("Recebendo requisição - draw={}, start={}, length={}, search='{}', startDate='{}', endDate='{}'",
                draw, start, length, search, startDate, endDate);

        // 📌 Se nenhuma data foi fornecida e o search está vazio, busca o mês atual (incluindo hoje)
        if (search == null && startDate == null && endDate == null) {
            startDate = firstDayOfMonth.toString();
            endDate = today.toString();
        }

        // 📌 Contagem total de registros por tabela
        long totalHistoric = backlogActivitiesHistoricRepository.countTarefas(search, startDate, endDate);
        long totalCurrent = (endDate == null || LocalDate.parse(endDate).equals(today)) 
                ? backlogActivitiesRepository.countTarefas(search, startDate, endDate) 
                : 0;

        long totalRecords = totalHistoric + totalCurrent;
        LOGGER.info("Total histórico: {}, Total atual: {}, Total geral: {}", totalHistoric, totalCurrent, totalRecords);

        // ✅ Se `start` for maior que o total de registros encontrados, ajustamos para `0`
        if (start >= totalRecords) {
            LOGGER.warn("⚠️ Start ({}) é maior que o total de registros ({})! Ajustando para 0.", start, totalRecords);
            start = 0;
        }

        List<TarefaWFMDTO> tarefas = new ArrayList<>();

        if (start < totalHistoric) {
            // 🔍 Começa buscando na tabela histórica
            int fetchFromHistoric = Math.min(length, (int) (totalHistoric - start));
            int fetchFromCurrent = length - fetchFromHistoric;

            LOGGER.info("Buscando {} registros do histórico e {} registros da tabela atual", fetchFromHistoric, fetchFromCurrent);

            Pageable pageableHistoric = PageRequest.of(start / length, fetchFromHistoric, JpaSort.unsafe("\"XA_PI_CREATE_DATE\"").descending());
            Page<Object[]> historicResults = backlogActivitiesHistoricRepository.findTarefas(pageableHistoric, search, startDate, endDate);
            tarefas.addAll(historicResults.map(this::mapToTarefaWFMDTO).getContent());

            if (fetchFromCurrent > 0 && totalCurrent > 0) {
                Pageable pageableCurrent = PageRequest.of(0, fetchFromCurrent, JpaSort.unsafe("\"XA_PI_CREATE_DATE\"").descending());
                Page<Object[]> currentResults = backlogActivitiesRepository.findTarefas(pageableCurrent, search, startDate, endDate);
                tarefas.addAll(currentResults.map(this::mapToTarefaWFMDTO).getContent());
            }

        } else {
            // 🔍 Busca apenas na tabela atual
            int adjustedStart = (int) (start - totalHistoric);
            Pageable pageableCurrent = PageRequest.of(adjustedStart / length, length, JpaSort.unsafe("\"XA_PI_CREATE_DATE\"").descending());
            Page<Object[]> currentResults = backlogActivitiesRepository.findTarefas(pageableCurrent, search, startDate, endDate);
            tarefas.addAll(currentResults.map(this::mapToTarefaWFMDTO).getContent());
        }

        LOGGER.info("Repositório retornou {} resultados após paginação.", tarefas.size());

        return new DataTablesResponse<>(draw, (int) totalRecords, (int) totalRecords, tarefas);
    }

    private TarefaWFMDTO mapToTarefaWFMDTO(Object[] result) {
        if (result == null || result.length < 8) {
            LOGGER.warn("Resultado inesperado da consulta: {}", result);
            return null;
        }
        return new TarefaWFMDTO(
                result[0] != null ? result[0].toString() : "",
                result[1] != null ? result[1].toString() : "",
                convertToLocalDateTime(result[2]),
                result[3] != null ? result[3].toString() : "",
                result[4] != null ? result[4].toString() : "",
                result[5] != null ? result[5].toString() : "",
                result[6] != null ? result[6].toString() : "",
                result[7] != null ? result[7].toString() : ""
        );
    }

    private LocalDateTime convertToLocalDateTime(Object dateObj) {
        if (dateObj instanceof LocalDateTime) {
            return (LocalDateTime) dateObj;
        }
        return null;
    }
}