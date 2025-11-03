package pt.iscteiul.analyx.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.iscteiul.analyx.entity.Artifact;

import java.util.List;

@RestController
@RequestMapping("/artifact")
public class ArtifactController {

	@GetMapping
	public List<Artifact> listAll() {
		return null;
	}

	@GetMapping("/{id}")
	public Artifact getArtifact(Integer id) {
		return null;
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> removeArtifact(Integer id) {
		return null;
	}

	/**
	 * Atualização total dos dados
	 * @param id
	 * @param artifact
	 * @return
	 */
	@PutMapping("/{id}")
	public Artifact update(Integer id, Artifact artifact) {
		return null;
	}

	/**
	 * Atualização parcial de dados
	 * @param id
	 * @param artifact
	 * @return
	 */
	@PatchMapping("/{id}")
	public Artifact updatePatch(Integer id, Artifact artifact) {
		return null;
	}

	@PostMapping
	public Artifact createArtifact(Artifact artifact) {
		return null;
	}


}
