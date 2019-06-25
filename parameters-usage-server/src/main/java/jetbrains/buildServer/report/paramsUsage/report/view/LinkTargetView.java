package jetbrains.buildServer.report.paramsUsage.report.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkTargetView {
    private String linkText;
    private String linkHref;
}
