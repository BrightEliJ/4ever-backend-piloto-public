package tim.field.application.TarefasSS.service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import java.util.logging.Logger;
import tim.field.application.TarefasSS.dto.TMRDataDTO;
import tim.field.application.TarefasSS.repository.TMRDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TMRDataService {

	private static final Logger LOGGER = Logger.getLogger(TMRDataService.class.getName());


    @Autowired
    private TMRDataRepository tmrDataRepository;
	
	    public String getCalculatedTMRByTask(String task){
        LOGGER.info("Iniciando cálculo de TMR para a tarefa: " + task);
        String calculatedValue = tmrDataRepository.findByTask(task);

        if (calculatedValue == null) {
            LOGGER.warning("Valor calculado retornado como null para a tarefa: " + task);
        } else {
            LOGGER.info("Valor calculado retornado para a tarefa: " + calculatedValue);
        }

        return removeLeadingZeros(calculatedValue);
    }

    // Retorna dois mapas: tmrByDay e tmrByDayAndTask
    public Map<String, Object> getTmrByDayAndTask(String grupoAcionado, List<String> statusList, Date startDate, Date endDate) {

        // Executa a consulta SQL e retorna os resultados
        List<Object[]> result = tmrDataRepository.findByFiltersWithFunction(grupoAcionado, statusList, startDate, endDate);

        if (result == null || result.isEmpty()) {
            throw new RuntimeException("Nenhum resultado encontrado para os parâmetros fornecidos.");
        }

        // Lista para armazenar os dados de TMR
        List<TMRDataDTO> tarefas = result.stream()
            .map(row -> new TMRDataDTO(
                (Long) row[0],                 // id
                (String) row[1],               // task
                (String) row[2],               // calculatedValue
                (String) row[3],               // grupo
                (String) row[4],               // status
                (Date) row[5],                 // dataCriacao
                (Date) row[6]                  // dataFechamento
            ))
            .collect(Collectors.toList());

        // Mapear para tmrByDay e calcular a média de TMR por dia
        Map<String, String> tmrByDay = tarefas.stream()
            .filter(tmrData -> tmrData.getDataCriacao() != null)
            .collect(Collectors.groupingBy(
                tmrData -> formatDate(tmrData.getDataCriacao()),
                Collectors.mapping(TMRDataDTO::getCalculatedValue, Collectors.toList())
            ))
            .entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> calculateAverageTime(entry.getValue())  // Calcular a média de TMR para cada dia
            ));

        // Mapear para tmrByDayAndTask
        Map<String, Map<String, String>> tmrByDayAndTask = tarefas.stream()
        .filter(tmrData -> tmrData.getDataCriacao() != null)
        .collect(Collectors.groupingBy(
            tmrData -> formatDate(tmrData.getDataCriacao()),
            Collectors.toMap(
                TMRDataDTO::getTask,
                tmrData -> convertSecondsToHoursMinutes(tmrData.getCalculatedValue()),  // Converter segundos diretamente para hh.mm
                (existing, replacement) -> existing
            )
        ));

        // Retornar os dois mapas
        return Map.of(
            "tmrByDay", tmrByDay,
            "tmrByDayAndTask", tmrByDayAndTask
        );
    }

    // Método auxiliar para formatar a data
    private String formatDate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(date);
    }

    // Método para converter segundos para o formato hh.mm
    private String convertSecondsToHoursMinutes(String secondsStr) {
        try {
            int totalSeconds = Integer.parseInt(secondsStr);  // Converter de String para inteiro
            int hours = totalSeconds / 3600;  // Calcular horas
            int minutes = (totalSeconds % 3600) / 60;  // Calcular minutos

            // Retornar no formato hh.mm
            return String.format("%02d.%02d", hours, minutes);
        } catch (NumberFormatException e) {
            // Log para tratamento de erro e retorno padrão em caso de falha
            System.out.println("Erro ao converter segundos: " + secondsStr);
            return "00.00";  // Retorno padrão em caso de erro
        }
    }

    // Método que calcula a média de TMR formatado para cada dia
    private String calculateAverageTime(List<String> timeValues) {
        int totalSeconds = 0;
        int count = timeValues.size();
    
        // Somar todos os valores de segundos
        for (String time : timeValues) {
            try {
                // Converter o valor de segundos de String para inteiro
                int seconds = Integer.parseInt(time);
                totalSeconds += seconds;
            } catch (NumberFormatException e) {
                // Log de erro caso algum valor não seja um número válido
                System.out.println("Erro ao converter segundos para números: " + time);
            }
        }
    
        // Se não houver valores válidos, retorna "00.00"
        if (count == 0) return "00.00";
    
        // Calcular a média de segundos
        int averageSeconds = totalSeconds / count;
        int hours = averageSeconds / 3600;
        int minutes = (averageSeconds % 3600) / 60;
    
        // Retornar no formato hh.mm
        return String.format("%02d.%02d", hours, minutes);
    }

    public static String removeLeadingZeros(String str) {
        // Verifica se a string não é nula ou vazia
        if (str != null && !str.isEmpty()) {
            // Verifica se os dois primeiros caracteres são '0'
            if (str.startsWith("00")) {
                // Retorna a string sem os dois primeiros caracteres
                return str.substring(2);
            } else if (str.charAt(0) == '0') {
                // Retorna a string sem o primeiro caractere se apenas o primeiro for '0'
                return str.substring(1);
            }
        }
        // Se não houver '0' no início, retorna a string original
        return str;
    }
}
