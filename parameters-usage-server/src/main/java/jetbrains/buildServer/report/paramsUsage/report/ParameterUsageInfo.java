package jetbrains.buildServer.report.paramsUsage.report;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ParameterUsageInfo {
    String name;
    List<String> definedAtProjects = new ArrayList<>();
    List<String> definedAtBuildConfigurations = new ArrayList<>();
    List<String> usedAtProjects = new ArrayList<>();
    List<String> usedAtBuildConfigurations = new ArrayList<>();

    public ParameterUsageInfo(String name) {
        this.name = name;
    }
}
