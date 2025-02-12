package tim.field.application.TarefasSS.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tim.field.application.TarefasSS.service.TarefaService;

@RestController
@RequestMapping("/api/dados")
public class ImportController {

    @Autowired
    private TarefaService tarefaService;

    @PostMapping("/import-ss")
    public void importFilesFromSsFolder() {
        // Caminho fixo da pasta
        String folderPath = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\\\myapp\\Imports\\Tarefas SS\\Processar Excel";
        
        // Chama o serviço para processar todos os arquivos da pasta
        tarefaService.importFileToSs(folderPath);
    }

    @PostMapping("/import-historico")
    public void importFilesFromHistoricoFolder() {
        // Caminho fixo da pasta
        String folderPath = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Histórico Tarefas\\XLSX";
        
        // Chama o serviço para processar todos os arquivos da pasta
        tarefaService.importFileToHistorico(folderPath);
    }

    @PostMapping("/import-wfm")
    public void importFilesFromWfmFolder() {
        // Caminho fixo da pasta
        String folderPath = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Tarefas WFM\\Processar Excel";
        
        // Chama o serviço para processar todos os arquivos da pasta
        tarefaService.importFileToWfm(folderPath);
    }
}
