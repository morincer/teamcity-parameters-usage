package jetbrains.buildServer.report.paramsUsage;

import com.intellij.openapi.diagnostic.Logger;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.report.paramsUsage.report.ParametersUsageReportProvider;
import jetbrains.buildServer.report.paramsUsage.report.view.ParametersUsageReportView;
import jetbrains.buildServer.web.openapi.PagePlaces;
import jetbrains.buildServer.web.openapi.PlaceId;
import jetbrains.buildServer.web.openapi.PluginDescriptor;
import jetbrains.buildServer.web.openapi.SimpleCustomTab;
import lombok.var;
import org.jetbrains.annotations.NotNull;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ParametersUsageReportPage extends SimpleCustomTab {

    private static final Logger LOG = Loggers.SERVER;
    private PluginDescriptor pluginDescriptor;
    private ParametersUsageReportProvider reportProvider;

    public ParametersUsageReportPage(@NotNull PagePlaces pagePlaces,
                                     PluginDescriptor pluginDescriptor,
                                     ParametersUsageReportProvider reportProvider) {
        super(pagePlaces);
        this.pluginDescriptor = pluginDescriptor;
        this.reportProvider = reportProvider;
        setPlaceId(PlaceId.MY_TOOLS_TABS);
        setTabTitle("Parameters Usage");

        LOG.info("Parameters Usage report page is initialized");

    }

    @NotNull
    @Override
    public String getPluginName() {
        return PluginConstants.PLUGIN_NAME;
    }

    @Override
    public boolean isVisible() {
        return true;
    }

    @NotNull
    @Override
    public String getIncludeUrl() {
        return this.pluginDescriptor.getPluginResourcesPath("parametersUsageReport.jsp");
    }

    @Override
    public void fillModel(@NotNull Map<String, Object> model, @NotNull HttpServletRequest request) {
        super.fillModel(model, request);
        var report = reportProvider.buildReport();
        model.put("pluginResources", this.pluginDescriptor.getPluginResourcesPath());
        model.put("report", new ParametersUsageReportView(report));

    }
}
