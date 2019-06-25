package jetbrains.buildServer.report.paramsUsage.report;

import jetbrains.buildServer.report.paramsUsage.report.model.BuildTypeInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ProjectInfo;
import lombok.var;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

public class ParametersUsageReportProviderTest {
    private ParametersUsageReportProvider provider;

    @Before
    public void setUp() throws Exception {
        var baseDir = Paths.get("src/test/resources").toAbsolutePath();

        assertThat(baseDir.toFile().exists(), is(true));

        TeamcityConfigurationProvider configurationProvider = new TeamcityConfigurationProvider() {
            @Override
            public Map<String, ProjectInfo> getProjects() {
                try {
                    return Files.walk(baseDir)
                            .filter(path -> path.getFileName().toString().equals("project-config.xml"))
                            .collect(Collectors.toMap(
                                    path -> path.getParent().getFileName().toString(),
                                    path -> new ProjectInfo(path.getFileName().toString(), path.toString(), path.toAbsolutePath())));
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            public Map<String, BuildTypeInfo> getBuildTypes() {
                try {
                    return Files.walk(baseDir)
                            .filter(path -> path.getFileName().toString().endsWith("BuildConfiguration.xml"))
                            .collect(Collectors.toMap(
                                    path -> path.getFileName().toString(),
                                    path -> new BuildTypeInfo(path.getFileName().toString(), path.toString(), path.toAbsolutePath())));
                } catch (IOException e) {
                    return null;
                }
            }
        };

        assertFalse(configurationProvider.getBuildTypes().isEmpty());
        assertFalse(configurationProvider.getProjects().isEmpty());

        this.provider = new ParametersUsageReportProvider(configurationProvider);
    }

    @Test
    public void itBuildsNonEmptyUsageReport() {
        var report = this.provider.buildReport();
        assertFalse(report.getParametersUsage().isEmpty());
    }

    @Test
    public void isFindsUsagesAndDefinitionsOfParameters() {
        var report = this.provider.buildReport();

        var params = report.getParametersUsage().stream().collect(Collectors.toMap(i -> i.getName(), i -> i));

        String prjParamName = "tellMeWhoAmI";

        assertTrue(params.containsKey(prjParamName));
        assertThat(params.get(prjParamName).getDefinedAtProjects().get(0), equalTo("MyTestProject"));
        assertThat(params.get(prjParamName).getUsedAtBuildConfigurations().get(0), equalTo("MyTestProject_MyTestBuildConfiguration.xml"));

        String btParamName = "MyBCParam";
        assertTrue(params.containsKey(btParamName));
        assertThat(params.get(btParamName).getDefinedAtBuildConfigurations().get(0), equalTo("MyTestProject_MyTestBuildConfiguration.xml"));

    }
}