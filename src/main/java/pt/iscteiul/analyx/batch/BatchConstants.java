package pt.iscteiul.analyx.batch;

import lombok.experimental.UtilityClass;

@UtilityClass
public class BatchConstants {
	public static final String JOB_PROCESS_PROJECT = "jobProcessProject";
	public static final String JOB_PROCESS_PROJECT_RESTART = "jobProcessProjectRestart";
	public static final String PARAM_ID_PROJECT = "idProject";
	public static final String PARAM_ID_PROJECT_EXPR = "#{jobParameters['" + PARAM_ID_PROJECT + "']}";
}
