package jetbrains.buildServer.report.paramsUsage;

import jetbrains.buildServer.report.paramsUsage.report.ParametersUsageReportProvider;
import jetbrains.buildServer.report.paramsUsage.report.TeamcityConfigurationProvider;
import jetbrains.buildServer.report.paramsUsage.report.TeamcityConfigurationProviderImpl;
import jetbrains.buildServer.serverSide.ProjectManager;
import jetbrains.buildServer.serverSide.WebLinks;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import lombok.var;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.xml.parsers.ParserConfigurationException;

@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class PluginConfiguration {
    @Bean
    BuildReportController buildReportController(WebControllerManager controllerManager) throws ParserConfigurationException {
        return new BuildReportController(controllerManager, parametersUsageReportProvider());
    }

    @Bean
    ParametersUsageReportProvider parametersUsageReportProvider() throws ParserConfigurationException {
        return new ParametersUsageReportProvider(teamcityConfigurationProvider(null, null));
    }

    @Bean
    TeamcityConfigurationProvider teamcityConfigurationProvider(ProjectManager projectManager,
                                                                WebLinks webLinks
                                                                ) {

        return new TeamcityConfigurationProviderImpl(projectManager, webLinks);
    }

    @Bean
    ParametersUsageReportPage parametersUsageAdminPage(PagePlaces pagePlaces, PluginDescriptor pluginDescriptor) throws ParserConfigurationException {
        var parametersUsageAdminPage = new ParametersUsageReportPage(pagePlaces, pluginDescriptor, parametersUsageReportProvider());
        parametersUsageAdminPage.register();
        return parametersUsageAdminPage;
    }

}