package pt.iscteiul.analyx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ArtifactController {

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("hello", "teste");
		return "index";
	}
}
