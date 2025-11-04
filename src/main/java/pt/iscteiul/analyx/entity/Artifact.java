package pt.iscteiul.analyx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "artifact")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Artifact {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_artifact", nullable = false)
	private Integer id;

	@Size(max = 45)
	@NotNull
	@Column(name = "name", nullable = false, length = 45)
	private String name;

	@NotNull
	@Column(name = "lines_code", nullable = false)
	private Integer linesCode;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_project", nullable = false)
	private Project project;

}