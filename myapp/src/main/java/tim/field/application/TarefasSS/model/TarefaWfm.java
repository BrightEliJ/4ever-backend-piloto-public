package tim.field.application.TarefasSS.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tb_task_wfm")
public class TarefaWfm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "bsc_rnc")
    private String bscRnc;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "classificacao_gsbi")
    private String classificacaoGsbi;

    @Column(name = "cm")
    private String cm;

    @Column(name = "contrato")
    private String contrato;

    @Column(name = "criacao_ntt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date criacaoNtt;

    @Column(name = "data")
    @Temporal(TemporalType.TIMESTAMP)
    private Date data;

    @Column(name = "data_primeira_roteirizacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataPrimeiraRoteirizacao;

    @Column(name = "data_ultima_roteirizacao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataUltimaRoteirizacao;

    @Column(name = "descricao_gmg", columnDefinition = "TEXT")
    private String descricaoGmg;

    @Column(name = "empresa")
    private String empresa;

    @Column(name = "end_id")
    private String endId;

    @Column(name = "estado")
    private String estado;

    @Column(name = "eta")
    private String eta;

    @Column(name = "evento")
    private String evento;

    @Column(name = "fim")
    @Temporal(TemporalType.TIMESTAMP)
    private Date fim;

    @Column(name = "funcao_equipamento")
    private String funcaoEquipamento;

    @Column(name = "grupo")
    private String grupo;

    @Column(name = "hora_criacao_atividade")
    @Temporal(TemporalType.TIMESTAMP)
    private Date horaCriacaoAtividade;

    @Column(name = "id_atividade")
    private Long idAtividade;

    @Column(name = "id_ticket_ca")
    private String idTicketCa;

    @Column(name = "inicio_gmg")
    @Temporal(TemporalType.TIMESTAMP)
    private Date inicioGmg;

    @Column(name = "matricula_provedor")
    private String matriculaProvedor;

    @Column(name = "motivo_suspensao", columnDefinition = "TEXT")
    private String motivoSuspensao;

    @Column(name = "motivo_tramitacao", columnDefinition = "TEXT")
    private String motivoTramitacao;

    @Column(name = "motivo_pendenciamento", columnDefinition = "TEXT")
    private String motivoPendenciamento;

    @Column(name = "ne_id")
    private String neId;

    @Column(name = "nota_abertura", columnDefinition = "TEXT")
    private String notaAbertura;

    @Column(name = "task")
    private String task;

    @Column(name = "resolucao_problema", columnDefinition = "TEXT")
    private String resolucaoProblema;

    @Column(name = "local_problema", columnDefinition = "TEXT")
    private String localProblema;

    @Column(name = "operadora")
    private String operadora;

    @Column(name = "predios_industriais")
    private String prediosIndustriais;

    @Column(name = "prioridade")
    private String prioridade;

    @Column(name = "priorizacao_dispatching")
    private Long priorizacaoDispatching;

    @Column(name = "priorizacao_dispatching_classific")
    private String priorizacaoDispatchingClassific;

    @Column(name = "provedor")
    private String provedor;

    @Column(name = "causa_falha_elemento", columnDefinition = "TEXT")
    private String causaFalhaElemento;

    @Column(name = "regional")
    private String regional;

    @Column(name = "regra_usuario_criador")
    private String regraUsuarioCriador;

    @Column(name = "repetido")
    private String repetido;

    @Column(name = "responsabilidade")
    private String responsabilidade;

    @Column(name = "responsavel_gmg")
    private String responsavelGmg;

    @Column(name = "seguimento_rede_equipamento")
    private String seguimentoRedeEquipamento;

    @Column(name = "status_gmg")
    private String statusGmg;

    @Column(name = "sub_area")
    private String subArea;

    @Column(name = "termino_gmg")
    @Temporal(TemporalType.TIMESTAMP)
    private Date terminoGmg;

    @Column(name = "tipo_contrato")
    private String tipoContrato;

    @Column(name = "tipo_atividade")
    private String tipoAtividade;

    @Column(name = "tipo_falha")
    private String tipoFalha;

    @Column(name = "tipo_ne")
    private String tipoNe;

    @Column(name = "titulo_alarme")
    private String tituloAlarme;

    @Column(name = "tramitacao_suspensao")
    private String tramitacaoSuspensao;

    @Column(name = "uf")
    private String uf;

    @Column(name = "usuario_executor")
    private String usuarioExecutor;

    @Column(name = "workzone")
    private String workzone;

    @Column(name = "workzone_end_id")
    private String workzoneEndId;

    @Column(name = "data_coleta")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataColeta;

    @Column(name = "base_origem")
    private String baseOrigem;

    @Column(name = "data_reg")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataReg;

    // Getters e Setters

    public Date getDataReg() {
        return dataReg;
    }

    public void setDataReg(Date dataReg) {
        this.dataReg = dataReg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBscRnc() {
        return bscRnc;
    }

    public void setBscRnc(String bscRnc) {
        this.bscRnc = bscRnc;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getClassificacaoGsbi() {
        return classificacaoGsbi;
    }

    public void setClassificacaoGsbi(String classificacaoGsbi) {
        this.classificacaoGsbi = classificacaoGsbi;
    }

    public String getCm() {
        return cm;
    }

    public void setCm(String cm) {
        this.cm = cm;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public Date getCriacaoNtt() {
        return criacaoNtt;
    }

    public void setCriacaoNtt(Date criacaoNtt) {
        this.criacaoNtt = criacaoNtt;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public Date getDataPrimeiraRoteirizacao() {
        return dataPrimeiraRoteirizacao;
    }

    public void setDataPrimeiraRoteirizacao(Date dataPrimeiraRoteirizacao) {
        this.dataPrimeiraRoteirizacao = dataPrimeiraRoteirizacao;
    }

    public Date getDataUltimaRoteirizacao() {
        return dataUltimaRoteirizacao;
    }

    public void setDataUltimaRoteirizacao(Date dataUltimaRoteirizacao) {
        this.dataUltimaRoteirizacao = dataUltimaRoteirizacao;
    }

    public String getDescricaoGmg() {
        return descricaoGmg;
    }

    public void setDescricaoGmg(String descricaoGmg) {
        this.descricaoGmg = descricaoGmg;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getEndId() {
        return endId;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getEvento() {
        return evento;
    }

    public void setEvento(String evento) {
        this.evento = evento;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getFuncaoEquipamento() {
        return funcaoEquipamento;
    }

    public void setFuncaoEquipamento(String funcaoEquipamento) {
        this.funcaoEquipamento = funcaoEquipamento;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public Date getHoraCriacaoAtividade() {
        return horaCriacaoAtividade;
    }

    public void setHoraCriacaoAtividade(Date horaCriacaoAtividade) {
        this.horaCriacaoAtividade = horaCriacaoAtividade;
    }

    public Long getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(Long idAtividade) {
        this.idAtividade = idAtividade;
    }

    public String getIdTicketCa() {
        return idTicketCa;
    }

    public void setIdTicketCa(String idTicketCa) {
        this.idTicketCa = idTicketCa;
    }

    public Date getInicioGmg() {
        return inicioGmg;
    }

    public void setInicioGmg(Date inicioGmg) {
        this.inicioGmg = inicioGmg;
    }

    public String getMatriculaProvedor() {
        return matriculaProvedor;
    }

    public void setMatriculaProvedor(String matriculaProvedor) {
        this.matriculaProvedor = matriculaProvedor;
    }

    public String getMotivoSuspensao() {
        return motivoSuspensao;
    }

    public void setMotivoSuspensao(String motivoSuspensao) {
        this.motivoSuspensao = motivoSuspensao;
    }

    public String getMotivoTramitacao() {
        return motivoTramitacao;
    }

    public void setMotivoTramitacao(String motivoTramitacao) {
        this.motivoTramitacao = motivoTramitacao;
    }

    public String getMotivoPendenciamento() {
        return motivoPendenciamento;
    }

    public void setMotivoPendenciamento(String motivoPendenciamento) {
        this.motivoPendenciamento = motivoPendenciamento;
    }

    public String getNeId() {
        return neId;
    }

    public void setNeId(String neId) {
        this.neId = neId;
    }

    public String getNotaAbertura() {
        return notaAbertura;
    }

    public void setNotaAbertura(String notaAbertura) {
        this.notaAbertura = notaAbertura;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public String getResolucaoProblema() {
        return resolucaoProblema;
    }

    public void setResolucaoProblema(String resolucaoProblema) {
        this.resolucaoProblema = resolucaoProblema;
    }

    public String getLocalProblema() {
        return localProblema;
    }

    public void setLocalProblema(String localProblema) {
        this.localProblema = localProblema;
    }

    public String getOperadora() {
        return operadora;
    }

    public void setOperadora(String operadora) {
        this.operadora = operadora;
    }

    public String getPrediosIndustriais() {
        return prediosIndustriais;
    }

    public void setPrediosIndustriais(String prediosIndustriais) {
        this.prediosIndustriais = prediosIndustriais;
    }

    public String getPrioridade() {
        return prioridade;
    }

    public void setPrioridade(String prioridade) {
        this.prioridade = prioridade;
    }

    public Long getPriorizacaoDispatching() {
        return priorizacaoDispatching;
    }

    public void setPriorizacaoDispatching(Long priorizacaoDispatching) {
        this.priorizacaoDispatching = priorizacaoDispatching;
    }

    public String getPriorizacaoDispatchingClassific() {
        return priorizacaoDispatchingClassific;
    }

    public void setPriorizacaoDispatchingClassific(String priorizacaoDispatchingClassific) {
        this.priorizacaoDispatchingClassific = priorizacaoDispatchingClassific;
    }

    public String getProvedor() {
        return provedor;
    }

    public void setProvedor(String provedor) {
        this.provedor = provedor;
    }

    public String getCausaFalhaElemento() {
        return causaFalhaElemento;
    }

    public void setCausaFalhaElemento(String causaFalhaElemento) {
        this.causaFalhaElemento = causaFalhaElemento;
    }

    public String getRegional() {
        return regional;
    }

    public void setRegional(String regional) {
        this.regional = regional;
    }

    public String getRegraUsuarioCriador() {
        return regraUsuarioCriador;
    }

    public void setRegraUsuarioCriador(String regraUsuarioCriador) {
        this.regraUsuarioCriador = regraUsuarioCriador;
    }

    public String getRepetido() {
        return repetido;
    }

    public void setRepetido(String repetido) {
        this.repetido = repetido;
    }

    public String getResponsabilidade() {
        return responsabilidade;
    }

    public void setResponsabilidade(String responsabilidade) {
        this.responsabilidade = responsabilidade;
    }

    public String getResponsavelGmg() {
        return responsavelGmg;
    }

    public void setResponsavelGmg(String responsavelGmg) {
        this.responsavelGmg = responsavelGmg;
    }

    public String getSeguimentoRedeEquipamento() {
        return seguimentoRedeEquipamento;
    }

    public void setSeguimentoRedeEquipamento(String seguimentoRedeEquipamento) {
        this.seguimentoRedeEquipamento = seguimentoRedeEquipamento;
    }

    public String getStatusGmg() {
        return statusGmg;
    }

    public void setStatusGmg(String statusGmg) {
        this.statusGmg = statusGmg;
    }

    public String getSubArea() {
        return subArea;
    }

    public void setSubArea(String subArea) {
        this.subArea = subArea;
    }

    public Date getTerminoGmg() {
        return terminoGmg;
    }

    public void setTerminoGmg(Date terminoGmg) {
        this.terminoGmg = terminoGmg;
    }

    public String getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(String tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public String getTipoAtividade() {
        return tipoAtividade;
    }

    public void setTipoAtividade(String tipoAtividade) {
        this.tipoAtividade = tipoAtividade;
    }

    public String getTipoFalha() {
        return tipoFalha;
    }

    public void setTipoFalha(String tipoFalha) {
        this.tipoFalha = tipoFalha;
    }

    public String getTipoNe() {
        return tipoNe;
    }

    public void setTipoNe(String tipoNe) {
        this.tipoNe = tipoNe;
    }

    public String getTituloAlarme() {
        return tituloAlarme;
    }

    public void setTituloAlarme(String tituloAlarme) {
        this.tituloAlarme = tituloAlarme;
    }

    public String getTramitacaoSuspensao() {
        return tramitacaoSuspensao;
    }

    public void setTramitacaoSuspensao(String tramitacaoSuspensao) {
        this.tramitacaoSuspensao = tramitacaoSuspensao;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getUsuario() {
        return usuarioExecutor;
    }

    public void setUsuarioExecutor(String usuarioExecutor) {
        this.usuarioExecutor = usuarioExecutor;
    }

    public String getWorkzone() {
        return workzone;
    }

    public void setWorkzone(String workzone) {
        this.workzone = workzone;
    }

    public String getWorkzoneEndId() {
        return workzoneEndId;
    }

    public void setWorkzoneEndId(String workzoneEndId) {
        this.workzoneEndId = workzoneEndId;
    }

    public Date getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(Date dataColeta) {
        this.dataColeta = dataColeta;
    }

    public String getBaseOrigem() {
        return baseOrigem;
    }

    public void setBaseOrigem(String baseOrigem) {
        this.baseOrigem = baseOrigem;
    }
}
