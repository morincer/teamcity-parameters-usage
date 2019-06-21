package jetbrains.buildServer.report.paramsUsage.report;

import java.nio.file.Path;
import java.util.Map;

public interface TeamcityConfigurationProvider {
    /**
     * @return Map of project names and their configuration files path'es
     */
    Map<String, Path> getProjectConfigurationFiles();

    /**
     * @return Map of build type names and their configuration path'es
     */
    Map<String, Path> getBuildTypesConfigurationFiles();
}
