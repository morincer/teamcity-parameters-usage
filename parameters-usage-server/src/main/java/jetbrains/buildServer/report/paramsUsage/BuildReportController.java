package jetbrains.buildServer.report.paramsUsage;

import com.intellij.openapi.diagnostic.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.json.JsonWriter;
import jetbrains.buildServer.controllers.BaseAjaxActionController;
import jetbrains.buildServer.controllers.GetActionAllowed;
import jetbrains.buildServer.log.Loggers;
import jetbrains.buildServer.report.paramsUsage.report.model.ParameterUsageInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ParametersUsageReport;
import jetbrains.buildServer.report.paramsUsage.report.ParametersUsageReportProvider;
import jetbrains.buildServer.web.openapi.ControllerAction;
import jetbrains.buildServer.web.openapi.WebControllerManager;
import lombok.var;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@GetActionAllowed
public class BuildReportController extends BaseAjaxActionController implements ControllerAction{
    private final Logger LOG = Loggers.SERVER;

    private ParametersUsageReportProvider reportProvider;

    public BuildReportController(@NotNull WebControllerManager controllerManager,
                                 @NotNull ParametersUsageReportProvider reportProvider) {
        super(controllerManager);
        this.reportProvider = reportProvider;

        controllerManager.registerController(PluginConstants.REPORT_AJAX_URI, this);

        LOG.info(String.format("Registered controller at %s", PluginConstants.REPORT_AJAX_URI));

        this.registerAction(this);

    }

    @Override
    public boolean canProcess(@NotNull HttpServletRequest request) {
        return true;
    }

    @Override
    public void process(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @Nullable Element ajaxResponse) {
        var report = reportProvider.buildReport();

        var xstream = new XStream();
        xstream.aliasType("param", ParameterUsageInfo.class);
        xstream.aliasType("report", ParametersUsageReport.class);

        try {
            PrintWriter writer = response.getWriter();
            JsonWriter jsonWriter = new JsonWriter(writer);
            xstream.marshal(report, jsonWriter);

            writer.flush();
            jsonWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
