package com.famaridon.ci.tools;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.famaridon.ci.tools.BashExporter;
import java.util.Arrays;
import java.util.Scanner;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;
import org.junit.Before;
import org.junit.Test;

public class BashExporterUTest {

  private BashExporter bashExporter;
  private ExpressionEvaluator expressionEvaluator;

  @Before
  public void setUp() throws Exception {
    this.expressionEvaluator = mock(ExpressionEvaluator.class);
    when(this.expressionEvaluator.evaluate("${project.artifactId}")).thenReturn("project-to-test");
    when(this.expressionEvaluator.evaluate("${project.version}")).thenReturn("1.0.0");
    when(this.expressionEvaluator.evaluate("${project.groupId}")).thenReturn("com.example");
    this.bashExporter = new BashExporter(expressionEvaluator);
  }

  @Test
  public void testExport() throws Exception {
    String output = this.bashExporter
        .export(Arrays.asList("project.artifactId", "project.version", "project.groupId"));

    try (Scanner scanner = new Scanner(output);) {
      assertTrue("bash MUST have hashbang", scanner.hasNextLine());
      assertEquals("hashbang MUST BE " + BashExporter.BASH_SHEBANG, BashExporter.BASH_SHEBANG,
          scanner.nextLine());
      assertEquals("export M2_CI_TOOLS_PROJECT_ARTIFACTID='project-to-test';", scanner.nextLine());
      assertEquals("export M2_CI_TOOLS_PROJECT_VERSION='1.0.0';", scanner.nextLine());
      assertEquals("export M2_CI_TOOLS_PROJECT_GROUPID='com.example';", scanner.nextLine());
    }
  }

  @Test
  public void testExportLine() throws Exception {
    String output = this.bashExporter.exportLine("project.artifactId");
    assertEquals("export M2_CI_TOOLS_PROJECT_ARTIFACTID='project-to-test';", output);
  }

  @Test
  public void toDefaultEnvironmentVariable() throws Exception {
    assertEquals(BashExporter.DEFAULT_VARIABLE_PREFIX+"PROJECT_ARTIFACTID",
        this.bashExporter.toEnvironmentVariable("project.artifactId"));
  }

  @Test
  public void toCustomEnvironmentVariable() throws Exception {
    this.bashExporter.setVariablePrefix("CUSTOM_");
    assertEquals("CUSTOM_PROJECT_ARTIFACTID",
        this.bashExporter.toEnvironmentVariable("project.artifactId"));
  }

  @Test
  public void toNullEnvironmentVariable() throws Exception {
    this.bashExporter.setVariablePrefix(null);
    assertEquals("PROJECT_ARTIFACTID",
        this.bashExporter.toEnvironmentVariable("project.artifactId"));
  }

}