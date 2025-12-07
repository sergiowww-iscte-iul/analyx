package pt.iscteiul.analyx.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.StringUtils;

public record UserDTO(
		@Size(max = 45)
		@NotNull
		String name,

		@Size(max = 45)
		@NotNull
		String email,

		@Size(max = 45)
		@NotNull
		String password,

		String confirmPassword
) {

	@AssertTrue(message = "Password provided does not confirm")
	public boolean isPasswordCorrect() {
		return StringUtils.equals(password, confirmPassword);
	}
}
