package pt.iscteiul.analyx.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import pt.iscteiul.analyx.dto.UserDTO;
import pt.iscteiul.analyx.service.UserDetailsServiceImpl;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@GetMapping("/new-user")
	public String newUser() {
		return "sign-up-form";
	}

	@PostMapping("/sign-up")
	public String createUser(@Valid UserDTO userDTO, BindingResult result, RedirectAttributes redirectAttributes) {
		if (!result.hasErrors()) {
//		userDetailsService.createUser()
			return "redirect:/user/login";
		}

		return "sign-up-form";
	}

	@GetMapping("/login")
	public String login() {
		return "login-form";
	}

	@PostMapping("/dologin")
	public String doLogin() {
		return "redirect:/";
	}

	@GetMapping("/logout")
	public String logout() {
		return "redirect:/login";
	}
}
