package pt.iscteiul.analyx.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private Integer id;

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
