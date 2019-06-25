package jetbrains.buildServer.report.paramsUsage.report.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ParameterUsageInfo {
    String name;
    List<ProjectInfo> definedAtProjects = new ArrayList<>();
    List<BuildTypeInfo> definedAtBuildConfigurations = new ArrayList<>();
    List<ProjectInfo> usedAtProjects = new ArrayList<>();
    List<BuildTypeInfo> usedAtBuildConfigurations = new ArrayList<>();

    public ParameterUsageInfo(String name) {
        this.name = name;
    }
}
