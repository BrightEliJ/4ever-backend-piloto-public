package tim.field.application.TarefasSS.model;

import java.sql.Timestamp;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_task_ss")
public class Tarefa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "task")
    private String task;

    @Column(name = "evento")
    private String evento;

    @Column(name = "data_criacao")
    private Date dataCriacao;

    @Column(name = "data_fechamento")
    private Date dataFechamento;

    @Column(name = "data_hora_sla_wfm")
    private Date dataHoraSlaWfm;

    @Column(name = "descricao_solucao")
    private String descricaoSolucao;

    @Column(name = "descricao_resumida")
    private String descricaoResumida;

    @Column(name = "detalhamento_causa")
    private String detalhamentoCausa;

    @Column(name = "grupo_acionado")
    private String grupoAcionado;

    @Column(name = "grupo_criador")
    private String grupoCriador;

    @Column(name = "id_wfm")
    private Long idWfm;

    @Column(name = "marcadores")
    private String marcadores;

    @Column(name = "nota_tramitacao")
    private String notaTramitacao;

    @Column(name = "nota_suspensao")
    private String notaSuspensao;

    @Column(name = "nota_cancelamento")
    private String notaCancelamento;

    @Column(name = "previsao_normalizacao")
    private Date previsaoNormalizacao;

    @Column(name = "sla")
    private Date sla;

    @Column(name = "wfm")
    private String wfm;

    @Column(name = "ne")
    private String ne;

    @Column(name = "ne_id_descricao")
    private String neIdDescricao;

    @Column(name = "ne_id_tarefa")
    private String neIdTarefa;

    @Column(name = "ne_id_evento")
    private String neIdEvento;

    @Column(name = "ne_id")
    private String neId;

    @Column(name = "not_done")
    private String notDone;

    @Column(name = "anotacoes_trabalho", columnDefinition = "TEXT")
    private String anotacoesTrabalho;

    @Column(name = "acompanhamento")
    private String acompanhamento;

    @Column(name = "anotacoes_fechamento", columnDefinition = "TEXT")
    private String anotacoesFechamento;

    @Column(name = "acao_not_done")
    private String acaoNotDone;

    @Column(name = "acao_paliativa")
    private String acaoPaliativa;

    @Column(name = "atualizado_em")
    private Date atualizadoEm;

    @Column(name = "atualizado_por")
    private String atualizadoPor;

    @Column(name = "atualizacoes")
    private Long atualizacoes;

    @Column(name = "comentarios_adicionais", columnDefinition = "TEXT")
    private String comentariosAdicionais;

    @Column(name = "comentarios_anotacoes_trabalho", columnDefinition = "TEXT")
    private String comentariosAnotacoesTrabalho;

    @Column(name = "expected_start")
    private Date expectedStart;

    @Column(name = "log_notas_crm", columnDefinition = "TEXT")
    private String logNotasCrm;

    @Column(name = "nota_wfm_not_done_tarefa_origem", columnDefinition = "TEXT")
    private String notaWfmNotDoneTarefaOrigem;

    @Column(name = "tempo_trabalhado")
    private Long tempoTrabalhado;

    @Column(name = "termino")
    private String termino;

    @Column(name = "area_de_risco")
    private String areaDeRisco;

    @Column(name = "aberto_por")
    private String abertoPor;

    @Column(name = "escalation")
    private String escalation;

    @Column(name = "entrada_usuario")
    private String entradaUsuario;

    @Column(name = "falha")
    private String falha;

    @Column(name = "lista_grupos")
    private String listaGrupos;

    @Column(name = "matricula_tecnico")
    private String matriculaTecnico;

    @Column(name = "motivo_primario")
    private String motivoPrimario;

    @Column(name = "motivo_rejeicao")
    private String motivoRejeicao;

    @Column(name = "motivo_secundario")
    private String motivoSecundario;

    @Column(name = "motivo_transferencia")
    private String motivoTransferencia;

    @Column(name = "motivo_cancelamento")
    private String motivoCancelamento;

    @Column(name = "motivo_pendenciamento")
    private String motivoPendenciamento;

    @Column(name = "ans_criado")
    private String ansCriado;

    @Column(name = "uf_tarefa")
    private String ufTarefa;

    @Column(name = "task_origem_wfm")
    private String taskOrigemWfm;

    @Column(name = "tmr_tsk")
    private String tmrTsk;

    @Column(name = "status")
    private String status;

    @Column(name = "solucao_falha")
    private String solucaoFalha;

    @Column(name = "solucao_paliativa")
    private String solucaoPaliativa;

    @Column(name = "sites_dependentes_total_tx")
    private String sitesDependentesTotalTx;

    @Column(name = "solucionador_falha")
    private String solucionadorFalha;

    @Column(name = "status_acao_realizada")
    private String statusAcaoRealizada;

    @Column(name = "sites_dependentes_por_tx")
    private Long sitesDependentesPorTx;

    @Column(name = "rota_link")
    private String rotaLink;

    @Column(name = "reincidente")
    private String reincidente;

    @Column(name = "prioridade")
    private String prioridade;

    @Column(name = "prazo")
    private String prazo;

    @Column(name = "nome_tecnico_campo")
    private String nomeTecnicoCampo;

    @Column(name = "nome_tecnico")
    private String nomeTecnico;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "latitude_endereco")
    private String latitudeEndereco;

    @Column(name = "longitude_endereco")
    private String longitudeEndereco;

    @Column(name = "telefone_tecnico")
    private String telefoneTecnico;

    @Column(name = "usuario")
    private String usuario;

    @Column(name = "alarme")
    private String alarme;

    @Column(name = "alarme_normalizado")
    private String alarmeNormalizado;

    @Column(name = "evento_massivo")
    private String eventoMassivo;

    @Column(name = "tipo_massiva")
    private String tipoMassiva;

    @Column(name = "tempo_pendenciamento")
    private Long tempoPendenciamento;

    @Column(name = "data_reg")
    private Timestamp dataReg;

    public Timestamp getDataReg() {
        return dataReg;
    }

    public void setDataReg(Timestamp dataReg) {
        this.dataReg = dataReg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public Date getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(Date dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public Date getDataFechamento() {
        return dataFechamento;
    }

    public void setDataFechamento(Date dataFechamento) {
        this.dataFechamento = dataFechamento;
    }

    public Date getDataHoraSlaWfm() {
        return dataHoraSlaWfm;
    }

    public void setDataHoraSlaWfm(Date dataHoraSlaWfm) {
        this.dataHoraSlaWfm = dataHoraSlaWfm;
    }

    public String getDescricaoSolucao() {
        return descricaoSolucao;
    }

    public void setDescricaoSolucao(String descricaoSolucao) {
        this.descricaoSolucao = descricaoSolucao;
    }

    public String getDescricaoResumida() {
        return descricaoResumida;
    }

    public void setDescricaoResumida(String descricaoResumida) {
        this.descricaoResumida = descricaoResumida;
    }

    public String getDetalhamentoCausa() {
        return detalhamentoCausa;
    }

    public void setDetalhamentoCausa(String detalhamentoCausa) {
        this.detalhamentoCausa = detalhamentoCausa;
    }

    public String getGrupoAcionado() {
        return grupoAcionado;
    }

    public void setGrupoAcionado(String grupoAcionado) {
        this.grupoAcionado = grupoAcionado;
    }

    public String getGrupoCriador() {
        return grupoCriador;
    }

    public void setGrupoCriador(String grupoCriador) {
        this.grupoCriador = grupoCriador;
    }

    public Long getIdWfm() {
        return idWfm;
    }

    public void setIdWfm(Long idWfm) {
        this.idWfm = idWfm;
    }

    public String getMarcadores() {
        return marcadores;
    }

    public void setMarcadores(String marcadores) {
        this.marcadores = marcadores;
    }

    public String getNotaTramitacao() {
        return notaTramitacao;
    }

    public void setNotaTramitacao(String notaTramitacao) {
        this.notaTramitacao = notaTramitacao;
    }

    public String getNotaSuspensao() {
        return notaSuspensao;
    }

    public void setNotaSuspensao(String notaSuspensao) {
        this.notaSuspensao = notaSuspensao;
    }

    public String getNotaCancelamento() {
        return notaCancelamento;
    }

    public void setNotaCancelamento(String notaCancelamento) {
        this.notaCancelamento = notaCancelamento;
    }

    public Date getPrevisaoNormalizacao() {
        return previsaoNormalizacao;
    }

    public void setPrevisaoNormalizacao(Date previsaoNormalizacao) {
        this.previsaoNormalizacao = previsaoNormalizacao;
    }

    public Date getSla() {
        return sla;
    }

    public void setSla(Date sla) {
        this.sla = sla;
    }

    public String getWfm() {
        return wfm;
    }

    public void setWfm(String wfm) {
        this.wfm = wfm;
    }

    public String getNe() {
        return ne;
    }

    public void setNe(String ne) {
        this.ne = ne;
    }

    public String getNeIdDescricao() {
        return neIdDescricao;
    }

    public void setNeIdDescricao(String neIdDescricao) {
        this.neIdDescricao = neIdDescricao;
    }

    public String getNeIdTarefa() {
        return neIdTarefa;
    }

    public void setNeIdTarefa(String neIdTarefa) {
        this.neIdTarefa = neIdTarefa;
    }

    public String getNeIdEvento() {
        return neIdEvento;
    }

    public void setNeIdEvento(String neIdEvento) {
        this.neIdEvento = neIdEvento;
    }

    public String getNeId() {
        return neId;
    }

    public void setNeId(String neId) {
        this.neId = neId;
    }

    public String getNotDone() {
        return notDone;
    }

    public void setNotDone(String notDone) {
        this.notDone = notDone;
    }

    public String getAnotacoesTrabalho() {
        return anotacoesTrabalho;
    }

    public void setAnotacoesTrabalho(String anotacoesTrabalho) {
        this.anotacoesTrabalho = anotacoesTrabalho;
    }

    public String getAcompanhamento() {
        return acompanhamento;
    }

    public void setAcompanhamento(String acompanhamento) {
        this.acompanhamento = acompanhamento;
    }

    public String getAnotacoesFechamento() {
        return anotacoesFechamento;
    }

    public void setAnotacoesFechamento(String anotacoesFechamento) {
        this.anotacoesFechamento = anotacoesFechamento;
    }

    public String getAcaoNotDone() {
        return acaoNotDone;
    }

    public void setAcaoNotDone(String acaoNotDone) {
        this.acaoNotDone = acaoNotDone;
    }

    public String getAcaoPaliativa() {
        return acaoPaliativa;
    }

    public void setAcaoPaliativa(String acaoPaliativa) {
        this.acaoPaliativa = acaoPaliativa;
    }

    public Date getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(Date atualizadoEm) {
        this.atualizadoEm = atualizadoEm;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }

    public Long getAtualizacoes() {
        return atualizacoes;
    }

    public void setAtualizacoes(Long atualizacoes) {
        this.atualizacoes = atualizacoes;
    }

    public String getComentariosAdicionais() {
        return comentariosAdicionais;
    }

    public void setComentariosAdicionais(String comentariosAdicionais) {
        this.comentariosAdicionais = comentariosAdicionais;
    }

    public String getComentariosAnotacoesTrabalho() {
        return comentariosAnotacoesTrabalho;
    }

    public void setComentariosAnotacoesTrabalho(String comentariosAnotacoesTrabalho) {
        this.comentariosAnotacoesTrabalho = comentariosAnotacoesTrabalho;
    }

    public Date getExpectedStart() {
        return expectedStart;
    }

    public void setExpectedStart(Date expectedStart) {
        this.expectedStart = expectedStart;
    }

    public String getLogNotasCrm() {
        return logNotasCrm;
    }

    public void setLogNotasCrm(String logNotasCrm) {
        this.logNotasCrm = logNotasCrm;
    }

    public String getNotaWfmNotDoneTarefaOrigem() {
        return notaWfmNotDoneTarefaOrigem;
    }

    public void setNotaWfmNotDoneTarefaOrigem(String notaWfmNotDoneTarefaOrigem) {
        this.notaWfmNotDoneTarefaOrigem = notaWfmNotDoneTarefaOrigem;
    }

    public Long getTempoTrabalhado() {
        return tempoTrabalhado;
    }

    public void setTempoTrabalhado(Long tempoTrabalhado) {
        this.tempoTrabalhado = tempoTrabalhado;
    }

    public String getTermino() {
        return termino;
    }

    public void setTermino(String termino) {
        this.termino = termino;
    }

    public String getAreaDeRisco() {
        return areaDeRisco;
    }

    public void setAreaDeRisco(String areaDeRisco) {
        this.areaDeRisco = areaDeRisco;
    }

    public String getAbertoPor() {
        return abertoPor;
    }

    public void setAbertoPor(String abertoPor) {
        this.abertoPor = abertoPor;
    }

    public String getEscalation() {
        return escalation;
    }

    public void setEscalation(String escalation) {
        this.escalation = escalation;
    }

    public String getEntradaUsuario() {
        return entradaUsuario;
    }

    public void setEntradaUsuario(String entradaUsuario) {
        this.entradaUsuario = entradaUsuario;
    }

    public String getFalha() {
        return falha;
    }

    public void setFalha(String falha) {
        this.falha = falha;
    }

    public String getListaGrupos() {
        return listaGrupos;
    }

    public void setListaGrupos(String listaGrupos) {
        this.listaGrupos = listaGrupos;
    }

    public String getMatriculaTecnico() {
        return matriculaTecnico;
    }

    public void setMatriculaTecnico(String matriculaTecnico) {
        this.matriculaTecnico = matriculaTecnico;
    }

    public String getMotivoPrimario() {
        return motivoPrimario;
    }

    public void setMotivoPrimario(String motivoPrimario) {
        this.motivoPrimario = motivoPrimario;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public String getMotivoSecundario() {
        return motivoSecundario;
    }

    public void setMotivoSecundario(String motivoSecundario) {
        this.motivoSecundario = motivoSecundario;
    }

    public String getMotivoTransferencia() {
        return motivoTransferencia;
    }

    public void setMotivoTransferencia(String motivoTransferencia) {
        this.motivoTransferencia = motivoTransferencia;
    }

    public String getMotivoCancelamento() {
        return motivoCancelamento;
    }

    public void setMotivoCancelamento(String motivoCancelamento) {
        this.motivoCancelamento = motivoCancelamento;
    }

    public String getMotivoPendenciamento() {
        return motivoPendenciamento;
    }

    public void setMotivoPendenciamento(String motivoPendenciamento) {
        this.motivoPendenciamento = motivoPendenciamento;
    }

    public String getAnsCriado() {
        return ansCriado;
    }

    public void setAnsCriado(String ansCriado) {
        this.ansCriado = ansCriado;
    }

    public String getUfTarefa() {
        return ufTarefa;
    }

    public void setUfTarefa(String ufTarefa) {
        this.ufTarefa = ufTarefa;
    }

    public String getTaskOrigemWfm() {
        return taskOrigemWfm;
    }

    public void setTaskOrigemWfm(String taskOrigemWfm) {
        this.taskOrigemWfm = taskOrigemWfm;
    }

    public String getTmrTsk() {
        return tmrTsk;
    }

    public void setTmrTsk(String tmrTsk) {
        this.tmrTsk = tmrTsk;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSolucaoFalha() {
        return solucaoFalha;
    }

    public void setSolucaoFalha(String solucaoFalha) {
        this.solucaoFalha = solucaoFalha;
    }

    public String getSolucaoPaliativa() {
        return solucaoPaliativa;
    }

    public void setSolucaoPaliativa(String solucaoPaliativa) {
        this.solucaoPaliativa = solucaoPaliativa;
    }

    public String getSitesDependentesTotalTx() {
        return sitesDependentesTotalTx;
    }

    public void setSitesDependentesTotalTx(String sitesDependentesTotalTx) {
        this.sitesDependentesTotalTx = sitesDependentesTotalTx;
    }

    public String getSolucionadorFalha() {
        return solucionadorFalha;
    }

    public void setSolucionadorFalha(String solucionadorFalha) {
        this.solucionadorFalha = solucionadorFalha;
    }

    public String getStatusAcaoRealizada() {
        return statusAcaoRealizada;
    }

    public void setStatusAcaoRealizada(String statusAcaoRealizada) {
        this.statusAcaoRealizada = statusAcaoRealizada;
    }

    public Long getSitesDependentesPorTx() {
        return sitesDependentesPorTx;
    }

    public void setSitesDependentesPorTx(Long sitesDependentesPorTx) {
        this.sitesDependentesPorTx = sitesDependentesPorTx;
    }

    public String getRotaLink() {
        return rotaLink;
    }

    public void setRotaLink(String rotaLink) {
        this.rotaLink = rotaLink;
    }

    public String getReincidente() {
        return reincidente;
    }

    public void setReincidente(String reincidente) {
        this.reincidente = reincidente;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public String getPrazo() {
        return prazo;
    }

    public void setPrazo(String prazo) {
        this.prazo = prazo;
    }

    public String getNomeTecnicoCampo() {
        return nomeTecnicoCampo;
    }

    public void setNomeTecnicoCampo(String nomeTecnicoCampo) {
        this.nomeTecnicoCampo = nomeTecnicoCampo;
    }

    public String getNomeTecnico() {
        return nomeTecnico;
    }

    public void setNomeTecnico(String nomeTecnico) {
        this.nomeTecnico = nomeTecnico;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getLatitudeEndereco() {
        return latitudeEndereco;
    }

    public void setLatitudeEndereco(String latitudeEndereco) {
        this.latitudeEndereco = latitudeEndereco;
    }

    public String getLongitudeEndereco() {
        return longitudeEndereco;
    }

    public void setLongitudeEndereco(String longitudeEndereco) {
        this.longitudeEndereco = longitudeEndereco;
    }

    public String getTelefoneTecnico() {
        return telefoneTecnico;
    }

    public void setTelefoneTecnico(String telefoneTecnico) {
        this.telefoneTecnico = telefoneTecnico;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getAlarme() {
        return alarme;
    }

    public void setAlarme(String alarme) {
        this.alarme = alarme;
    }

    public String getAlarmeNormalizado() {
        return alarmeNormalizado;
    }

    public void setAlarmeNormalizado(String alarmeNormalizado) {
        this.alarmeNormalizado = alarmeNormalizado;
    }

    public String getEventoMassivo() {
        return eventoMassivo;
    }

    public void setEventoMassivo(String eventoMassivo) {
        this.eventoMassivo = alarmeNormalizado;
    }

    public String getTipoMassiva() {
        return tipoMassiva;
    }

    public void setTipoMassiva(String tipoMassiva) {
        this.tipoMassiva = tipoMassiva;
    }

    public Long getTempoPendenciamento() {
        return tempoPendenciamento;
    }

    public void setTempoPendenciamento(Long tempoPendenciamento) {
        this.tempoPendenciamento = tempoPendenciamento;
    }


}
