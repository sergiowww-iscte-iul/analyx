package pt.iscteiul.analyx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.analyx.model.Report;
import pt.iscteiul.analyx.model.ReportStatus;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByProjectId(Long projectId);
    List<Report> findByProjectIdOrderByCreatedAtDesc(Long projectId);
    List<Report> findByStatus(ReportStatus status);
}
