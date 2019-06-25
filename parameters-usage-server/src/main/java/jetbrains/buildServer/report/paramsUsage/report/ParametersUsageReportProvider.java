package jetbrains.buildServer.report.paramsUsage.report;

import jetbrains.buildServer.report.paramsUsage.report.model.ParameterUsageInfo;
import jetbrains.buildServer.report.paramsUsage.report.model.ParametersUsageReport;
import jetbrains.buildServer.util.StringUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class ParametersUsageReportProvider {

    private final DocumentBuilder documentBuilder;
    private final XPath xpath;
    private final Pattern paramSearchRegex;

    @Getter
    private TeamcityConfigurationProvider configurationProvider;

    public ParametersUsageReportProvider(@NotNull TeamcityConfigurationProvider configurationProvider) throws ParserConfigurationException {
        this.configurationProvider = configurationProvider;
        this.documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        this.xpath = XPathFactory.newInstance().newXPath();
        this.paramSearchRegex = Pattern.compile("%(.*?)%");
    }

    public ParametersUsageReport buildReport() {
        var result = new ParametersUsageReport();

        Map<String, ParameterUsageInfo> parameters = new HashMap<>();

        this.configurationProvider.getProjects()
                .forEach((projectName, projectInfo) -> {
                    try {
                        var contents = Files.readAllBytes(projectInfo.getConfigurationFilePath());
                        var usedParameters = findUsedParameters(contents);

                        for (String usedParameter : usedParameters) {
                            if (!parameters.containsKey(usedParameter)) {
                                parameters.put(usedParameter, new ParameterUsageInfo(usedParameter));
                            }

                            var param = parameters.get(usedParameter);
                            param.getUsedAtProjects().add(projectInfo);
                        }

                        var definedParameters = findDefinedParameters(contents);

                        for (String definedParameter : definedParameters) {
                            if (!parameters.containsKey(definedParameter)) {
                                parameters.put(definedParameter, new ParameterUsageInfo(definedParameter));
                            }

                            var param = parameters.get(definedParameter);
                            param.getDefinedAtProjects().add(projectInfo);
                        }
                    } catch (Exception e) {
                        log.error(String.format("Failed to process %s: %s", projectInfo.getConfigurationFilePath(), e.getMessage()), e);
                    }
                });

        this.configurationProvider.getBuildTypes()
                .forEach((buildConfigName, buildTypeInfo) -> {
                    try {
                        var contents = Files.readAllBytes(buildTypeInfo.getConfigurationFilePath());
                        var usedParameters = findUsedParameters(contents);

                        for (String usedParameter : usedParameters) {
                            if (!parameters.containsKey(usedParameter)) {
                                parameters.put(usedParameter, new ParameterUsageInfo(usedParameter));
                            }

                            var param = parameters.get(usedParameter);
                            param.getUsedAtBuildConfigurations().add(buildTypeInfo);
                        }

                        var definedParameters = findDefinedParameters(contents);

                        for (String definedParameter : definedParameters) {
                            if (!parameters.containsKey(definedParameter)) {
                                parameters.put(definedParameter, new ParameterUsageInfo(definedParameter));
                            }

                            var param = parameters.get(definedParameter);
                            param.getDefinedAtBuildConfigurations().add(buildTypeInfo);
                        }
                    } catch (Exception e) {
                        log.error(String.format("Failed to process %s: %s", buildTypeInfo.getConfigurationFilePath(), e.getMessage()), e);
                    }
                });

        result.getParametersUsage().addAll(parameters.values());

        return result;
    }

    private Set<String> findUsedParameters(byte[] contents) {
        var result = new HashSet<String>();

        Matcher matcher = paramSearchRegex.matcher(new String(contents));
        while (matcher.find()) {
            result.add(matcher.group(1));
        }

        return result;
    }

    private Set<String> findDefinedParameters(byte[] contents) throws IOException, SAXException, XPathExpressionException {
        var result = new HashSet<String>();

        var doc = this.documentBuilder.parse(new ByteArrayInputStream(contents));
        var nodeList = (NodeList) this.xpath.evaluate("/project/parameters/param|/build-type/settings/parameters/param", doc, XPathConstants.NODESET);

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            var nameAttr = node.getAttributes().getNamedItem("name");

            if (nameAttr != null && !StringUtil.isEmpty(nameAttr.getNodeValue())) {
                result.add(nameAttr.getNodeValue());
            }
        }

        return result;
    }
}
