package pt.iscteiul.analyx.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id_user", nullable = false)
	private Integer id;

	@Size(max = 45)
	@NotNull
	@Column(name = "name", nullable = false, length = 45)
	private String name;

	@Size(max = 45)
	@NotNull
	@Column(name = "email", nullable = false, length = 45)
	private String email;

	@Size(max = 45)
	@NotNull
	@Column(name = "password", nullable = false, length = 45)
	private String password;

}