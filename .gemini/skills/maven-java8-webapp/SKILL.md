---
name: maven-java8-webapp
description: |
  Configuração completa de Maven para builds de webapps Java 8 (WAR packaging). Cobre compiler plugin, war plugin, surefire para testes, JaCoCo para cobertura, e resolução de problemas comuns com plugins legados em Java moderno.

  Use when: Configurar build Maven para aplicações web Java 8 ou atualizar projetos legados Java 6/7 para compilar com Java 8+ mantendo compatibilidade.
version: 1.0.0
tags: [maven, java8, webapp, war, build-tools, jacoco, testing]
---

# Maven Java 8 Webapp Build Configuration

## Overview

Configuração Maven para builds de webapps Java 8 com packaging WAR, incluindo testes automatizados, cobertura de código e resolução de problemas de compatibilidade com plugins legados.

## Basic POM Structure

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-webapp</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <packaging>war</packaging>

    <name>My Web Application</name>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>

        <!-- Plugin Versions -->
        <maven.compiler.plugin.version>3.8.1</maven.compiler.plugin.version>
        <maven.war.plugin.version>3.3.2</maven.war.plugin.version>
        <maven.surefire.plugin.version>2.22.2</maven.surefire.plugin.version>
        <jacoco.version>0.8.8</jacoco.version>
    </properties>
</project>
```

## Essential Plugins

### 1. Maven Compiler Plugin

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven.compiler.plugin.version}</version>
            <configuration>
                <source>${maven.compiler.source}</source>
                <target>${maven.compiler.target}</target>
                <encoding>${project.build.sourceEncoding}</encoding>
                <showWarnings>true</showWarnings>
                <showDeprecation>true</showDeprecation>
            </configuration>
        </plugin>
    </plugins>
</build>
```

**Key Points:**
- Use version 3.8.1+ for Java 8-11 compatibility
- `source` and `target` define Java version
- `encoding` ensures consistent character encoding

### 2. Maven WAR Plugin

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-war-plugin</artifactId>
    <version>${maven.war.plugin.version}</version>
    <configuration>
        <failOnMissingWebXml>true</failOnMissingWebXml>
        <warName>${project.build.finalName}</warName>
        <archive>
            <manifest>
                <addDefaultImplementationEntries>true</addDefaultImplementationEntries>
            </manifest>
        </archive>
    </configuration>
</plugin>
```

**Important:**
- **Version 3.3.2+** required for Java 8+ compatibility
- Version 2.6 and older fail with Java 8+ module system errors
- Set `failOnMissingWebXml=false` for Servlet 3.0+ annotation-based config

**Common Error:**
```
module java.base does not "opens java.util" to unnamed module
```
**Solution:** Upgrade to maven-war-plugin 3.3.2+

### 3. Maven Surefire Plugin (Testing)

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>${maven.surefire.plugin.version}</version>
    <configuration>
        <redirectTestOutputToFile>false</redirectTestOutputToFile>
        <includes>
            <include>**/*Test.java</include>
            <include>**/*Tests.java</include>
        </includes>
        <excludes>
            <exclude>**/*IntegrationTest.java</exclude>
        </excludes>
    </configuration>
</plugin>
```

**Key Features:**
- Auto-discovers test classes ending in `Test` or `Tests`
- Supports JUnit 4, JUnit 5, TestNG
- `redirectTestOutputToFile=false` shows output in console

### 4. JaCoCo Code Coverage

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>${jacoco.version}</version>
    <executions>
        <!-- Prepare agent for test execution -->
        <execution>
            <id>prepare-agent</id>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>

        <!-- Generate report after tests -->
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>

        <!-- Enforce coverage thresholds -->
        <execution>
            <id>check</id>
            <phase>test</phase>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <includes>
                    <include>com/example/service/*</include>
                    <include>com/example/dao/*</include>
                    <include>com/example/util/*</include>
                </includes>
                <rules>
                    <rule>
                        <element>BUNDLE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.80</minimum>
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**Coverage Metrics:**
- `LINE`: Line coverage (recommended)
- `BRANCH`: Branch coverage (if/else, switch)
- `INSTRUCTION`: Bytecode instruction coverage
- `COMPLEXITY`: Cyclomatic complexity coverage

**Report Location:** `target/site/jacoco/index.html`

## Complete Build Configuration Example

```xml
<build>
    <finalName>my-webapp-${project.version}</finalName>

    <resources>
        <resource>
            <directory>src/main/resources</directory>
            <filtering>true</filtering>
        </resource>
    </resources>

    <plugins>
        <!-- Compiler -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>1.8</source>
                <target>1.8</target>
                <encoding>UTF-8</encoding>
            </configuration>
        </plugin>

        <!-- WAR Packaging -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-war-plugin</artifactId>
            <version>3.3.2</version>
            <configuration>
                <failOnMissingWebXml>true</failOnMissingWebXml>
            </configuration>
        </plugin>

        <!-- Test Execution -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>2.22.2</version>
        </plugin>

        <!-- Code Coverage -->
        <plugin>
            <groupId>org.jacoco</groupId>
            <artifactId>jacoco-maven-plugin</artifactId>
            <version>0.8.8</version>
            <executions>
                <execution>
                    <id>prepare-agent</id>
                    <goals>
                        <goal>prepare-agent</goal>
                    </goals>
                </execution>
                <execution>
                    <id>report</id>
                    <phase>test</phase>
                    <goals>
                        <goal>report</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

## Maven Commands

### Build Lifecycle

```bash
# Clean build directory
mvn clean

# Compile source code
mvn compile

# Run tests
mvn test

# Package as WAR
mvn package

# Skip tests
mvn package -DskipTests

# Install to local repository
mvn install

# Clean + Package
mvn clean package
```

### Testing Commands

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run specific test method
mvn test -Dtest=UserServiceTest#testFindByName

# Run tests matching pattern
mvn test -Dtest=*ServiceTest

# Run with verbose output
mvn test -X
```

### Coverage Reports

```bash
# Generate coverage report
mvn clean test

# View report
open target/site/jacoco/index.html

# Check coverage thresholds
mvn jacoco:check
```

## Common Plugin Version Matrix

| Plugin | Minimum Version | Recommended | Notes |
|--------|----------------|-------------|-------|
| maven-compiler-plugin | 3.1 | 3.8.1 | Java 8-11 support |
| maven-war-plugin | 3.3.2 | 3.3.2 | **Critical** for Java 8+ |
| maven-surefire-plugin | 2.19.1 | 2.22.2 | JUnit 5 support |
| jacoco-maven-plugin | 0.8.0 | 0.8.8 | Java 8+ support |
| maven-resources-plugin | 3.0.0 | 3.3.0 | UTF-8 support |

## Troubleshooting

### Problem 1: maven-war-plugin Fails with Java 8+

**Error:**
```
module java.base does not "opens java.util" to unnamed module
ExceptionInInitializerError
```

**Solution:**
```xml
<!-- Upgrade from 2.6 to 3.3.2+ -->
<maven.war.plugin.version>3.3.2</maven.war.plugin.version>
```

### Problem 2: Source Option Not Supported

**Error:**
```
Source option 6 is no longer supported. Use 7 or later.
```

**Solution:**
```xml
<maven.compiler.source>1.8</maven.compiler.source>
<maven.compiler.target>1.8</maven.compiler.target>
```

### Problem 3: Tests Not Running

**Check:**
1. Test class names end with `Test` or `Tests`
2. Surefire plugin version >= 2.19.1
3. JUnit dependency in `<scope>test</scope>`

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
```

### Problem 4: JaCoCo Coverage Not Generated

**Solution:**
```bash
# Ensure prepare-agent runs before tests
mvn clean test

# Check target/jacoco.exec exists
ls -la target/jacoco.exec

# Manually generate report
mvn jacoco:report
```

### Problem 5: Character Encoding Warnings

**Solution:**
```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
</properties>
```

## Advanced Configurations

### Multi-Module Project

```xml
<modules>
    <module>webapp-core</module>
    <module>webapp-web</module>
    <module>webapp-integration-tests</module>
</modules>
```

### Dependency Management

```xml
<dependencyManagement>
    <dependencies>
        <!-- Spring BOM -->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-framework-bom</artifactId>
            <version>4.3.30.RELEASE</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### Profiles for Different Environments

```xml
<profiles>
    <profile>
        <id>dev</id>
        <activation>
            <activeByDefault>true</activeByDefault>
        </activation>
        <properties>
            <env>development</env>
            <db.url>jdbc:h2:mem:testdb</db.url>
        </properties>
    </profile>

    <profile>
        <id>prod</id>
        <properties>
            <env>production</env>
            <db.url>jdbc:postgresql://prod-db:5432/myapp</db.url>
        </properties>
    </profile>
</profiles>
```

**Usage:**
```bash
mvn clean package -Pprod
```

## Best Practices

1. **Pin Plugin Versions**
   ```xml
   <!-- ✅ Good -->
   <version>3.8.1</version>

   <!-- ❌ Avoid - unpredictable builds -->
   <version>LATEST</version>
   ```

2. **Use Properties for Versions**
   ```xml
   <properties>
       <maven.compiler.plugin.version>3.8.1</maven.compiler.plugin.version>
   </properties>

   <plugin>
       <version>${maven.compiler.plugin.version}</version>
   </plugin>
   ```

3. **Set Encoding Explicitly**
   ```xml
   <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
   ```

4. **Use `clean` Before `package`**
   ```bash
   mvn clean package  # Ensures clean build
   ```

5. **Skip Tests Only When Necessary**
   ```bash
   # ✅ During rapid development
   mvn package -DskipTests

   # ❌ Before commit/push
   mvn package  # Always run tests
   ```

## References

- [Maven Official Documentation](https://maven.apache.org/guides/)
- [Maven Compiler Plugin](https://maven.apache.org/plugins/maven-compiler-plugin/)
- [Maven WAR Plugin](https://maven.apache.org/plugins/maven-war-plugin/)
- [Maven Surefire Plugin](https://maven.apache.org/surefire/maven-surefire-plugin/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- Source: `pom.xml` (this project)

## Minimal Working POM Template

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                             http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>my-webapp</artifactId>
    <version>1.0.0</version>
    <packaging>war</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>3.1.0</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-war-plugin</artifactId>
                <version>3.3.2</version>
            </plugin>
        </plugins>
    </build>
</project>
```
