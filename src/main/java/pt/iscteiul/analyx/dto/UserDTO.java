package pt.iscteiul.analyx.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class UserDTO {
	@Size(max = 45)
	@NotEmpty
	private String name;

	@Size(max = 45)
	@NotEmpty
	@Email
	private String email;

	@Size(max = 45, min = 5)
	@NotEmpty
	private String password;

	@NotEmpty
	private String confirmPassword;

	@AssertTrue(message = "Password provided does not confirm")
	public boolean isPasswordCorrect() {
		return StringUtils.equals(password, confirmPassword);
	}
}
