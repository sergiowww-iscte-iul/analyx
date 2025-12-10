package pt.iscteiul.analyx.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.iscteiul.analyx.dto.UserDTO;
import pt.iscteiul.analyx.service.UserDetailsServiceImpl;
import pt.iscteiul.analyx.util.ControllerKeys;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@GetMapping("/new-user")
	public String newUser(Model model) {
		model.addAttribute("userDTO", new UserDTO());
		return "sign-up";
	}

	@GetMapping("/profile")
	public String profile(Model model, Authentication auth) {
		model.addAttribute("userDTO", userDetailsService.getUserByName(auth.getName()));
		return "sign-up";
	}

	@PostMapping("/sign-up")
	public String createUser(@ModelAttribute("userDTO") @Valid UserDTO userDTO, BindingResult result, RedirectAttributes redirectAttributes) {
		if (!result.hasErrors()) {
			redirectAttributes.addFlashAttribute(ControllerKeys.INFO_MESSAGE, "User created successfully");
			userDetailsService.createUser(userDTO);
			return "redirect:/user/login";
		}

		return "sign-up";
	}

	@PostMapping("/update")
	public String updateUser(
			@ModelAttribute("userDTO")
			@Valid
			UserDTO userDTO,
			BindingResult result,
			RedirectAttributes redirectAttributes,
			Authentication auth) {
		if (!result.hasErrors()) {
			redirectAttributes.addFlashAttribute(ControllerKeys.INFO_MESSAGE, "User updated successfully, please logout to see the changes");
			userDetailsService.updateUser(userDTO, auth.getName());
			return "redirect:/";
		}
		return "sign-up";
	}

	@GetMapping("/login")
	public String login(@RequestParam(value = "error", required = false) String error,
						@RequestParam(value = "logout", required = false) String logout,
						Model model) {

		if (error != null) {
			model.addAttribute(ControllerKeys.ERROR_MESSAGE, "Invalid username or password");
		}

		if (logout != null) {
			model.addAttribute(ControllerKeys.INFO_MESSAGE, "You have been logged out successfully.");
		}
		return "login";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/login";
	}
}
