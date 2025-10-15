package pt.iscteiul.analyx.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ArtifactController {

	@GetMapping("/hello")
	public String helloWorld() {
		return "Hello World!";
	}
}
