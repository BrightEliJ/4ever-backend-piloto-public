package tim.field.application.TarefasWFM.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tim.field.application.TarefasWFM.dto.DataTablesResponse;
import tim.field.application.TarefasWFM.dto.TarefaWFMDTO;
import tim.field.application.TarefasWFM.service.TarefaWFMService;
import tim.field.application.util.GlobalResponse;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/tarefasWFM")
public class TarefaWFMController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TarefaWFMController.class);

    @Autowired
    private TarefaWFMService tarefaWFMService;

    /**
     * Endpoint para buscar tarefas com paginação e filtros
     */
    @GetMapping
    public ResponseEntity<GlobalResponse<DataTablesResponse<TarefaWFMDTO>>> getTarefas(
            @RequestParam("draw") int draw,
            @RequestParam("start") int start,
            @RequestParam("length") int length,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            HttpServletRequest request) {
        
        // Ajustar valores nulos ou vazios para evitar problemas na consulta
        startDate = (startDate == null || startDate.isEmpty()) ? null : startDate;
        endDate = (endDate == null || endDate.isEmpty()) ? null : endDate;
        search = (search == null || search.isEmpty()) ? null : search;

        LOGGER.info("Recebendo requisição para buscar tarefas - draw={}, start={}, length={}, search='{}', startDate='{}', endDate='{}'",
                draw, start, length, search, startDate, endDate);

        try {
            // Criar objeto de paginação
            Pageable pageable = PageRequest.of(start / length, length);

            // Buscar tarefas no service
            DataTablesResponse<TarefaWFMDTO> response = tarefaWFMService.getTarefas(draw, start, length, search, startDate, endDate);

            // Criar log da requisição
            setRequestAttributes(request, "TAREFA_WFM", "Tarefas buscadas com sucesso", 
                    HttpStatus.OK.value(), Map.of("total", response.getRecordsTotal()));

            LOGGER.info("Tarefas encontradas: {} registros.", response.getRecordsTotal());

            // Retornar a resposta padronizada com GlobalResponse
            return ResponseEntity.ok(GlobalResponse.success("Tarefas recuperadas com sucesso.", response));

        } catch (Exception e) {
            LOGGER.error("Erro ao buscar tarefas: ", e);

            // Log do erro
            setRequestAttributes(request, "TAREFA_WFM", "Erro ao buscar tarefas", 
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), Map.of("error", e.getMessage()));

            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(GlobalResponse.error("Erro inesperado ao buscar as tarefas.", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        }
    }

    /**
     * Método auxiliar para registrar logs e atributos da requisição
     */
    private void setRequestAttributes(HttpServletRequest request, String module, String message, int status, Object data) {
        request.setAttribute("module", module);
        request.setAttribute("message", message);
        request.setAttribute("status", status);
        request.setAttribute("responseData", data);
    }
}
