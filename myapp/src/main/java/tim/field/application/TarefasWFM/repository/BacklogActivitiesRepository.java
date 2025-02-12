package tim.field.application.TarefasWFM.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tim.field.application.TarefasWFM.model.BacklogActivities;

public interface BacklogActivitiesRepository extends JpaRepository<BacklogActivities, Long> {

    @Query(value = "SELECT b.\"apptNumber\", b.\"XA_PI_EVENT\", b.\"XA_PI_CREATE_DATE\", " +
            "b.status, b.regional, b.company, b.\"resourceId\", b.\"activityType\" " +
            "FROM backlog_activities b " +
            "WHERE (:search IS NULL OR " +
            "b.\"apptNumber\" ILIKE CONCAT('%', :search, '%') OR " +
            "b.status ILIKE CONCAT('%', :search, '%') OR " +
            "b.regional ILIKE CONCAT('%', :search, '%') OR " +
            "b.company ILIKE CONCAT('%', :search, '%')) " +
            "AND (:startDate IS NULL OR b.\"XA_PI_CREATE_DATE\" >= TO_DATE(:startDate, 'YYYY-MM-DD')) " +
            "AND (:endDate IS NULL OR b.\"XA_PI_CREATE_DATE\" <= TO_DATE(:endDate, 'YYYY-MM-DD')) " +
            "ORDER BY b.\"XA_PI_CREATE_DATE\" DESC",
        countQuery = "SELECT COUNT(*) FROM backlog_activities b " +
            "WHERE (:search IS NULL OR " +
            "b.\"apptNumber\" ILIKE CONCAT('%', :search, '%') OR " +
            "b.status ILIKE CONCAT('%', :search, '%') OR " +
            "b.regional ILIKE CONCAT('%', :search, '%') OR " +
            "b.company ILIKE CONCAT('%', :search, '%')) " +
            "AND (:startDate IS NULL OR b.\"XA_PI_CREATE_DATE\" >= TO_DATE(:startDate, 'YYYY-MM-DD')) " +
            "AND (:endDate IS NULL OR b.\"XA_PI_CREATE_DATE\" <= TO_DATE(:endDate, 'YYYY-MM-DD'))",
        nativeQuery = true)
    Page<Object[]> findTarefas(Pageable pageable, @Param("search") String search, 
                               @Param("startDate") String startDate, 
                               @Param("endDate") String endDate);

    @Query(value = "SELECT COUNT(*) FROM backlog_activities b " +
            "WHERE (:search IS NULL OR " +
            "b.\"apptNumber\" ILIKE CONCAT('%', :search, '%') OR " +
            "b.status ILIKE CONCAT('%', :search, '%') OR " +
            "b.regional ILIKE CONCAT('%', :search, '%') OR " +
            "b.company ILIKE CONCAT('%', :search, '%')) " +
            "AND (:startDate IS NULL OR b.\"XA_PI_CREATE_DATE\" >= TO_DATE(:startDate, 'YYYY-MM-DD')) " +
            "AND (:endDate IS NULL OR b.\"XA_PI_CREATE_DATE\" <= TO_DATE(:endDate, 'YYYY-MM-DD'))",
        nativeQuery = true)
    long countTarefas(@Param("search") String search, 
                      @Param("startDate") String startDate, 
                      @Param("endDate") String endDate);
}