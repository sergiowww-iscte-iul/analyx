package pt.iscteiul.analyx.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "project")
public class Project {
	@Id
	@Column(name = "id_project", nullable = false)
	private Integer id;

	@Size(max = 45)
	@NotNull
	@Column(name = "name", nullable = false, length = 45)
	private String name;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_user", nullable = false)
	private User idUser;

	@NotNull
	@Column(name = "status_analysis", nullable = false)
	private Byte statusAnalysis;

	@NotNull
	@Column(name = "generated_date", nullable = false)
	private Instant generatedDate;

}