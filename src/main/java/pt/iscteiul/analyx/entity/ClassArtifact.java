package pt.iscteiul.analyx.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "class_artifact")
@PrimaryKeyJoinColumn(name = "id_class_artifact")
public class ClassArtifact extends Artifact {

	@NotNull
	@Column(name = "number_attributes", nullable = false)
	private Integer numberAttributes;

	@NotNull
	@Column(name = "dit", nullable = false)
	private Integer dit;

	@NotNull
	@Column(name = "cbo", nullable = false)
	private Integer cbo;

	@NotNull
	@Column(name = "noc", nullable = false)
	private Integer noc;

}