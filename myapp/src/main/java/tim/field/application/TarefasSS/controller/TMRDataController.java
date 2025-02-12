package tim.field.application.TarefasSS.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tim.field.application.TarefasSS.dto.TmrRequest;
import tim.field.application.TarefasSS.service.TMRDataService;

@RestController
@RequestMapping("/api/user/tmrdata")
public class TMRDataController {

    @Autowired
    private TMRDataService tmrDataService;

    @GetMapping("/by-task")
    public String getCalculatedTMRByTask(
            @RequestParam("task") String task) {

        // Chama o service passando a task e retorna o resultado
        return tmrDataService.getCalculatedTMRByTask(task);
    }

    @PostMapping("/by-day")
    public Map<String, Object> getTmrByDay(
            @RequestBody TmrRequest request) {

        // Chama o service passando os parâmetros do objeto request e retorna o resultado
        return tmrDataService.getTmrByDayAndTask(
            request.getGrupoAcionado(),
            request.getStatusList(),
            request.getStartDate(),
            request.getEndDate());
    }
}
