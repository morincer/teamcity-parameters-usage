package jetbrains.buildServer.report.paramsUsage;

import jetbrains.buildServer.BuildProject;
import jetbrains.buildServer.report.paramsUsage.report.ParametersUsageReportProvider;
import jetbrains.buildServer.report.paramsUsage.report.TeamcityConfigurationProvider;
import jetbrains.buildServer.serverSide.BuildTypeIdentity;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.parsers.ParserConfigurationException;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class PluginConfiguration {
    @Bean
    BuildReportController buildReportController(WebControllerManager controllerManager) throws ParserConfigurationException {
        return new BuildReportController(controllerManager, parametersUsageReportProvider());
    }

    @Bean
    ParametersUsageReportProvider parametersUsageReportProvider() throws ParserConfigurationException {
        return new ParametersUsageReportProvider(teamcityConfigurationProvider(null));
    }

    @Bean
    TeamcityConfigurationProvider teamcityConfigurationProvider(ProjectManager projectManager) {
        return new TeamcityConfigurationProvider() {
            @Override
            public Map<String, Path> getProjectConfigurationFiles() {
                return projectManager.getProjects().stream().collect(Collectors.toMap(
                        BuildProject::getName,
                        prj -> prj.getConfigurationFile().toPath()));
            }

            @Override
            public Map<String, Path> getBuildTypesConfigurationFiles() {
                return projectManager.getAllBuildTypes().stream().collect(Collectors.toMap(
                        BuildTypeIdentity::getName,
                        btype -> btype.getConfigurationFile().toPath()
                ));
            }
        };
    }
}