package tim.field.application.TarefasSS.controller;

import tim.field.application.TarefasSS.service.CsvToXlsxService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/csv-to-xlsx")
public class CsvToXlsxController {

    private final CsvToXlsxService csvToXlsxService;

    // Defina os caminhos diretamente no código
    private static final String CSV_DIRECTORY_PATH = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Histórico Tarefas\\CSV";
    private static final String XLSX_DIRECTORY_PATH = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Histórico Tarefas\\XLSX";
    private static final String PROCESSED_FILES_PATH = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Histórico Tarefas\\processed_files (CSV).txt";

    public CsvToXlsxController(CsvToXlsxService csvToXlsxService) {
        this.csvToXlsxService = csvToXlsxService;
    }

    @GetMapping("/convert")
    public String convertCsvToXlsx() {
        try {
            csvToXlsxService.convertAllCsvToXlsx(CSV_DIRECTORY_PATH, XLSX_DIRECTORY_PATH, PROCESSED_FILES_PATH);
            return "Arquivo csv convertido com sucesso";
        } catch (IOException e) {
            e.printStackTrace();
            return "Ocorreu um erro ao converter os arquivos.";
        }
    }
}
