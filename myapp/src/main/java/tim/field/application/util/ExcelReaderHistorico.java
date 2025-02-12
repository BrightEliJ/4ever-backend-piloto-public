package tim.field.application.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tim.field.application.TarefasSS.model.TarefaHistorico;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ExcelReaderHistorico {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderSs.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private FileProcessorTracker fileProcessorTracker;

public List<TarefaHistorico> readDirectoryForTarefasHistorico(String directoryPath) throws IOException {
    List<TarefaHistorico> historico = new ArrayList<>();
    Path directory = Paths.get(directoryPath);

    if (!Files.isDirectory(directory)) {
        throw new IllegalArgumentException("O caminho especificado não é um diretorio: " + directoryPath);
    }

    try (DirectoryStream<Path> stream = Files.newDirectoryStream(directory, "*.{csv,xlsx}")) {
        for (Path entry : stream) {
            if (Files.isRegularFile(entry)) {
                historico.addAll(processFile(entry.toFile()));
            }
        }
    } catch (IOException e) {
        logger.warn("Erro ao acessar arquivos no diretorio: {}", directoryPath, e);
        throw e;
    }

    return historico;
}

    private List<TarefaHistorico> processFile(File file) throws IOException {
        String filePath = file.getAbsolutePath();
        if (fileProcessorTracker.isFileProcessedForHistorico(filePath)) {
            logger.info("Arquivo ja foi processado: {}", filePath);
            return new ArrayList<>();
        }

        List<TarefaHistorico> historico;
        if (filePath.endsWith(".csv")) {
            historico = readCsvFileForHistorico(filePath);
        } else if (filePath.endsWith(".xlsx")) {
            historico = readExcelFileForHistorico(filePath);
        } else {
            throw new IllegalArgumentException("Formato de arquivo nao suportado: " + filePath);
        }

        fileProcessorTracker.markFileAsProcessedForHistorico(filePath);
        return historico;
    }

    private List<TarefaHistorico> readExcelFileForHistorico(String filePath) throws IOException {
        List<TarefaHistorico> historicos = new ArrayList<>();
        int processedRows = 0;

        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            logger.info("Iniciando o processamento do arquivo. Total de linhas: {}", totalRows);

            for (int i = 0; i < totalRows; i++) {
                Row row = sheet.getRow(i);

                if (row == null || i == 0) { // Considera a primeira linha como cabeçalho
                    logger.info("Ignorando a linha {} (cabecalho ou linha vazia).", i);
                    continue;
                }

                TarefaHistorico historico = createHistoricoFromRow(row);
                historicos.add(historico);
                processedRows++;
            }
            logger.info("Processamento concluido. Total de tarefas lidas: {}", historicos.size());
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo Excel: ", e);
            throw e;
        }
        return historicos;
    }

    private List<TarefaHistorico> readCsvFileForHistorico(String filePath) throws IOException {
        List<TarefaHistorico> historicos = new ArrayList<>();
        int processedRows = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (processedRows == 0) { // Ignora o cabeçalho
                    logger.info("Ignorando o cabecalho do arquivo CSV.");
                    processedRows++;
                    continue;
                }

                TarefaHistorico historico = createHistoricoFromCsvValues(values);
                historicos.add(historico);
                processedRows++;
            }
            logger.info("Processamento concluido. Total de tarefas lidas: {}", historicos.size());
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo CSV: ", e);
            throw e;
        }
        return historicos;
    }

    private TarefaHistorico createHistoricoFromRow(Row row) {
        LocalDateTime dataReg = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(dataReg);

        TarefaHistorico tarefaHistorico = new TarefaHistorico();

        tarefaHistorico.setTask(getCellValueAsString(row.getCell(0)));
        tarefaHistorico.setMatricula(getCellValueAsString(row.getCell(1)));
        tarefaHistorico.setNome(getCellValueAsString(row.getCell(2)));
        tarefaHistorico.setGrupoAcionado(getCellValueAsString(row.getCell(3)));
        tarefaHistorico.setAcao(getCellValueAsString(row.getCell(4)));
        tarefaHistorico.setStatus(getCellValueAsString(row.getCell(5)));
        tarefaHistorico.setDataInicio(getCellValueAsDate(row.getCell(6)));
        tarefaHistorico.setDataFim(getCellValueAsDate(row.getCell(7)));
        tarefaHistorico.setSysCreatedOn(getCellValueAsDate(row.getCell(8)));
        tarefaHistorico.setSysCreatedBy(getCellValueAsString(row.getCell(9)));
        tarefaHistorico.setAlisePorHistorico(getCellValueAsString(row.getCell(10)));
        tarefaHistorico.setSysTags(getCellValueAsString(row.getCell(11)));
        tarefaHistorico.setSysUpdatedOn(getCellValueAsDate(row.getCell(12)));
        tarefaHistorico.setSysUpdatedBy(getCellValueAsString(row.getCell(13)));
        tarefaHistorico.setSysModCount(getCellValueAsLong(row.getCell(14)));
        tarefaHistorico.setDataReg(timestamp);  // Usando o timestamp gerado para o registro

        return tarefaHistorico;
    }

    private TarefaHistorico createHistoricoFromCsvValues(String[] values) {
        LocalDateTime dataReg = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(dataReg);

        TarefaHistorico tarefaHistorico = new TarefaHistorico();
        tarefaHistorico.setTask(values[0]);
        tarefaHistorico.setMatricula(values[1]);
        tarefaHistorico.setNome(values[2]);
        tarefaHistorico.setGrupoAcionado(values[3]);
        tarefaHistorico.setAcao(values[4]);
        tarefaHistorico.setStatus(values[5]);
        tarefaHistorico.setDataInicio(parseDate((values[6])));
        tarefaHistorico.setDataFim(parseDate((values[7])));
        tarefaHistorico.setSysCreatedOn(parseDate((values[8])));
        tarefaHistorico.setSysCreatedBy(values[9]);
        tarefaHistorico.setAlisePorHistorico(values[10]);
        tarefaHistorico.setSysTags(values[11]);
        tarefaHistorico.setSysUpdatedOn(parseDate((values[12])));
        tarefaHistorico.setSysUpdatedBy(values[13]);
        tarefaHistorico.setSysModCount(parseLong((values[14])));
        tarefaHistorico.setDataReg(timestamp);  // Usando o timestamp gerado para o registro

        return tarefaHistorico;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return dateFormat.format(cell.getDateCellValue());
                }
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private Date getCellValueAsDate(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK || 
            (cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty())) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return cell.getDateCellValue();
        } else if (cell.getCellType() == CellType.STRING) {
            // Tenta converter a string para uma data usando o formato esperado
            String dateStr = cell.getStringCellValue();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            try {
                return dateFormat.parse(dateStr);
            } catch (ParseException e) {
                // Caso o parse falhe, você pode registrar o erro ou tratá-lo de acordo
                e.printStackTrace();
            }
        }
        return null;
    }
    

    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return (long) cell.getNumericCellValue();
        }
        return null;
    }

    private Date parseDate(String value) {
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            logger.error("Erro ao fazer parse da data: {}", value, e);
            return null;
        }
    }

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.error("Erro ao fazer parse do numero: {}", value, e);
            return null;
        }
    }
}

