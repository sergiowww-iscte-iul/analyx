package pt.iscteiul.analyx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pt.iscteiul.analyx.dto.ProjectDTO;
import pt.iscteiul.analyx.entity.Project;
import pt.iscteiul.analyx.entity.StatusAnalysis;
import pt.iscteiul.analyx.exception.BusinessException;
import pt.iscteiul.analyx.repository.ProjectRepository;
import pt.iscteiul.analyx.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Supplier;

import static java.util.Objects.nonNull;

@Service
public class ProjectService {

	@Autowired
	private WorkspaceService workspaceService;

	@Autowired
	private ProjectRepository projectRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ProjectBatchServiceManager projectBatchServiceManager;

	@Autowired
	private ArtifactService artifactService;

	public List<Project> findAllByUserName(String userName) {
		return projectRepository.findByUser_Name(userName);
	}

	@Transactional
	public Project saveProject(ProjectDTO projectDTO, Authentication auth) {
		Project project;
		if (nonNull(projectDTO.id())) {
			project = projectRepository.findByIdAndUser_Name(projectDTO.id(), auth.getName())
					.orElseThrow(throwBusinessException(projectDTO.id()));
		} else {
			project = new Project();
			project.setUser(userRepository.findByName(auth.getName()).orElseThrow());
		}
		project.setGeneratedDate(LocalDateTime.now());

		project.setDescription(projectDTO.description());
		project.setName(projectDTO.name());
		project.setStatusAnalysis(StatusAnalysis.ADDED);

		projectRepository.save(project);

		return project;
	}

	@Async
	public void startAnalysis(Project project, MultipartFile projectFiles) throws Exception {
		if (projectFiles.isEmpty()) {
			throw new BusinessException("Project files cannot be empty");
		}
		uploadProject(project, projectFiles);


		Integer idProject = project.getId();
		projectBatchServiceManager.startAnalysis(idProject);
	}

	public void uploadProject(Project project, MultipartFile projectFiles) {
		if (!projectFiles.isEmpty()) {
			workspaceService.moveUploadedFileToWorkspace(project, projectFiles);
		}
	}


	public Project getProjectById(Long idProject) {
		return projectRepository.findById(idProject.intValue())
				.orElseThrow(() -> new BusinessException("Project %d not found.".formatted(idProject)));
	}

	public ProjectDTO getProjectForEditing(Integer idProject) {

		return projectRepository.findById(idProject)
				.map(p -> ProjectDTO.builder()
						.id(p.getId())
						.description(p.getDescription())
						.name(p.getName())
						.build()
				)
				.orElseThrow(throwBusinessException(idProject));
	}

	private static Supplier<BusinessException> throwBusinessException(Integer idProject) {
		return () -> new BusinessException("Project %d not found".formatted(idProject));
	}

	@Transactional
	public void delete(Integer idProject) {
		projectRepository.findById(idProject)
				.map(workspaceService::deleteProjectFiles)
				.map(artifactService::deleteByProject)
				.ifPresent(projectRepository::delete);

	}

	@Transactional
	public void markProjectAsAnalysisStarted(Long idProject) {
		updateStatus(idProject, StatusAnalysis.PROCESSING_FILES);
	}

	private void updateStatus(Long idProject, StatusAnalysis status) {
		Project project = getProjectById(idProject);
		project.setStatusAnalysis(status);
		projectRepository.save(project);
	}

	@Transactional
	public void markProjectAsFailed(Long idProject) {
		updateStatus(idProject, StatusAnalysis.ERROR);
	}

	@Transactional
	public void markProjectAsAnalysisFinished(Long idProject) {
		updateStatus(idProject, StatusAnalysis.FINISHED);
	}
}
