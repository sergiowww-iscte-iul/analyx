package pt.iscteiul.analyx.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "artifact")
public class Artifact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idClassMetric", nullable = false)
	private Integer id;

	@Size(max = 100)
	@NotNull(message = "artifact name is required")
	@Column(name = "nmClass", nullable = false, length = 100)
	private String artifactName;

	@NotNull
	@Column(name = "qtLinesOfCode", nullable = false)
	private Integer linesOfCode;

	@Enumerated
	@Column(name = "tpArtifact", columnDefinition = "tinyint not null")
	private TypeArtifact type;

	@Column(name = "qtCoupling")
	private Integer coupling;

}