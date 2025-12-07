package pt.iscteiul.analyx.util;

import pt.iscteiul.analyx.model.*;

import java.time.LocalDateTime;

/**
 * Helper class for creating test data objects.
 * Provides convenient methods to build User, Project, Report, and Metric instances for testing.
 */
public class TestDataBuilder {

    /**
     * Creates a test User with the given username.
     *
     * @param username the username for the test user
     * @return a User instance with test data
     */
    public static User createUser(String username) {
        User user = new User();
        user.setId(1L);
        user.setUsername(username);
        user.setEmail(username + "@test.com");
        user.setPassword("encodedPassword123");
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Creates a test User with a specific ID.
     *
     * @param id       the ID for the user
     * @param username the username for the test user
     * @return a User instance with test data
     */
    public static User createUser(Long id, String username) {
        User user = createUser(username);
        user.setId(id);
        return user;
    }

    /**
     * Creates a test Project owned by the given user.
     *
     * @param user the owner of the project
     * @param name the project name
     * @return a Project instance with test data
     */
    public static Project createProject(User user, String name) {
        Project project = new Project();
        project.setId(1L);
        project.setUser(user);
        project.setName(name);
        project.setDescription("Test description for " + name);
        project.setCreatedAt(LocalDateTime.now());
        project.setUpdatedAt(LocalDateTime.now());
        return project;
    }

    /**
     * Creates a test Project with a specific ID.
     *
     * @param id   the ID for the project
     * @param user the owner of the project
     * @param name the project name
     * @return a Project instance with test data
     */
    public static Project createProject(Long id, User user, String name) {
        Project project = createProject(user, name);
        project.setId(id);
        return project;
    }

    /**
     * Creates a test Report for the given project with the specified status.
     *
     * @param project the project this report belongs to
     * @param status  the status of the report
     * @return a Report instance with test data
     */
    public static Report createReport(Project project, ReportStatus status) {
        Report report = new Report();
        report.setId(1L);
        report.setProject(project);
        report.setFileName("test-project.zip");
        report.setVersion("1.0");
        report.setStatus(status);
        report.setCreatedAt(LocalDateTime.now());
        if (status == ReportStatus.COMPLETED || status == ReportStatus.FAILED) {
            report.setCompletedAt(LocalDateTime.now());
        }
        return report;
    }

    /**
     * Creates a test Report with a specific ID.
     *
     * @param id      the ID for the report
     * @param project the project this report belongs to
     * @param status  the status of the report
     * @return a Report instance with test data
     */
    public static Report createReport(Long id, Project project, ReportStatus status) {
        Report report = createReport(project, status);
        report.setId(id);
        return report;
    }

    /**
     * Creates a test Metric for the given report with the specified artifact type.
     *
     * @param report       the report this metric belongs to
     * @param artifactType the type of artifact (CLASS, METHOD, PACKAGE)
     * @return a Metric instance with test data
     */
    public static Metric createMetric(Report report, ArtifactType artifactType) {
        Metric metric = new Metric();
        metric.setId(1L);
        metric.setReport(report);
        metric.setArtifactType(artifactType);
        metric.setPackageName("com.example.test");
        metric.setClassName("TestClass");
        metric.setCreatedAt(LocalDateTime.now());

        if (artifactType == ArtifactType.CLASS) {
            metric.setLoc(100);
            metric.setNumMethods(5);
            metric.setNumAttributes(3);
            metric.setCbo(2);
            metric.setDit(1);
            metric.setNoc(0);
        } else if (artifactType == ArtifactType.METHOD) {
            metric.setMethodName("testMethod");
            metric.setLoc(20);
            metric.setCyclomaticComplexity(3);
        }

        return metric;
    }

    /**
     * Creates a test Metric with specific field values.
     *
     * @param report       the report this metric belongs to
     * @param artifactType the type of artifact
     * @param packageName  the package name
     * @param className    the class name
     * @param methodName   the method name (can be null for CLASS type)
     * @return a Metric instance with custom test data
     */
    public static Metric createMetric(Report report, ArtifactType artifactType,
                                      String packageName, String className, String methodName) {
        Metric metric = createMetric(report, artifactType);
        metric.setPackageName(packageName);
        metric.setClassName(className);
        metric.setMethodName(methodName);
        return metric;
    }

    /**
     * Creates a CLASS type Metric with all fields populated.
     *
     * @param report the report this metric belongs to
     * @return a fully populated CLASS Metric
     */
    public static Metric createClassMetric(Report report) {
        Metric metric = new Metric();
        metric.setReport(report);
        metric.setArtifactType(ArtifactType.CLASS);
        metric.setPackageName("com.example.model");
        metric.setClassName("User");
        metric.setLoc(150);
        metric.setNumMethods(10);
        metric.setNumAttributes(5);
        metric.setCbo(3);
        metric.setDit(1);
        metric.setNoc(0);
        metric.setCreatedAt(LocalDateTime.now());
        return metric;
    }

    /**
     * Creates a METHOD type Metric with all relevant fields populated.
     *
     * @param report the report this metric belongs to
     * @return a fully populated METHOD Metric
     */
    public static Metric createMethodMetric(Report report) {
        Metric metric = new Metric();
        metric.setReport(report);
        metric.setArtifactType(ArtifactType.METHOD);
        metric.setPackageName("com.example.service");
        metric.setClassName("UserService");
        metric.setMethodName("getUserById");
        metric.setLoc(25);
        metric.setCyclomaticComplexity(4);
        metric.setCreatedAt(LocalDateTime.now());
        return metric;
    }

    /**
     * Creates a Metric with null values for testing null handling.
     *
     * @param report the report this metric belongs to
     * @return a Metric with minimal data and many null fields
     */
    public static Metric createMetricWithNulls(Report report) {
        Metric metric = new Metric();
        metric.setReport(report);
        metric.setArtifactType(ArtifactType.CLASS);
        metric.setPackageName(null);
        metric.setClassName(null);
        metric.setMethodName(null);
        metric.setLoc(null);
        metric.setNumMethods(null);
        metric.setNumAttributes(null);
        metric.setCyclomaticComplexity(null);
        metric.setCbo(null);
        metric.setDit(null);
        metric.setNoc(null);
        metric.setCreatedAt(LocalDateTime.now());
        return metric;
    }
}
