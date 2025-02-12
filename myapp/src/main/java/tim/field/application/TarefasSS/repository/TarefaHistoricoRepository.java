package tim.field.application.TarefasSS.repository;

import java.util.Optional;

import tim.field.application.TarefasSS.model.TarefaHistorico;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaHistoricoRepository extends JpaRepository<TarefaHistorico, Long>{
        Optional<TarefaHistorico> findByTask(String task);
}
