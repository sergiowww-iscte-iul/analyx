package pt.iscteiul.analyx.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.iscteiul.analyx.model.ArtifactType;
import pt.iscteiul.analyx.model.Metric;

import java.util.List;

public interface MetricRepository extends JpaRepository<Metric, Long> {
    List<Metric> findByReportId(Long reportId);
    List<Metric> findByReportIdAndArtifactType(Long reportId, ArtifactType artifactType);
    List<Metric> findByReportIdAndPackageName(Long reportId, String packageName);
    List<Metric> findByReportIdAndClassName(Long reportId, String className);
}
