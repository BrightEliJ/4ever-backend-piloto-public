package tim.field.application.logging.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tim.field.application.logging.model.Log;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    // Custom queries for specific log types or filters can be added here
}
