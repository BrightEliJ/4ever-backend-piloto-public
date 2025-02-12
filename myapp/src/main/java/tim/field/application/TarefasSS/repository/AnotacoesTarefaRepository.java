package tim.field.application.TarefasSS.repository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import tim.field.application.TarefasSS.dto.AnotacoesTarefaDTO;

@Repository
public class AnotacoesTarefaRepository {

    @PersistenceContext
    private EntityManager entityManager;        

    @Transactional
    public List<AnotacoesTarefaDTO> findAnotacoesByTask(String task) {
        
        // Executa a consulta nativa usando a função pipelined
        @SuppressWarnings("unchecked")
        List<Object[]> results = entityManager.createNativeQuery(
            "SELECT id_anotacao, " +
            "anot.tipo_anotacao AS tipoAnotacao, " +
            "anot.anotacao AS anotacao, " +
            "anot.usuario_anotacao AS usuarioAnotacao, " +
            "anot.data_anotacao AS dataAnotacao " +
            "FROM public.fn_insere_anotacoes(:task) anot"
        )
        .setParameter("task", task)
        .getResultList();

        // Mapeia o resultado para o DTO
        return results.stream().map(obj -> {
            Long id = (Long) obj[0]; 
            String tipoAnotacao = (String) obj[1];
            String anotacao = (String) obj[2];
            String usuarioAnotacao = (String) obj[3];
            Date dataAnotacao = (Date) obj[4];

            return new AnotacoesTarefaDTO(id, task, tipoAnotacao, anotacao, usuarioAnotacao, dataAnotacao);
        }).collect(Collectors.toList());
    }
}