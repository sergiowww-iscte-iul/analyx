package pt.iscteiul.analyx.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "method_artifact")
@PrimaryKeyJoinColumn(name = "id_method_artifact")
public class MethodArtifact extends Artifact {

	@NotNull
	@Column(name = "cyclomatic_complexity", nullable = false)
	private Integer cyclomaticComplexity;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_class_artifact", nullable = false)
	private ClassArtifact classArtifact;

}