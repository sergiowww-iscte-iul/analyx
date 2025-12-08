package pt.iscteiul.analyx.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_project", nullable = false)
	private Integer id;

	@Size(max = 45)
	@NotNull
	@Column(name = "name", nullable = false, length = 45)
	private String name;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_user", nullable = false)
	private AppUser user;

	@NotNull
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "status_analysis", nullable = false)
	private StatusAnalysis statusAnalysis;

	@NotNull
	@Column(name = "generated_date", nullable = false)
	private LocalDateTime generatedDate;

	@Size(max = 200)
	@Column(name = "description",  nullable = false, length = 200)
	private String description;


}