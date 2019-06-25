package jetbrains.buildServer.report.paramsUsage.report.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BuildTypeInfo {
    private String name;
    private String url;
    private Path configurationFilePath;
}
