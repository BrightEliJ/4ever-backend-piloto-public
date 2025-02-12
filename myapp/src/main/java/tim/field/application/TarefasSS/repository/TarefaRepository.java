package tim.field.application.TarefasSS.repository;

import java.util.List;

import tim.field.application.TarefasSS.model.Tarefa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TarefaRepository extends JpaRepository<Tarefa, Long> {
        Tarefa findByTask(String task);  // Método existente

        // Método com paginação para retornar todos os campos de tarefa
        @Query(value = "SELECT t.task AS task, " +
                        "t.grupo_acionado AS grupoAcionado, " +
                        "t.status AS status, " +
                        "ROUND(EXTRACT(EPOCH from (t.data_fechamento - t.data_criacao)) / 3600, 2) as downtime, " +
                        "t.data_criacao AS dataCriacao, " +
                        "t.evento_massivo AS eventoMassivo " +
                        "FROM tb_task_ss t " ,
           countQuery = "SELECT COUNT(*) FROM tb_task_ss t",
        nativeQuery = true)
        Page<Object[]> findAllTarefaFields(Pageable pageable);

        // Método com paginação e pesquisa para retornar todos os campos de tarefa
        @Query(value = "SELECT t.task AS task, " +
                        "t.grupo_acionado AS grupoAcionado, " +
                        "t.status AS status, " +
                        "ROUND(extract(EPOCH from (t.data_fechamento - t.data_criacao)) / 3600, 2) as downtime, " +
                        "t.data_criacao AS dataCriacao, " +
                        "t.evento_massivo AS eventoMassivo " +
                        "FROM tb_task_ss t " +
                        "WHERE LOWER(t.task) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "      LOWER(t.grupo_acionado) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "      LOWER(t.status) LIKE LOWER(CONCAT('%', :searchValue, '%'))",
           countQuery = "SELECT COUNT(*) FROM tb_task_ss t " +
                        "WHERE LOWER(t.task) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "      LOWER(t.grupo_acionado) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "      LOWER(t.status) LIKE LOWER(CONCAT('%', :searchValue, '%'))",
        nativeQuery = true)
        Page<Object[]> findAllTarefaFieldsWithSearch(Pageable pageable, @Param("searchValue") String searchValue);

        @Query(value = "SELECT t.task AS task, " +
                        "t.grupo_acionado AS grupoAcionado, " +
                        "t.status AS status, " +
                        "ROUND(EXTRACT(EPOCH from (t.data_fechamento - t.data_criacao)) / 3600, 2) as downtime, " +
                        "t.data_criacao AS dataCriacao, " +
                        "t.evento_massivo AS eventoMassivo " +
                        "FROM tb_task_ss t " +
                        "WHERE (LOWER(t.task) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "       LOWER(t.grupo_acionado) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "       LOWER(t.status) LIKE LOWER(CONCAT('%', :searchValue, '%'))) " +
                        "AND (:startDate IS NULL OR t.data_criacao >= TO_DATE(:startDate, 'DD/MM/YYYY')) " +
                        "AND (:endDate IS NULL OR t.data_criacao <= TO_DATE(:endDate, 'DD/MM/YYYY'))",
           countQuery = "SELECT COUNT(*) FROM tb_task_ss t " +	
                        "WHERE (LOWER(t.task) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "       LOWER(t.grupo_acionado) LIKE LOWER(CONCAT('%', :searchValue, '%')) OR " +
                        "       LOWER(t.status) LIKE LOWER(CONCAT('%', :searchValue, '%'))) " +
                        "AND (:startDate IS NULL OR t.data_criacao >= TO_DATE(:startDate, 'DD/MM/YYYY')) " +
                        "AND (:endDate IS NULL OR t.data_criacao <= TO_DATE(:endDate, 'DD/MM/YYYY'))",
        nativeQuery = true)
        Page<Object[]> findAllTarefaFieldsWithFilters(Pageable pageable, @Param("searchValue") String searchValue,
                                       @Param("startDate") String startDate, @Param("endDate") String endDate);
}
