package com.famaridon.ci.tools;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.lifecycle.internal.MojoDescriptorCreator;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.PluginParameterExpressionEvaluator;
import org.apache.maven.plugin.descriptor.MojoDescriptor;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluationException;
import org.codehaus.plexus.component.configurator.expression.ExpressionEvaluator;

/**
 * Goal which touches a timestamp file.
 */
@Mojo(name = "environment", defaultPhase = LifecyclePhase.INITIALIZE)
public class EnvironmentMojo extends AbstractMojo {

  /**
   * Component used to get mojo descriptors.
   */
  @Component
  private MojoDescriptorCreator mojoDescriptorCreator;

  /**
   * The current Maven project.
   */
  @Parameter(defaultValue = "${project}", readonly = true, required = true)
  private MavenProject project;

  @Parameter(defaultValue = "${mojo}", readonly = true, required = true)
  private MojoExecution mojoExecution;

  /**
   * The current build session instance. This is used for
   * plugin manager API calls.
   */
  @Parameter( defaultValue = "${session}", readonly = true, required = true )
  protected MavenSession session;

  /** lazy loading evaluator variable */
  private PluginParameterExpressionEvaluator evaluator;

  @Parameter( defaultValue = "project.artifactId,project.version,project.groupId", readonly = true, required = true )
  protected List<String> expressions;

  @Parameter( defaultValue = "${project.build.directory}/ci-tools-env.sh", readonly = true, required = true )
  protected File outputFile;

  public void execute() throws MojoExecutionException {

    ExpressionEvaluator evaluator = new PluginParameterExpressionEvaluator( session, mojoExecution );

    BashExporter bashExporter = new BashExporter(evaluator);
    try {
      outputFile.getParentFile().mkdirs();
      try (PrintWriter out = new PrintWriter(outputFile);) {
        out.println(bashExporter.export(this.expressions));
      }
    } catch (ExpressionEvaluationException | IOException e) {
      throw new MojoExecutionException("Fail to build output.", e);
    }
  }

  /**
   * copied from:  https://github.com/apache/maven-help-plugin/blob/master/src/main/java/org/apache/maven/plugins/help/EvaluateMojo.java
   */
  private PluginParameterExpressionEvaluator getEvaluator()
      throws MojoExecutionException, MojoFailureException
  {
    if ( evaluator == null )
    {
      MojoDescriptor mojoDescriptor;
      try
      {
        mojoDescriptor = mojoDescriptorCreator.getMojoDescriptor( "help:evaluate", session, project );
      }
      catch ( Exception e )
      {
        throw new MojoFailureException( "Failure while evaluating.", e );
      }
      MojoExecution mojoExecution = new MojoExecution( mojoDescriptor );

      MavenProject currentProject = session.getCurrentProject();
      // Maven 3: PluginParameterExpressionEvaluator gets the current project from the session:
      // synchronize in case another thread wants to fetch the real current project in between
      synchronized ( session )
      {
        session.setCurrentProject( project );
        evaluator = new PluginParameterExpressionEvaluator( session, mojoExecution );
        session.setCurrentProject( currentProject );
      }
    }

    return evaluator;
  }
}
