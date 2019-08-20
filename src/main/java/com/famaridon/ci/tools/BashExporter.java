package com.famaridon.ci.tools;


import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Stream;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;


public class BashExporter {

  public static final String DEFAULT_VARIABLE_PREFIX = "M2_CI_TOOLS_";
  public static final String BASH_SHEBANG = "#!/usr/bin/env bash";
  private static final String EXPORT_TEMPLATE = "export {0}=''{1}'';";

  private final ExpressionEvaluator expressionEvaluator;
  private String variablePrefix;


  public BashExporter(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
    this.variablePrefix = DEFAULT_VARIABLE_PREFIX;
  }

  public String export(List<String> expresions) throws ExpressionEvaluationException {
    StringBuilder result = new StringBuilder(BASH_SHEBANG + '\n');
    for (String expresion : expresions) {
      result.append(this.exportLine(expresion)).append('\n');
    }
    return result.toString();
  }

  protected String exportLine(String expression) throws ExpressionEvaluationException {
    String value = this.expressionEvaluator.evaluate("${" + expression + "}").toString();
    return MessageFormat.format(EXPORT_TEMPLATE, this.toEnvironmentVariable(expression), value);
  }

  protected String toEnvironmentVariable(String expression) throws ExpressionEvaluationException {
    return this.variablePrefix + expression.toUpperCase().replaceAll("\\.", "_").replaceAll("-", "_");
  }

  public String getVariablePrefix() {
    return variablePrefix;
  }

  public void setVariablePrefix(String variablePrefix) {
    this.variablePrefix = variablePrefix == null ? "" : variablePrefix;
  }
}
