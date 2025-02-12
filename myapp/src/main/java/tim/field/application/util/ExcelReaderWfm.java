package tim.field.application.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tim.field.application.TarefasSS.model.TarefaWfm;

@Component
public class ExcelReaderWfm {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderWfm.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private FileProcessorTracker fileProcessorTracker;

    public List<TarefaWfm> readDirectoryForTarefasWfm(String directoryPath) throws IOException {
        File directory = new File(directoryPath);
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("O caminho fornecido não e um diretorio: " + directoryPath);
        }

        List<TarefaWfm> todasTarefas = new ArrayList<>();
        File[] files = directory.listFiles((dir, name) -> name.endsWith(".csv") || name.endsWith(".xlsx"));

        if (files == null) {
            logger.warn("Nenhum arquivo encontrado no diretorio: {}", directoryPath);
            return todasTarefas;
        }

        for (File file : files) {
            String filePath = file.getAbsolutePath();
            if (fileProcessorTracker.isFileProcessedForWFM(filePath)) {
                logger.info("Arquivo ja processado anteriormente: {}", filePath);
                continue; // Pular arquivos já processados
            }

            try {
                List<TarefaWfm> tarefas;
                if (filePath.endsWith(".csv")) {
                    tarefas = readCsvFileForTarefasWfm(filePath);
                } else if (filePath.endsWith(".xlsx")) {
                    tarefas = readExcelFileForTarefasWfm(filePath);
                } else {
                    throw new IllegalArgumentException("Formato de arquivo nao suportado: " + filePath);
                }

                todasTarefas.addAll(tarefas);
                fileProcessorTracker.markFileAsProcessedForWFM(filePath);
            } catch (IOException e) {
                logger.error("Erro ao processar o arquivo: {}", file.getName(), e);
            }
        }

        logger.info("Processamento concluido. Total de tarefas lidas: {}", todasTarefas.size());
        return todasTarefas;
    }

    private List<TarefaWfm> readExcelFileForTarefasWfm(String filePath) throws IOException {
        List<TarefaWfm> tarefas = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            int totalRows = sheet.getPhysicalNumberOfRows();
            logger.info("Iniciando o processamento do arquivo Excel: {}. Total de linhas: {}", filePath, totalRows);

            for (int i = 0; i < totalRows; i++) {
                Row row = sheet.getRow(i);

                if (row == null || i == 0) {
                    logger.info("Ignorando a linha {} (cabecalho ou linha vazia).", i);
                    continue;
                }

                TarefaWfm tarefa = createTarefaWfmFromRow(row);
                tarefas.add(tarefa);
            }
            logger.info("Processamento do arquivo Excel concluído. Total de tarefas lidas: {}", tarefas.size());
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo Excel: ", e);
            throw e;
        }
        return tarefas;
    }

    private List<TarefaWfm> readCsvFileForTarefasWfm(String filePath) throws IOException {
        List<TarefaWfm> tarefas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            int processedRows = 0;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                if (processedRows == 0) {
                    logger.info("Ignorando o cabecalho do arquivo CSV: {}", filePath);
                    processedRows++;
                    continue;
                }

                TarefaWfm tarefa = createTarefaWfmFromCsvValues(values);
                tarefas.add(tarefa);
                processedRows++;
            }
            logger.info("Processamento do arquivo CSV concluido. Total de tarefas lidas: {}", tarefas.size());
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo CSV: ", e);
            throw e;
        }
        return tarefas;
    }

    private TarefaWfm createTarefaWfmFromRow(Row row) {
        LocalDateTime dataReg = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(dataReg);

        TarefaWfm tarefaWfm = new TarefaWfm();

                // Obtém o valor da célula para bscRnc
        String bscRncValue = getCellValueAsString(row.getCell(0));
        
        // Verifica o comprimento do valor e aplica a lógica específica
        if (bscRncValue != null && bscRncValue.getBytes().length > 255) {
            tarefaWfm.setBscRnc("-");
        } else {
            tarefaWfm.setBscRnc(bscRncValue);
        }

        tarefaWfm.setCidade(getCellValueAsString(row.getCell(1)));
        tarefaWfm.setClassificacaoGsbi(getCellValueAsString(row.getCell(2)));
        tarefaWfm.setCm(getCellValueAsString(row.getCell(3)));
        tarefaWfm.setContrato(getCellValueAsString(row.getCell(4)));
        tarefaWfm.setCriacaoNtt(getCellValueAsDate(row.getCell(5)));
        tarefaWfm.setData(getCellValueAsDate(row.getCell(6)));
        tarefaWfm.setDataPrimeiraRoteirizacao(getCellValueAsDate(row.getCell(7)));
        tarefaWfm.setDataUltimaRoteirizacao(getCellValueAsDate(row.getCell(8)));
        tarefaWfm.setDescricaoGmg(getCellValueAsString(row.getCell(9)));
        tarefaWfm.setEmpresa(getCellValueAsString(row.getCell(10)));
        tarefaWfm.setEndId(getCellValueAsString(row.getCell(11)));
        tarefaWfm.setEstado(getCellValueAsString(row.getCell(12)));
        tarefaWfm.setEta(getCellValueAsString(row.getCell(13)));
        tarefaWfm.setEvento(getCellValueAsString(row.getCell(14)));
        tarefaWfm.setFim(getCellValueAsDate(row.getCell(15)));
        tarefaWfm.setFuncaoEquipamento(getCellValueAsString(row.getCell(16)));
        tarefaWfm.setGrupo(getCellValueAsString(row.getCell(17)));
        tarefaWfm.setHoraCriacaoAtividade(getCellValueAsDate(row.getCell(18)));
        tarefaWfm.setIdAtividade(getCellValueAsLong(row.getCell(19)));
        tarefaWfm.setIdTicketCa(getCellValueAsString(row.getCell(20)));
        tarefaWfm.setInicioGmg(getCellValueAsDate(row.getCell(21)));
        tarefaWfm.setMatriculaProvedor(getCellValueAsString(row.getCell(22)));
        tarefaWfm.setMotivoSuspensao(getCellValueAsString(row.getCell(23)));
        tarefaWfm.setMotivoTramitacao(getCellValueAsString(row.getCell(24)));
        tarefaWfm.setMotivoPendenciamento(getCellValueAsString(row.getCell(25)));
        tarefaWfm.setNeId(getCellValueAsString(row.getCell(26)));
        tarefaWfm.setNotaAbertura(getCellValueAsString(row.getCell(27)));
        tarefaWfm.setTask(getCellValueAsString(row.getCell(28)));
        tarefaWfm.setResolucaoProblema(getCellValueAsString(row.getCell(29)));
        tarefaWfm.setLocalProblema(getCellValueAsString(row.getCell(30)));
        tarefaWfm.setOperadora(getCellValueAsString(row.getCell(31)));
        tarefaWfm.setPrediosIndustriais(getCellValueAsString(row.getCell(32)));
        tarefaWfm.setPrioridade(getCellValueAsString(row.getCell(33)));
        tarefaWfm.setPriorizacaoDispatching(getCellValueAsLong(row.getCell(34)));
        tarefaWfm.setPriorizacaoDispatchingClassific(getCellValueAsString(row.getCell(35)));
        tarefaWfm.setProvedor(getCellValueAsString(row.getCell(36)));
        tarefaWfm.setCausaFalhaElemento(getCellValueAsString(row.getCell(37)));
        tarefaWfm.setRegional(getCellValueAsString(row.getCell(38)));
        tarefaWfm.setRegraUsuarioCriador(getCellValueAsString(row.getCell(39)));
        tarefaWfm.setRepetido(getCellValueAsString(row.getCell(40)));
        tarefaWfm.setResponsabilidade(getCellValueAsString(row.getCell(41)));
        tarefaWfm.setResponsavelGmg(getCellValueAsString(row.getCell(42)));
        tarefaWfm.setSeguimentoRedeEquipamento(getCellValueAsString(row.getCell(43)));
        tarefaWfm.setStatusGmg(getCellValueAsString(row.getCell(44)));
        tarefaWfm.setSubArea(getCellValueAsString(row.getCell(45)));
        tarefaWfm.setTerminoGmg(getCellValueAsDate(row.getCell(46)));
        tarefaWfm.setTipoContrato(getCellValueAsString(row.getCell(47)));
        tarefaWfm.setTipoAtividade(getCellValueAsString(row.getCell(48)));
        tarefaWfm.setTipoFalha(getCellValueAsString(row.getCell(49)));
        tarefaWfm.setTipoNe(getCellValueAsString(row.getCell(50)));
        tarefaWfm.setTituloAlarme(getCellValueAsString(row.getCell(51)));
        tarefaWfm.setTramitacaoSuspensao(getCellValueAsString(row.getCell(52)));
        tarefaWfm.setUf(getCellValueAsString(row.getCell(53)));
        tarefaWfm.setUsuarioExecutor(getCellValueAsString(row.getCell(54)));
        tarefaWfm.setWorkzone(getCellValueAsString(row.getCell(55)));
        tarefaWfm.setWorkzoneEndId(getCellValueAsString(row.getCell(56)));
        tarefaWfm.setDataColeta(getCellValueAsDate(row.getCell(57)));
        tarefaWfm.setBaseOrigem(getCellValueAsString(row.getCell(58)));
        tarefaWfm.setDataReg(timestamp);

        return tarefaWfm;
    }

    private TarefaWfm createTarefaWfmFromCsvValues(String[] values) {
        LocalDateTime dataReg = LocalDateTime.now();
        Timestamp timestamp = Timestamp.valueOf(dataReg);
    
        TarefaWfm tarefaWfm = new TarefaWfm();
    
        // Aplica a verificação de comprimento de bytes apenas ao campo bscRnc
        String bscRncValue = values[0];
        if (bscRncValue != null && bscRncValue.getBytes().length > 255) {
            tarefaWfm.setBscRnc("-");
        } else {
            tarefaWfm.setBscRnc(parseString(bscRncValue));
        }
    
        tarefaWfm.setCidade(parseString(values[1]));
        tarefaWfm.setClassificacaoGsbi(parseString(values[2]));
        tarefaWfm.setCm(parseString(values[3]));
        tarefaWfm.setContrato(parseString(values[4]));
        tarefaWfm.setCriacaoNtt(parseDate(values[5]));
        tarefaWfm.setData(parseDate(values[6]));
        tarefaWfm.setDataPrimeiraRoteirizacao(parseDate(values[7]));
        tarefaWfm.setDataUltimaRoteirizacao(parseDate(values[8]));
        tarefaWfm.setDescricaoGmg(parseString(values[9]));
        tarefaWfm.setEmpresa(parseString(values[10]));
        tarefaWfm.setEndId(parseString(values[11]));
        tarefaWfm.setEstado(parseString(values[12]));
        tarefaWfm.setEta(parseString(values[13]));
        tarefaWfm.setEvento(parseString(values[14]));
        tarefaWfm.setFim(parseDate(values[15]));
        tarefaWfm.setFuncaoEquipamento(parseString(values[16]));
        tarefaWfm.setGrupo(parseString(values[17]));
        tarefaWfm.setHoraCriacaoAtividade(parseDate(values[18]));
        tarefaWfm.setIdAtividade(parseLong(values[19]));
        tarefaWfm.setIdTicketCa(parseString(values[20]));
        tarefaWfm.setInicioGmg(parseDate(values[21]));
        tarefaWfm.setMatriculaProvedor(parseString(values[22]));
        tarefaWfm.setMotivoSuspensao(parseString(values[23]));
        tarefaWfm.setMotivoTramitacao(parseString(values[24]));
        tarefaWfm.setMotivoPendenciamento(parseString(values[25]));
        tarefaWfm.setNeId(parseString(values[26]));
        tarefaWfm.setNotaAbertura(parseString(values[27]));
        tarefaWfm.setTask(parseString(values[28]));
        tarefaWfm.setResolucaoProblema(parseString(values[29]));
        tarefaWfm.setLocalProblema(parseString(values[30]));
        tarefaWfm.setOperadora(parseString(values[31]));
        tarefaWfm.setPrediosIndustriais(parseString(values[32]));
        tarefaWfm.setPrioridade(parseString(values[33]));
        tarefaWfm.setPriorizacaoDispatching(parseLong(values[34]));
        tarefaWfm.setPriorizacaoDispatchingClassific(parseString(values[35]));
        tarefaWfm.setProvedor(parseString(values[36]));
        tarefaWfm.setCausaFalhaElemento(parseString(values[37]));
        tarefaWfm.setRegional(parseString(values[38]));
        tarefaWfm.setRegraUsuarioCriador(parseString(values[39]));
        tarefaWfm.setRepetido(parseString(values[40]));
        tarefaWfm.setResponsabilidade(parseString(values[41]));
        tarefaWfm.setResponsavelGmg(parseString(values[42]));
        tarefaWfm.setSeguimentoRedeEquipamento(parseString(values[43]));
        tarefaWfm.setStatusGmg(parseString(values[44]));
        tarefaWfm.setSubArea(parseString(values[45]));
        tarefaWfm.setTerminoGmg(parseDate(values[46]));
        tarefaWfm.setTipoContrato(parseString(values[47]));
        tarefaWfm.setTipoAtividade(parseString(values[48]));
        tarefaWfm.setTipoFalha(parseString(values[49]));
        tarefaWfm.setTipoNe(parseString(values[50]));
        tarefaWfm.setTituloAlarme(parseString(values[51]));
        tarefaWfm.setTramitacaoSuspensao(parseString(values[52]));
        tarefaWfm.setUf(parseString(values[53]));
        tarefaWfm.setUsuarioExecutor(parseString(values[54]));
        tarefaWfm.setWorkzone(parseString(values[55]));
        tarefaWfm.setWorkzoneEndId(parseString(values[56]));
        tarefaWfm.setDataColeta(parseDate(values[57]));
        tarefaWfm.setBaseOrigem(parseString(values[58]));
        tarefaWfm.setDataReg(timestamp);
    
        return tarefaWfm;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }
        switch (cell.getCellType()) {
            case STRING:
                String stringValue = cell.getStringCellValue();
                return "-".equals(stringValue) ? null : stringValue;
            case NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return cell.toString();
        }
    }

    private Date getCellValueAsDate(Cell cell) {
        if (cell == null) {
            return null;
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getDateCellValue();
        }
        String stringValue = getCellValueAsString(cell);
        return parseDate(stringValue);
    }

    private Long getCellValueAsLong(Cell cell) {
        if (cell == null) {
            return null;
        }
        try {
            switch (cell.getCellType()) {
                case NUMERIC:
                    return (long) cell.getNumericCellValue();
                case STRING:
                    // Tenta converter o valor da célula do tipo STRING para LONG
                    return parseLong(cell.getStringCellValue());
                default:
                    return null;
            }
        } catch (Exception e) {
            logger.error("Erro ao converter o valor da célula para LONG", e);
            return null;
        }
    }

    private Long parseLong(String value) {
        if (value == null || value.trim().isEmpty() || "-".equals(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            logger.error("Erro ao fazer parse do numero: {}", value, e);
            return null;
        }
    }

    private String parseString(String value) {
        if (value == null || "-".equals(value)) {
            return null;
        }
        return value.trim();
    }

    private Date parseDate(String value) {
        if (value == null || value.isEmpty() || "-".equals(value) || "0000/00/00".equals(value) || "/".equals(value)) {
            return null;
        }
        // Tente identificar e filtrar casos onde a string não segue o formato esperado
        if (value.length() < 10 || !value.matches("\\d{4}/\\d{2}/\\d{2}( \\d{2}:\\d{2}:\\d{2})?")) {
            return null;
        }
        try {
            return new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").parse(value);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
