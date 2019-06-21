package jetbrains.buildServer.report.paramsUsage.report;

import lombok.var;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
            public Map<String, Path> getProjectConfigurationFiles() {
                try {
                    return Files.walk(baseDir)
                            .filter(path -> path.getFileName().toString().equals("project-config.xml"))
                            .collect(Collectors.toMap(
                                    path -> path.getParent().getFileName().toString(),
                                    Path::toAbsolutePath));
                } catch (IOException e) {
                    return null;
                }
            }

            @Override
            public Map<String, Path> getBuildTypesConfigurationFiles() {
                try {
                    return Files.walk(baseDir)
                            .filter(path -> path.getFileName().toString().endsWith("BuildConfiguration.xml"))
                            .collect(Collectors.toMap(
                                    path -> path.getFileName().toString(),
                                    Path::toAbsolutePath));
                } catch (IOException e) {
                    return null;
                }
            }
        };

        assertFalse(configurationProvider.getBuildTypesConfigurationFiles().isEmpty());
        assertFalse(configurationProvider.getProjectConfigurationFiles().isEmpty());

        this.provider = new ParametersUsageReportProvider(configurationProvider);
    }

    @Test
    public void itBuildsNonEmptyUsageReport() {
        var report = this.provider.buildReport();
        assertFalse(report.parametersUsage.isEmpty());
    }

    @Test
    public void isFindsUsagesAndDefinitionsOfParameters() {
        var report = this.provider.buildReport();

        var params = report.parametersUsage.stream().collect(Collectors.toMap(i -> i.name, i -> i));

        String prjParamName = "tellMeWhoAmI";

        assertTrue(params.containsKey(prjParamName));
        assertThat(params.get(prjParamName).definedAtProjects.get(0), equalTo("MyTestProject"));
        assertThat(params.get(prjParamName).usedAtBuildConfigurations.get(0), equalTo("MyTestProject_MyTestBuildConfiguration.xml"));

        String btParamName = "MyBCParam";
        assertTrue(params.containsKey(btParamName));
        assertThat(params.get(btParamName).definedAtBuildConfigurations.get(0), equalTo("MyTestProject_MyTestBuildConfiguration.xml"));

    }
}