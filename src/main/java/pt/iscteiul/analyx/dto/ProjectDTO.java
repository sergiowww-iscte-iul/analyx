package pt.iscteiul.analyx.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import org.springframework.web.bind.annotation.RequestPart;

@Builder
public record ProjectDTO(
		@Size(max = 200)
		@RequestPart("description")
		String description,

		@Size(max = 45)
		@NotEmpty
		@RequestPart("name")
		String name,

		@RequestPart("id")
		Integer id
) {
}
