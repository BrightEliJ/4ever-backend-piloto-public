package tim.field.application.TarefasSS.service;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tim.field.application.TarefasSS.dto.AnotacoesTarefaDTO;
import tim.field.application.TarefasSS.dto.TarefaDTO;
import tim.field.application.TarefasSS.model.Tarefa;
import tim.field.application.TarefasSS.model.TarefaHistorico;
import tim.field.application.TarefasSS.model.TarefaWfm;
import tim.field.application.TarefasSS.repository.AnotacoesTarefaRepository;
import tim.field.application.TarefasSS.repository.TarefaHistoricoRepository;
import tim.field.application.TarefasSS.repository.TarefaRepository;
import tim.field.application.TarefasSS.repository.TarefaWfmRepository;
import tim.field.application.util.ExcelReaderHistorico;
import tim.field.application.util.ExcelReaderSs;
import tim.field.application.util.ExcelReaderWfm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TarefaService {

    private static final Logger LOGGER = Logger.getLogger(TarefaService.class.getName());

    @Autowired
    private TarefaRepository tarefaRepository;
    
    @Autowired
    private AnotacoesTarefaRepository anotacoesTarefaRepository;

    @Autowired
    private TarefaHistoricoRepository tarefaHistoricoRepository;

    @Autowired
    private TarefaWfmRepository tarefaWfmRepository;

    @Autowired
    private ExcelReaderSs excelReaderSs;

    @Autowired
    private ExcelReaderHistorico excelReaderHistorico;

    @Autowired
    private ExcelReaderWfm excelReaderWfm;

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

// Método para buscar a tarefa e montar o JSON
public Map<String, Object> getTarefaDetalhes(String task) {

    // Busca a tarefa usando o método existente
    Tarefa tarefa = tarefaRepository.findByTask(task);

    if (tarefa == null) {
        return null;  // Ou lançar uma exceção personalizada
    }

    // Monta o JSON com apenas os campos necessários
    Map<String, Object> tarefaDetalhes = new HashMap<>();
    tarefaDetalhes.put("status", tarefa.getStatus());
    tarefaDetalhes.put("evento", tarefa.getEvento());

    // Verificação se as datas são nulas antes de formatar
    tarefaDetalhes.put("dataCriacao", tarefa.getDataCriacao() != null ? dateFormat.format(tarefa.getDataCriacao()) : null);
    tarefaDetalhes.put("dataFechamento", tarefa.getDataFechamento() != null ? dateFormat.format(tarefa.getDataFechamento()) : null);
    tarefaDetalhes.put("eventoMassivo", tarefa.getEventoMassivo());
    tarefaDetalhes.put("tipoMassiva", tarefa.getTipoMassiva());
    tarefaDetalhes.put("alarmeNormalizado", tarefa.getAlarmeNormalizado());
    tarefaDetalhes.put("grupoAcionado", tarefa.getGrupoAcionado());
    tarefaDetalhes.put("grupoCriador", tarefa.getGrupoCriador());
    tarefaDetalhes.put("notaTramitacao", tarefa.getNotaTramitacao());
    tarefaDetalhes.put("notaSuspensao", tarefa.getNotaSuspensao());
    tarefaDetalhes.put("notaCancelamento", tarefa.getNotaCancelamento());
    tarefaDetalhes.put("previsaoNormalizacao", tarefa.getPrevisaoNormalizacao() != null ? dateFormat.format(tarefa.getPrevisaoNormalizacao()) : null);
    tarefaDetalhes.put("sla", tarefa.getSla() != null ? dateFormat.format(tarefa.getSla()) : null);
    tarefaDetalhes.put("notDone", tarefa.getNotDone());
    tarefaDetalhes.put("atualizadoEm", tarefa.getAtualizadoEm() != null ? dateFormat.format(tarefa.getAtualizadoEm()) : null);
    tarefaDetalhes.put("atualizadoPor", tarefa.getAtualizadoPor());
    tarefaDetalhes.put("sitesDependentesTotalTx", tarefa.getSitesDependentesTotalTx());
    tarefaDetalhes.put("descricaoSolucao", tarefa.getDescricaoSolucao());
    tarefaDetalhes.put("anotacoesFechamento", tarefa.getAnotacoesFechamento());
    tarefaDetalhes.put("nomeTecnicoCampo", tarefa.getNomeTecnicoCampo());
    tarefaDetalhes.put("telefoneTecnico", tarefa.getTelefoneTecnico());

    // Calcule a porcentagem do tempo decorrido em relação ao SLA se as datas não forem nulas
    if (tarefa.getDataCriacao() != null && tarefa.getSla() != null) {
        LocalDateTime dataCriacao = tarefa.getDataCriacao().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime slaData = tarefa.getSla().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();

        Duration totalDuration = Duration.between(dataCriacao, slaData);
        Duration elapsedDuration = Duration.between(dataCriacao, now);

        long percentage = 0;
        boolean slaSituation = false;

        if (totalDuration.toMinutes() > 0) {
            // Calcula a porcentagem do tempo decorrido
            percentage = (long) ((double) elapsedDuration.toMinutes() / totalDuration.toMinutes() * 100);
            if (percentage > 100) {
                // Se o tempo decorrido for maior que o SLA e o status for concluído
                if ("concluído".equalsIgnoreCase(tarefa.getStatus())) {
                    percentage = 100;
                    slaSituation = true;
                } else {
                    slaSituation = false;
                }
            } else {
                slaSituation = false;
            }
        }

        tarefaDetalhes.put("percentualSla", percentage);
        tarefaDetalhes.put("slaSituation", slaSituation);
    } else {
        // Se qualquer uma das datas for nula, define percentualSla como 50
        tarefaDetalhes.put("percentualSla", 50L);
        tarefaDetalhes.put("slaSituation", false);
    }

    return tarefaDetalhes;
}

    @Transactional
    public void importFileToSs(String filePath) {
        try {
            List<Tarefa> tarefas = excelReaderSs.readDirectoryForTarefasSs(filePath);
            if (tarefas != null && !tarefas.isEmpty()) {
                tarefaRepository.saveAll(tarefas);
                LOGGER.info("Importacao de dados concluida com sucesso.");
            } else {
                LOGGER.warning("Nenhum dado encontrado no arquivo.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao importar dados do arquivo.", e);
        }
    }

    @Transactional
    public void importFileToHistorico(String filePath) {
        try {
            List<TarefaHistorico> historicos = excelReaderHistorico.readDirectoryForTarefasHistorico(filePath);
            if (historicos != null && !historicos.isEmpty()) {
                tarefaHistoricoRepository.saveAll(historicos);
                LOGGER.info("Importacao de dados concluida com sucesso.");
            } else {
                LOGGER.warning("Nenhum dado encontrado no arquivo.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao importar dados do arquivo.", e);
        }
    }

    @Transactional
    public void importFileToWfm(String filePath) {
        try {
            List<TarefaWfm> tarefas = excelReaderWfm.readDirectoryForTarefasWfm(filePath);
            if (tarefas != null && !tarefas.isEmpty()) {
                tarefaWfmRepository.saveAll(tarefas);
                LOGGER.info("Importacao de dados concluida com sucesso.");
            } else {
                LOGGER.warning("Nenhum dado encontrado no arquivo.");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erro ao importar dados do arquivo.", e);
        }
    }

    public List<Tarefa> getAllTarefas() {
        return tarefaRepository.findAll();
    }

    // Método atualizado para buscar a tarefa diretamente
    public Tarefa getTarefaByTask(String task) {
        return tarefaRepository.findByTask(task);
    }

    public Page<TarefaDTO> getAllTarefaFields(Pageable pageable, String searchValue, String startDate, String endDate) {
        Page<Object[]> results;

        if (startDate != null && endDate != null){
            // Chama o método que lida com a pesquisa e filtros de data
            results = tarefaRepository.findAllTarefaFieldsWithFilters(pageable, searchValue, startDate, endDate);
            }
        else if (startDate == null && endDate == null && searchValue != null){
            // Chama o método que lida com a pesquisa e filtros de data
            results = tarefaRepository.findAllTarefaFieldsWithSearch(pageable, searchValue);            
        }
        else {
            // Chama o método que retorna os valores sem filtragem
            results = tarefaRepository.findAllTarefaFields(pageable);
        }
        
        // Mapeia o resultado para o DTO
        return results.map(result -> new TarefaDTO(
                (String) result[0],
                (String) result[1],
                (String) result[2],
                result[3] != null ? ((Number) result[3]).longValue() : 0L,
                (Date) result[4],
                (String) result[5]
        ));
    }

    public List<AnotacoesTarefaDTO> findAllAnotacoes(String task) {
        return anotacoesTarefaRepository.findAnotacoesByTask(task);
    }
    
}
