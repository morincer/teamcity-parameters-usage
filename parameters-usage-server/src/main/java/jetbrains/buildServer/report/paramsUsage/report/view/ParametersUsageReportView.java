package jetbrains.buildServer.report.paramsUsage.report.view;

import jetbrains.buildServer.report.paramsUsage.report.model.BuildTypeInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ParameterUsageInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ParametersUsageReport;
import jetbrains.buildServer.report.paramsUsage.report.model.ProjectInfo;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParametersUsageReportView {
    private ParametersUsageReport report;

    @Getter
    private List<ParameterUsageView> parameters;

    public ParametersUsageReportView(ParametersUsageReport report) {
        this.report = report;
        build();
    }

    private void build() {
        if (report == null) {
            parameters = new ArrayList<>();
            return;
        }

        parameters = this.report.getParametersUsage().stream()
                .sorted(Comparator.comparing(p -> p.getName().toLowerCase()))
                .map(p -> new ParameterUsageView(
                        p.getName(),
                        getCombinedStream(p.getDefinedAtProjects(), p.getDefinedAtBuildConfigurations()).collect(Collectors.toList()),
                        getCombinedStream(p.getUsedAtProjects(), p.getUsedAtBuildConfigurations()).collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    private Stream<LinkTargetView> getCombinedStream(List<ProjectInfo> projects, List<BuildTypeInfo> btypes) {
        return Stream.concat(
                projects.stream() // sorted project links
                        .sorted(Comparator.comparing(p -> p.getName().toLowerCase()))
                        .map(pi -> new LinkTargetView(pi.getName(), pi.getUrl())),
                btypes.stream()
                        .sorted(Comparator.comparing(bt -> bt.getName().toLowerCase()))
                        .map(bt -> new LinkTargetView(bt.getName(), bt.getUrl()))
        );
    }

}
