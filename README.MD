## Description

Simple maven plugin to create shell file with environment variable from pom expressions.

## Usage

### CLI 

To generate the file you can run this command : 

```bash
mvn com.famaridon:ci-tools-maven-plugin:0.0.1-SNAPSHOT:environment
```

you can use **expressions** argument to specify expressions.

```bash
mvn com.famaridon:ci-tools-maven-plugin:0.0.1-SNAPSHOT:environment -Dexpressions=project.artifactId,project.version,project.groupId,project.build.sourceEncoding
```

This will produce ```./target/ci-tools-env.sh``` like this : 

```bash
#!/usr/bin/env bash
CI_TOOLS_PROJECT_ARTIFACTID='ci-tools-maven-plugin';
CI_TOOLS_PROJECT_VERSION='0.0.1-SNAPSHOT';
CI_TOOLS_PROJECT_GROUPID='com.famaridon';
CI_TOOLS_PROJECT_BUILD_SOURCEENCODING='UTF-8';
```

### INITIALIZE build phase.

You can generate the file during INITIALIZE maven phase like this.

```xml
<?xml version="1.0" encoding="UTF-8"?>


<project xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
  <modelVersion>4.0.0</modelVersion>

  <groupId>your.groupid</groupId>
  <artifactId>your-artifact</artifactId>
  <version>0.0.1-SNAPSHOT</version>

  <properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>1.8</maven.compiler.source>
    <maven.compiler.target>1.8</maven.compiler.target>
  </properties>

  <build>
    <plugins>
      <plugin>
        <groupId>com.famaridon</groupId>
        <artifactId>ci-tools-maven-plugin</artifactId>
        <version>0.0.1-SNAPSHOT</version>
        <configuration>
          <expressions>
            <expression>project.artifactId</expression>
            <expression>project.version</expression>
            <expression>project.groupId</expression>
            <expression>project.build.sourceEncoding</expression>
          </expressions>
        </configuration>
      </plugin>
    </plugins>
  </build>
</project>

```

Expression can be any of [PluginParameterExpressionEvaluator](https://maven.apache.org/ref/3.3.9/maven-core/apidocs/org/apache/maven/plugin/PluginParameterExpressionEvaluator.html)