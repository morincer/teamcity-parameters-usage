package jetbrains.buildServer.report.paramsUsage.report.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ParameterUsageView {
    private String name;
    private List<LinkTargetView> definedAt;
    private List<LinkTargetView> usedAt;
}
