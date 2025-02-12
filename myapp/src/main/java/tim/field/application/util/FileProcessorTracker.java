package tim.field.application.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
public class FileProcessorTracker {

    // Caminhos fixos para arquivos de configuração
    private static final String SS_TRACKER_FILE_PATH = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Tarefas SS\\processed_files_ss.txt";
    private static final String WFM_TRACKER_FILE_PATH = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Tarefas WFM\\processed_files_wfm.txt";
    private static final String HISTORICO_TRACKER_FILE_PATH = "C:\\Users\\t3724475\\Documents\\GitHub\\4Ever\\4Ever (Back-end)\\4Ever-Backend\\myapp\\Imports\\Histórico Tarefas\\processed_files XLSX.txt";


    // Verifica se o arquivo já foi processado para SS
    public boolean isFileProcessedForSS(String filePath) throws IOException {
        return isFileProcessed(filePath, SS_TRACKER_FILE_PATH);
    }

    // Marca o arquivo como processado para SS
    public void markFileAsProcessedForSS(String filePath) throws IOException {
        markFileAsProcessed(filePath, SS_TRACKER_FILE_PATH);
    }

        // Verifica se o arquivo já foi processado para Historico de Tarefas
        public boolean isFileProcessedForHistorico(String filePath) throws IOException {
            return isFileProcessed(filePath, HISTORICO_TRACKER_FILE_PATH);
        }
    
        // Marca o arquivo como processado para Historico de Tarefas
        public void markFileAsProcessedForHistorico(String filePath) throws IOException {
            markFileAsProcessed(filePath, HISTORICO_TRACKER_FILE_PATH);
        }

    // Verifica se o arquivo já foi processado para WFM
    public boolean isFileProcessedForWFM(String filePath) throws IOException {
        return isFileProcessed(filePath, WFM_TRACKER_FILE_PATH);
    }

    // Marca o arquivo como processado para WFM
    public void markFileAsProcessedForWFM(String filePath) throws IOException {
        markFileAsProcessed(filePath, WFM_TRACKER_FILE_PATH);
    }

    // Verifica se o arquivo já foi processado usando um caminho de arquivo específico
    private boolean isFileProcessed(String filePath, String trackerFilePath) throws IOException {
        Set<String> processedFiles = readProcessedFiles(trackerFilePath);
        return processedFiles.contains(filePath);
    }

    // Marca o arquivo como processado usando um caminho de arquivo específico
    private void markFileAsProcessed(String filePath, String trackerFilePath) throws IOException {
        Set<String> processedFiles = readProcessedFiles(trackerFilePath);
        processedFiles.add(filePath);
        writeProcessedFiles(processedFiles, trackerFilePath);
    }

    // Lê a lista de arquivos processados usando um caminho de arquivo específico
    private Set<String> readProcessedFiles(String trackerFilePath) throws IOException {
        Set<String> processedFiles = new HashSet<>();
        if (Files.exists(Paths.get(trackerFilePath))) {
            try (BufferedReader reader = Files.newBufferedReader(Paths.get(trackerFilePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processedFiles.add(line.trim());
                }
            }
        }
        return processedFiles;
    }

    // Escreve a lista de arquivos processados usando um caminho de arquivo específico
    private void writeProcessedFiles(Set<String> processedFiles, String trackerFilePath) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(trackerFilePath))) {
            for (String filePath : processedFiles) {
                writer.write(filePath);
                writer.newLine();
            }
        }
    }
}
