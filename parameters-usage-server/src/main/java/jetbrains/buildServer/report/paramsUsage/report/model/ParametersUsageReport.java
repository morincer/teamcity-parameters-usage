package jetbrains.buildServer.report.paramsUsage.report.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class ParametersUsageReport {
    List<ParameterUsageInfo> parametersUsage = new ArrayList<>();
}
