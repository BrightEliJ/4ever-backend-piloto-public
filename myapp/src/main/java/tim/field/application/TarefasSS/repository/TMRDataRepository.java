package tim.field.application.TarefasSS.repository;

import java.util.Date;
import java.util.List;

import tim.field.application.TarefasSS.model.Tarefa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TMRDataRepository extends JpaRepository<Tarefa, Long> {
    
    @Query(value = "SELECT t.id, t.task, fnc_tmr_calc_noformat(t.task) AS calculatedValue, " +
                   "t.grupo_acionado AS grupo, t.status, t.data_criacao, t.data_fechamento " +
                   "FROM tb_task_ss t " +
                   "WHERE t.grupo_acionado = :grupo " +
                   "AND t.status IN (:statusList) " +
                   "AND t.data_criacao BETWEEN :startDate AND :endDate " +
                   "ORDER BY t.data_criacao", nativeQuery = true)
    List<Object[]> findByFiltersWithFunction(@Param("grupo") String grupo,
                                             @Param("statusList") List<String> statusList,
                                             @Param("startDate") Date startDate,
                                             @Param("endDate") Date endDate);
    
    //Busca TMR por tarefa
    @Query(value = "SELECT fnc_tmr_calc(:task)" ,
    nativeQuery = true)
        String findByTask(@Param("task") String task);
}
