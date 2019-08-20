package com.famaridon;


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

  public static final String VARIABLE_PREFIX = "M2_CI_TOOLS_";
  private final ExpressionEvaluator expressionEvaluator;
  private static final String EXPORT_TEMPLATE = "export {0}=''{1}'';";

  public BashExporter(ExpressionEvaluator expressionEvaluator) {
    this.expressionEvaluator = expressionEvaluator;
  }

  public String export(List<String> expresions) throws ExpressionEvaluationException {
    StringBuilder result = new StringBuilder("#!/bin/bash\n");
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
    return VARIABLE_PREFIX +expression.toUpperCase().replaceAll("\\.", "_").replaceAll("-", "_");
  }

}
