package jetbrains.buildServer.report.paramsUsage.report;

import jetbrains.buildServer.BuildProject;
import jetbrains.buildServer.report.paramsUsage.report.model.BuildTypeInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ProjectInfo;
import jetbrains.buildServer.serverSide.BuildTypeIdentity;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.WebLinks;

import java.util.Map;
import java.util.stream.Collectors;

public class TeamcityConfigurationProviderImpl implements TeamcityConfigurationProvider {
    private final ProjectManager projectManager;
    private final WebLinks urlResolver;

    public TeamcityConfigurationProviderImpl(ProjectManager projectManager, WebLinks urlResolver) {
        this.projectManager = projectManager;
        this.urlResolver = urlResolver;
    }

    @Override
    public Map<String, ProjectInfo> getProjects() {
        return projectManager.getProjects().stream().collect(Collectors.toMap(
                BuildProject::getProjectId,
                prj -> new ProjectInfo(prj.getName(),
                        urlResolver.getProjectPageUrl(prj.getExternalId()),
                        prj.getConfigurationFile().toPath()) ));
    }

    @Override
    public Map<String, BuildTypeInfo> getBuildTypes() {
        return projectManager.getAllBuildTypes().stream().collect(Collectors.toMap(
                BuildTypeIdentity::getConfigId,
                btype -> new BuildTypeInfo(btype.getName(),
                        urlResolver.getConfigurationHomePageUrl(btype),
                        btype.getConfigurationFile().toPath())
        ));
    }
}
