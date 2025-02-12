package tim.field.application.TarefasSS.service;

import com.opencsv.CSVReader;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import com.opencsv.exceptions.CsvValidationException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.io.*;
import java.text.Normalizer;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class CsvToXlsxService {

    private static final Logger logger = Logger.getLogger(CsvToXlsxService.class.getName());

    public void convertAllCsvToXlsx(String csvDirectoryPath, String xlsxDirectoryPath, String processedFilesPath) throws IOException {
        Set<String> processedFiles = loadProcessedFiles(processedFilesPath);

        Path csvDirectory = Paths.get(csvDirectoryPath);

        // Check if the directory exists and is a directory
        if (!Files.exists(csvDirectory) || !Files.isDirectory(csvDirectory)) {
            logger.severe("Diretorio csv nao existe: " + csvDirectoryPath);
            throw new IOException("Diretorio csv invalido: " + csvDirectoryPath);
        }

        // List CSV files in the directory
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(csvDirectory, "*.csv")) {
            for (Path csvFile : directoryStream) {
                String fileName = csvFile.getFileName().toString();

                if (processedFiles.contains(fileName)) {
                    logger.info("O arquivo ja foi processado: " + fileName);
                    continue;
                }

                String baseFileName = "u_historico_status_tarefa";
                String extension = ".xlsx";
                String xlsxFilePath = getUniqueFilePath(xlsxDirectoryPath, baseFileName, extension);

                try {
                    convertCsvToXlsx(csvFile.toString(), xlsxFilePath);
                    recordProcessedFile(processedFilesPath, fileName);
                } catch (IOException | CsvValidationException e) {
                    logger.severe("Falha ao processar o arquivo: " + fileName + ". Erro: " + e.getMessage());
                }
            }
        }
    }

    private Set<String> loadProcessedFiles(String processedFilesPath) throws IOException {
        Set<String> processedFiles = new HashSet<>();
        Path filePath = Paths.get(processedFilesPath);

        if (Files.exists(filePath)) {
            try (BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)) {
                String line;
                while ((line = reader.readLine()) != null) {
                    processedFiles.add(line.trim());
                }
            }
        }

        return processedFiles;
    }

    private void recordProcessedFile(String processedFilesPath, String fileName) throws IOException {
        Path filePath = Paths.get(processedFilesPath);
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND)) {
            writer.write(fileName);
            writer.newLine();
        }
    }

    private String getUniqueFilePath(String directoryPath, String baseFileName, String extension) {
        Path directory = Paths.get(directoryPath);
        String filePath;
        int count = 1;

        do {
            filePath = directoryPath + File.separator + baseFileName + " (" + count + ")" + extension;
            count++;
        } while (Files.exists(Paths.get(filePath)));

        return filePath;
    }

    private void convertCsvToXlsx(String csvFilePath, String xlsxFilePath) throws IOException, CsvValidationException {
        try (BOMInputStream bomInputStream = new BOMInputStream(new FileInputStream(csvFilePath));
             InputStreamReader isr = new InputStreamReader(bomInputStream, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(isr);
             XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(xlsxFilePath)) {

            Sheet sheet = workbook.createSheet("Sheet1");
            String[] nextLine;
            int rowNum = 0;

            while ((nextLine = csvReader.readNext()) != null) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < nextLine.length; i++) {
                    Cell cell = row.createCell(i);
                    // Normalize and adjust cell values
                    String normalizedValue = Normalizer.normalize(nextLine[i], Normalizer.Form.NFC);
                    String adjustedValue = adjustCellValue(normalizedValue, i);
                    cell.setCellValue(adjustedValue);
                }
            }

            workbook.write(fileOut);
            logger.info("Arquivo xlsx criado com sucesso: " + xlsxFilePath);
        }
    }

    private String adjustCellValue(String value, int columnIndex) {
        // Trimming leading and trailing whitespace
        value = value.trim();
    
        // Normalize the value
        String normalizedValue = Normalizer.normalize(value, Normalizer.Form.NFC);
    
        // Adjust the value based on column index
        switch (columnIndex) {
            case 4: // Assuming u_acao is the fifth column (index 4)
            if (normalizedValue.contains("Trabalho")) {
                // Replace the entire content with "Anotações de Trabalho" if it contains "Trabalho"
                normalizedValue = "Anotações de Trabalho";
            } else if (normalizedValue.contains("Status")) {
                // Replace the entire content with "Alteração de Status" if it contains "Status"
                normalizedValue = "Alteração de Status";
            }
                break;
            case 5: // Assuming u_status is the sixth column (index 5)
                // Replace "N" followed by text with specific status
                if (normalizedValue.startsWith("N")) {
                    if (normalizedValue.contains("Conclu")) {
                        normalizedValue = "Não Concluído";
                    } else if (normalizedValue.contains("Iniciado")) {
                        normalizedValue = "Não Iniciado";
                    }
                }
                break;
            default:
                // Other columns can be handled here if needed
                break;
        }
    
        return normalizedValue;
    }
    
}
