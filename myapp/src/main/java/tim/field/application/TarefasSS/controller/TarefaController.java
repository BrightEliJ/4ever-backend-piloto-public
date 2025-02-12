package tim.field.application.TarefasSS.controller;

import java.util.List;
import java.util.Map;

import tim.field.application.TarefasSS.dto.AnotacoesTarefaDTO;
import tim.field.application.TarefasSS.dto.DataTablesResponse;
import tim.field.application.TarefasSS.dto.TarefaDTO;
import tim.field.application.TarefasSS.model.Tarefa;
import tim.field.application.TarefasSS.service.TarefaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user/tarefas")
public class TarefaController {

    @Autowired
    private TarefaService tarefaService;

        // Endpoint para obter detalhes da tarefa por task ID
    @GetMapping("/detalhes-tarefa/{task}")
    public ResponseEntity<Object> getTarefaDetalhes(@PathVariable String task) {
        Map<String, Object> tarefaDetalhes = tarefaService.getTarefaDetalhes(task);
        
        if (tarefaDetalhes == null) {
            // Retorna mensagem personalizada se a tarefa não for encontrada
            return ResponseEntity.status(404).body("Informações não encontradas, verifique o código da Tarefa.");
        }

        return ResponseEntity.ok(tarefaDetalhes);
    }

    @GetMapping
    public List<Tarefa> getAllTarefas() {
        return tarefaService.getAllTarefas();
    }

    @GetMapping("/campos-lista")
    public DataTablesResponse<TarefaDTO> getAllTarefaFields(
            @RequestParam("draw") int draw,
            @RequestParam("start") int start,
            @RequestParam("length") int length,
            @RequestParam("search[value]") String searchValue,
            @RequestParam("order[0][column]") int sortColumnIndex,
            @RequestParam("order[0][dir]") String sortDirection,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate) {
    
        // Mapeamento de colunas para campos de DTO
        String[] columnNames = {"task", "grupoAcionado", "status", "downtime", "dataCriacao", "eventoMassivo"};
        String sortColumn = columnNames[sortColumnIndex];
    
        // Ajusta os valores das datas, se estiverem presentes
        String adjustedStartDate = (startDate != null && !startDate.isEmpty()) ? startDate : null;
        String adjustedEndDate = (endDate != null && !endDate.isEmpty()) ? endDate : null;
    
        // Configura a paginação e ordenação
        Pageable pageable = PageRequest.of(start / length, length,
                sortDirection.equals("asc") ? Sort.by(sortColumn).ascending() : Sort.by(sortColumn).descending());
    
        // Consulta o serviço para obter os dados filtrados
        Page<TarefaDTO> page = tarefaService.getAllTarefaFields(pageable, searchValue, adjustedStartDate, adjustedEndDate);
    
        // Cria e retorna a resposta para o DataTables
        return new DataTablesResponse<>(
                draw,
                page.getTotalElements(),
                page.getTotalElements(),
                page.getContent()
        );
    }
    
    @GetMapping("/{task}")
    public Tarefa getTarefaByTask(@PathVariable String task) {
        return tarefaService.getTarefaByTask(task);
    }

    @GetMapping("/anotacoes")
    public ResponseEntity<List<AnotacoesTarefaDTO>> searchAnotacoes(@RequestParam String task) {
        List<AnotacoesTarefaDTO> anotacoes = tarefaService.findAllAnotacoes(task);
        return ResponseEntity.ok(anotacoes);
    }

}
