package tim.field.application.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.ZipInputStream;

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

import tim.field.application.TarefasSS.model.Tarefa;

@Component
public class ExcelReaderSs {

    private static final Logger logger = LoggerFactory.getLogger(ExcelReaderSs.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    static {
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Sao_Paulo"));
    }

    @Autowired
    private FileProcessorTracker fileProcessorTracker;

    public List<Tarefa> readDirectoryForTarefasSs(String directoryPath) throws IOException {
        List<Tarefa> tarefas = new ArrayList<>();
        File directory = new File(directoryPath);

        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("O caminho especificado não e um diretório: " + directoryPath);
        }

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && (file.getName().endsWith(".csv") || file.getName().endsWith(".xlsx"))) {
                    tarefas.addAll(processFile(file));
                }
            }
        } else {
            logger.warn("Nenhum arquivo encontrado no diretorio: {}", directoryPath);
        }

        return tarefas;
    }

    private List<Tarefa> processFile(File file) throws IOException {
        String filePath = file.getAbsolutePath();
        if (fileProcessorTracker.isFileProcessedForSS(filePath)) {
            logger.info("Arquivo ja foi processado: {}", filePath);
            return new ArrayList<>();
        }

        List<Tarefa> tarefas;
        if (filePath.endsWith(".csv")) {
            tarefas = readCsvFileForTarefas(filePath);
        } else if (filePath.endsWith(".xlsx")) {
            // Verifica se o arquivo é um arquivo Excel válido antes de processar
            if (!isValidExcelFile(file)) {
                logger.error("O arquivo {} não é um arquivo Excel válido.", filePath);
                return new ArrayList<>();
            }
            tarefas = readExcelFileForTarefas(filePath);
        } else {
            throw new IllegalArgumentException("Formato de arquivo nao suportado: " + filePath);
        }

        fileProcessorTracker.markFileAsProcessedForSS(filePath);
        return tarefas;
    }

    private boolean isValidExcelFile(File file) {
        try (FileInputStream fis = new FileInputStream(file);
             ZipInputStream zis = new ZipInputStream(fis)) {
            // Verifica se o arquivo pode ser lido como um arquivo ZIP, indicando que é um arquivo OOXML
            return zis.getNextEntry() != null;
        } catch (IOException e) {
            logger.error("Erro ao validar o arquivo Excel: ", e);
            return false;
        }
    }

    private List<Tarefa> readExcelFileForTarefas(String filePath) throws IOException {
        List<Tarefa> tarefas = new ArrayList<>();
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

                Tarefa tarefa = createTarefaFromRow(row);
                tarefas.add(tarefa);
                processedRows++;
            }
            logger.info("Processamento concluido. Total de tarefas lidas: {}", tarefas.size());
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo Excel: ", e);
            throw e;
        }
        return tarefas;
    }

    private List<Tarefa> readCsvFileForTarefas(String filePath) throws IOException {
        List<Tarefa> tarefas = new ArrayList<>();
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

                Tarefa tarefa = createTarefaFromCsvValues(values);
                tarefas.add(tarefa);
                processedRows++;
            }
            logger.info("Processamento concluido. Total de tarefas lidas: {}", tarefas.size());
        } catch (IOException e) {
            logger.error("Erro ao processar o arquivo CSV: ", e);
            throw e;
        }
        return tarefas;
    }


    private Tarefa createTarefaFromRow(Row row) {
        // Use LocalDateTime e ZonedDateTime para manipular a data e o fuso horário corretamente
        LocalDateTime dataReg = LocalDateTime.now();
        ZonedDateTime zonedDateTime = dataReg.atZone(ZoneId.of("America/Sao_Paulo"));
        Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());

        Tarefa tarefa = new Tarefa();
        tarefa.setTask(getCellValueAsString(row.getCell(0)));
        tarefa.setEvento(getCellValueAsString(row.getCell(1)));
        tarefa.setDataCriacao(parseDate(getCellValueAsString(row.getCell(2))));
        tarefa.setDataFechamento(parseDate(getCellValueAsString(row.getCell(3))));
        tarefa.setDataHoraSlaWfm(parseDate(getCellValueAsString(row.getCell(4))));
        tarefa.setDescricaoSolucao(getCellValueAsString(row.getCell(5)));
        tarefa.setDescricaoResumida(getCellValueAsString(row.getCell(6)));
        tarefa.setDetalhamentoCausa(getCellValueAsString(row.getCell(7)));
        tarefa.setGrupoAcionado(getCellValueAsString(row.getCell(8)));
        tarefa.setGrupoCriador(getCellValueAsString(row.getCell(9)));
        tarefa.setIdWfm(getCellValueAsLong(row.getCell(10)));
        tarefa.setMarcadores(getCellValueAsString(row.getCell(11)));
        tarefa.setNotaTramitacao(getCellValueAsString(row.getCell(12)));
        tarefa.setNotaSuspensao(getCellValueAsString(row.getCell(13)));
        tarefa.setNotaCancelamento(getCellValueAsString(row.getCell(14)));
        tarefa.setPrevisaoNormalizacao(parseDate(getCellValueAsString(row.getCell(15))));
        tarefa.setSla(parseDate(getCellValueAsString(row.getCell(16))));
        tarefa.setWfm(getCellValueAsString(row.getCell(17)));
        tarefa.setNe(getCellValueAsString(row.getCell(18)));
        tarefa.setNeIdDescricao(getCellValueAsString(row.getCell(19)));
        tarefa.setNeIdTarefa(getCellValueAsString(row.getCell(20)));
        tarefa.setNeIdEvento(getCellValueAsString(row.getCell(21)));
        tarefa.setNeId(getCellValueAsString(row.getCell(22)));
        tarefa.setNotDone(getCellValueAsString(row.getCell(23)));
        tarefa.setAnotacoesTrabalho(getCellValueAsString(row.getCell(24)));
        tarefa.setAcompanhamento(getCellValueAsString(row.getCell(25)));
        tarefa.setAnotacoesFechamento(getCellValueAsString(row.getCell(26)));
        tarefa.setAcaoNotDone(getCellValueAsString(row.getCell(27)));
        tarefa.setAcaoPaliativa(getCellValueAsString(row.getCell(28)));
        tarefa.setAtualizadoEm(parseDate(getCellValueAsString(row.getCell(29))));
        tarefa.setAtualizadoPor(getCellValueAsString(row.getCell(30)));
        tarefa.setAtualizacoes(getCellValueAsLong(row.getCell(31)));
        tarefa.setComentariosAdicionais(getCellValueAsString(row.getCell(32)));
        tarefa.setComentariosAnotacoesTrabalho(getCellValueAsString(row.getCell(33)));
        tarefa.setExpectedStart(parseDate(getCellValueAsString(row.getCell(34))));
        tarefa.setLogNotasCrm(getCellValueAsString(row.getCell(35)));
        tarefa.setNotaWfmNotDoneTarefaOrigem(getCellValueAsString(row.getCell(36)));
        tarefa.setTempoTrabalhado(getCellValueAsLong(row.getCell(37)));
        tarefa.setTermino(getCellValueAsString(row.getCell(38)));
        tarefa.setAreaDeRisco(getCellValueAsString(row.getCell(39)));
        tarefa.setAbertoPor(getCellValueAsString(row.getCell(40)));
        tarefa.setEscalation(getCellValueAsString(row.getCell(41)));
        tarefa.setEntradaUsuario(getCellValueAsString(row.getCell(42)));
        tarefa.setFalha(getCellValueAsString(row.getCell(43)));
        tarefa.setListaGrupos(getCellValueAsString(row.getCell(44)));
        tarefa.setMatriculaTecnico(getCellValueAsString(row.getCell(45)));
        tarefa.setMotivoPrimario(getCellValueAsString(row.getCell(46)));
        tarefa.setMotivoRejeicao(getCellValueAsString(row.getCell(47)));
        tarefa.setMotivoSecundario(getCellValueAsString(row.getCell(48)));
        tarefa.setMotivoTransferencia(getCellValueAsString(row.getCell(49)));
        tarefa.setMotivoCancelamento(getCellValueAsString(row.getCell(50)));
        tarefa.setMotivoPendenciamento(getCellValueAsString(row.getCell(51)));
        tarefa.setAnsCriado(getCellValueAsString(row.getCell(52)));
        tarefa.setUfTarefa(getCellValueAsString(row.getCell(53)));
        tarefa.setTaskOrigemWfm(getCellValueAsString(row.getCell(54)));
        tarefa.setTmrTsk(getCellValueAsString(row.getCell(55)));
        tarefa.setStatus(getCellValueAsString(row.getCell(56)));
        tarefa.setSolucaoFalha(getCellValueAsString(row.getCell(57)));
        tarefa.setSolucaoPaliativa(getCellValueAsString(row.getCell(58)));
        tarefa.setSitesDependentesTotalTx(getCellValueAsString(row.getCell(59)));
        tarefa.setSolucionadorFalha(getCellValueAsString(row.getCell(60)));
        tarefa.setStatusAcaoRealizada(getCellValueAsString(row.getCell(61)));
        tarefa.setSitesDependentesPorTx(getCellValueAsLong(row.getCell(62)));
        tarefa.setRotaLink(getCellValueAsString(row.getCell(63)));
        tarefa.setReincidente(getCellValueAsString(row.getCell(64)));
        tarefa.setPrioridade(getCellValueAsString(row.getCell(65)));
        tarefa.setPrazo(getCellValueAsString(row.getCell(66)));
        tarefa.setNomeTecnicoCampo(getCellValueAsString(row.getCell(67)));
        tarefa.setNomeTecnico(getCellValueAsString(row.getCell(68)));
        tarefa.setMotivo(getCellValueAsString(row.getCell(69)));
        tarefa.setLatitudeEndereco(getCellValueAsString(row.getCell(70)));
        tarefa.setLongitudeEndereco(getCellValueAsString(row.getCell(71)));
        tarefa.setTelefoneTecnico(getCellValueAsString(row.getCell(72)));
        tarefa.setUsuario(getCellValueAsString(row.getCell(73)));
        tarefa.setAlarme(getCellValueAsString(row.getCell(74)));
        tarefa.setAlarmeNormalizado(getCellValueAsString(row.getCell(75)));
        tarefa.setEventoMassivo(getCellValueAsString(row.getCell(76)));
        tarefa.setTipoMassiva(getCellValueAsString(row.getCell(77)));
        tarefa.setTempoPendenciamento(getCellValueAsLong(row.getCell(78)));
        tarefa.setDataReg(timestamp);  // Usando o timestamp gerado para o registro

        return tarefa;
    }

    private Tarefa createTarefaFromCsvValues(String[] values) {
        LocalDateTime dataReg = LocalDateTime.now();
        ZonedDateTime zonedDateTime = dataReg.atZone(ZoneId.of("America/Sao_Paulo"));
        Timestamp timestamp = Timestamp.from(zonedDateTime.toInstant());

        Tarefa tarefa = new Tarefa();
        tarefa.setTask(values[0]);
        tarefa.setEvento(values[1]);
        tarefa.setDataCriacao(parseDate(values[2]));
        tarefa.setDataFechamento(parseDate(values[3]));
        tarefa.setDataHoraSlaWfm(parseDate(values[4]));
        tarefa.setDescricaoSolucao(values[5]);
        tarefa.setDescricaoResumida(values[6]);
        tarefa.setDetalhamentoCausa(values[7]);
        tarefa.setGrupoAcionado(values[8]);
        tarefa.setGrupoCriador(values[9]);
        tarefa.setIdWfm(parseLong(values[10]));
        tarefa.setMarcadores(values[11]);
        tarefa.setNotaTramitacao(values[12]);
        tarefa.setNotaSuspensao(values[13]);
        tarefa.setNotaCancelamento(values[14]);
        tarefa.setPrevisaoNormalizacao(parseDate(values[15]));
        tarefa.setSla(parseDate(values[16]));
        tarefa.setWfm(values[17]);
        tarefa.setNe(values[18]);
        tarefa.setNeIdDescricao(values[19]);
        tarefa.setNeIdTarefa(values[20]);
        tarefa.setNeIdEvento(values[21]);
        tarefa.setNeId(values[22]);
        tarefa.setNotDone(values[23]);
        tarefa.setAnotacoesTrabalho(values[24]);
        tarefa.setAcompanhamento(values[25]);
        tarefa.setAnotacoesFechamento(values[26]);
        tarefa.setAcaoNotDone(values[27]);
        tarefa.setAcaoPaliativa(values[28]);
        tarefa.setAtualizadoEm(parseDate(values[29]));
        tarefa.setAtualizadoPor(values[30]);
        tarefa.setAtualizacoes(parseLong(values[31]));
        tarefa.setComentariosAdicionais(values[32]);
        tarefa.setComentariosAnotacoesTrabalho(values[33]);
        tarefa.setExpectedStart(parseDate(values[34]));
        tarefa.setLogNotasCrm(values[35]);
        tarefa.setNotaWfmNotDoneTarefaOrigem(values[36]);
        tarefa.setTempoTrabalhado(parseLong(values[37]));
        tarefa.setTermino(values[38]);
        tarefa.setAreaDeRisco(values[39]);
        tarefa.setAbertoPor(values[40]);
        tarefa.setEscalation(values[41]);
        tarefa.setEntradaUsuario(values[42]);
        tarefa.setFalha(values[43]);
        tarefa.setListaGrupos(values[44]);
        tarefa.setMatriculaTecnico(values[45]);
        tarefa.setMotivoPrimario(values[46]);
        tarefa.setMotivoRejeicao(values[47]);
        tarefa.setMotivoSecundario(values[48]);
        tarefa.setMotivoTransferencia(values[49]);
        tarefa.setMotivoCancelamento(values[50]);
        tarefa.setMotivoPendenciamento(values[51]);
        tarefa.setAnsCriado(values[52]);
        tarefa.setUfTarefa(values[53]);
        tarefa.setTaskOrigemWfm(values[54]);
        tarefa.setTmrTsk(values[55]);
        tarefa.setStatus(values[56]);
        tarefa.setSolucaoFalha(values[57]);
        tarefa.setSolucaoPaliativa(values[58]);
        tarefa.setSitesDependentesTotalTx(values[59]);
        tarefa.setSolucionadorFalha(values[60]);
        tarefa.setStatusAcaoRealizada(values[61]);
        tarefa.setSitesDependentesPorTx(parseLong(values[62]));
        tarefa.setRotaLink(values[63]);
        tarefa.setReincidente(values[64]);
        tarefa.setPrioridade(values[65]);
        tarefa.setPrazo(values[66]);
        tarefa.setNomeTecnicoCampo(values[67]);
        tarefa.setNomeTecnico(values[68]);
        tarefa.setMotivo(values[69]);
        tarefa.setLatitudeEndereco(values[70]);
        tarefa.setLongitudeEndereco(values[71]);
        tarefa.setTelefoneTecnico(values[72]);
        tarefa.setUsuario(values[73]);
        tarefa.setAlarme(values[74]);
        tarefa.setAlarmeNormalizado(values[75]);
        tarefa.setEventoMassivo(values[76]);
        tarefa.setTipoMassiva(values[77]);
        tarefa.setTempoPendenciamento(parseLong(values[78]));
        tarefa.setDataReg(timestamp);  // Usando o timestamp gerado para o registro

        return tarefa;
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

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            // Retorna null se a string de data estiver vazia ou nula
            return null;
        }
        try {
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            logger.error("Erro ao fazer parse da data: {}", dateStr, e);
            return null;
        }
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

    private Long parseLong(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            logger.error("Erro ao fazer parse do numero: {}", value, e);
            return null;
        }
    }
}
