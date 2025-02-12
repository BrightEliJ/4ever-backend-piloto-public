package tim.field.application.TarefasSS.repository;

import java.util.Optional;
import tim.field.application.TarefasSS.model.TarefaWfm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TarefaWfmRepository extends JpaRepository<TarefaWfm, Long> {
    Optional<TarefaWfm> findByTask(String task);

}
