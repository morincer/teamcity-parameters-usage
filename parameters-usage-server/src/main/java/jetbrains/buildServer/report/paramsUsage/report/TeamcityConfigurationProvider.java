package jetbrains.buildServer.report.paramsUsage.report;

import jetbrains.buildServer.report.paramsUsage.report.model.BuildTypeInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ProjectInfo;

import java.util.Map;

public interface TeamcityConfigurationProvider {
    /**
     * @return Map of project names and their configuration files path'es
     */
    Map<String, ProjectInfo> getProjects();

    /**
     * @return Map of build type names and their configuration path'es
     */
    Map<String, BuildTypeInfo> getBuildTypes();
}
