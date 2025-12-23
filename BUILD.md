# Build and Deployment Guide

This document covers how to build, test, and deploy the multi-module Spring Boot project.

## Project Structure

```
spring-dude/
├── pom.xml                    # Parent POM (aggregator)
├── earth/                     # Library module (reusable component)
│   └── pom.xml
└── venus/                     # Main application module
    └── pom.xml
```

- **earth**: Library module containing shared utilities, domain classes, and adapters
- **venus**: Main Spring Boot application that depends on `earth`

## Prerequisites

- Java 17+
- Maven 3.8+
- (Optional) JFrog Artifactory or Nexus for artifact deployment

## Building the Project

### Build All Modules

```bash
# Clean and compile all modules
mvn clean compile

# Build with tests
mvn clean install

# Build without tests (faster)
mvn clean install -DskipTests
```

### Build Specific Module

```bash
# Build only earth module
mvn clean install -pl earth

# Build only venus module (requires earth to be installed first)
mvn clean install -pl venus

# Build venus and its dependencies (earth)
mvn clean install -pl venus -am
```

## Running Tests

### Run All Tests

```bash
mvn test
```

### Run Tests for Specific Module

```bash
# Test earth module only
mvn test -pl earth

# Test venus module only
mvn test -pl venus
```

### Run Specific Test Class

```bash
mvn test -Dtest=BusinessAServiceTest -pl earth
mvn test -Dtest=AuditorServiceTest -pl venus
```

## Running the Application

### Run Venus Application

```bash
# Using Maven
mvn spring-boot:run -pl venus

# Using specific profile
mvn spring-boot:run -pl venus -Dspring-boot.run.profiles=local
mvn spring-boot:run -pl venus -Dspring-boot.run.profiles=dev
```

### Run as JAR

```bash
# Build the JAR
mvn clean package -DskipTests

# Run the JAR
java -jar venus/target/venus-0.0.1-SNAPSHOT.jar

# Run with specific profile
java -jar venus/target/venus-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

---

## Deploying Earth Module as Standalone Library

The `earth` module can be deployed separately to a Maven repository (Maven Central, JFrog Artifactory, or Nexus) for use by other projects.

### Step 1: Configure Distribution Management

Add the following to `earth/pom.xml`:

```xml
<distributionManagement>
    <!-- For releases -->
    <repository>
        <id>releases</id>
        <name>Release Repository</name>
        <url>https://your-artifactory.com/artifactory/libs-release-local</url>
    </repository>
    <!-- For snapshots -->
    <snapshotRepository>
        <id>snapshots</id>
        <name>Snapshot Repository</name>
        <url>https://your-artifactory.com/artifactory/libs-snapshot-local</url>
    </snapshotRepository>
</distributionManagement>
```

### Step 2: Configure Maven Settings

Add credentials to `~/.m2/settings.xml`:

```xml
<settings>
    <servers>
        <server>
            <id>releases</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
        <server>
            <id>snapshots</id>
            <username>your-username</username>
            <password>your-password</password>
        </server>
    </servers>
</settings>
```

### Step 3: Deploy Earth Module

```bash
# Deploy snapshot version
mvn deploy -pl earth

# Deploy release version (after updating version in pom.xml)
mvn deploy -pl earth -DskipTests
```

---

## JFrog Artifactory Deployment

### Option A: Using Maven Deploy Plugin

1. Add JFrog repository configuration to `earth/pom.xml`:

```xml
<distributionManagement>
    <repository>
        <id>central</id>
        <name>libs-release</name>
        <url>https://your-org.jfrog.io/artifactory/libs-release-local</url>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <name>libs-snapshot</name>
        <url>https://your-org.jfrog.io/artifactory/libs-snapshot-local</url>
    </snapshotRepository>
</distributionManagement>
```

2. Deploy:

```bash
mvn deploy -pl earth
```

### Option B: Using JFrog CLI

1. Install JFrog CLI:

```bash
# macOS
brew install jfrog-cli

# Or download from https://jfrog.com/getcli/
```

2. Configure JFrog CLI:

```bash
jfrog config add
# Follow prompts to enter:
# - Server ID (e.g., "my-artifactory")
# - JFrog Platform URL
# - Access token or username/password
```

3. Build and deploy:

```bash
# Build the earth module
mvn clean package -pl earth -DskipTests

# Deploy using JFrog CLI
jfrog rt upload \
  "earth/target/earth-0.0.1-SNAPSHOT.jar" \
  "libs-snapshot-local/lib/yyggee/earth/0.0.1-SNAPSHOT/" \
  --server-id=my-artifactory

# Deploy POM file
jfrog rt upload \
  "earth/pom.xml" \
  "libs-snapshot-local/lib/yyggee/earth/0.0.1-SNAPSHOT/earth-0.0.1-SNAPSHOT.pom" \
  --server-id=my-artifactory
```

### Option C: Using JFrog Artifactory Maven Plugin

1. Add plugin to `earth/pom.xml`:

```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.jfrog.buildinfo</groupId>
            <artifactId>artifactory-maven-plugin</artifactId>
            <version>3.6.2</version>
            <inherited>false</inherited>
            <executions>
                <execution>
                    <id>build-info</id>
                    <goals>
                        <goal>publish</goal>
                    </goals>
                    <configuration>
                        <publisher>
                            <contextUrl>https://your-org.jfrog.io/artifactory</contextUrl>
                            <username>${env.JFROG_USER}</username>
                            <password>${env.JFROG_PASSWORD}</password>
                            <repoKey>libs-release-local</repoKey>
                            <snapshotRepoKey>libs-snapshot-local</snapshotRepoKey>
                        </publisher>
                    </configuration>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

2. Deploy:

```bash
export JFROG_USER=your-username
export JFROG_PASSWORD=your-password
mvn deploy -pl earth
```

---

## Nexus Repository Deployment

### Configure for Nexus

1. Add to `earth/pom.xml`:

```xml
<distributionManagement>
    <repository>
        <id>nexus-releases</id>
        <name>Nexus Release Repository</name>
        <url>https://your-nexus.com/repository/maven-releases/</url>
    </repository>
    <snapshotRepository>
        <id>nexus-snapshots</id>
        <name>Nexus Snapshot Repository</name>
        <url>https://your-nexus.com/repository/maven-snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

2. Add credentials to `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>nexus-releases</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
    <server>
        <id>nexus-snapshots</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
</servers>
```

3. Deploy:

```bash
mvn deploy -pl earth
```

---

## Using Earth Module in Other Projects

Once deployed, other projects can use the `earth` module by adding:

```xml
<dependency>
    <groupId>lib.yyggee</groupId>
    <artifactId>earth</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

And configuring the repository:

```xml
<repositories>
    <repository>
        <id>your-repo</id>
        <url>https://your-artifactory.com/artifactory/libs-release</url>
    </repository>
</repositories>
```

---

## CI/CD Integration

### GitLab CI Example

The project includes `.gitlab-ci.yml` for CI/CD. Key stages:

```yaml
# Build stage
build:
  script:
    - mvn clean compile

# Test stage
test:
  script:
    - mvn test

# Deploy earth module
deploy-earth:
  script:
    - mvn deploy -pl earth -DskipTests
  only:
    - main
    - tags
```

### GitHub Actions Example

```yaml
name: Build and Deploy

on:
  push:
    branches: [main]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build with Maven
        run: mvn clean install

      - name: Deploy Earth Module
        if: github.ref == 'refs/heads/main'
        run: mvn deploy -pl earth -DskipTests
        env:
          MAVEN_USERNAME: ${{ secrets.MAVEN_USERNAME }}
          MAVEN_PASSWORD: ${{ secrets.MAVEN_PASSWORD }}
```

---

## Version Management

### Release a New Version

1. Update version in all pom.xml files:

```bash
# Set release version
mvn versions:set -DnewVersion=1.0.0

# Commit and tag
git add .
git commit -m "Release version 1.0.0"
git tag v1.0.0

# Deploy
mvn deploy -pl earth -DskipTests

# Set next snapshot version
mvn versions:set -DnewVersion=1.0.1-SNAPSHOT
git add .
git commit -m "Prepare for next development iteration"
```

### Using Maven Release Plugin

```bash
# Prepare release (updates versions, creates tag)
mvn release:prepare -pl earth

# Perform release (builds and deploys)
mvn release:perform -pl earth
```

---

## Troubleshooting

### Common Issues

1. **Dependency not found after deploy**
   - Verify the artifact was uploaded correctly
   - Check repository URL in consumer project
   - Clear local Maven cache: `rm -rf ~/.m2/repository/lib/yyggee/earth`

2. **Authentication failed**
   - Verify credentials in `~/.m2/settings.xml`
   - Check server ID matches between pom.xml and settings.xml

3. **Build fails with missing earth dependency**
   - Run `mvn install -pl earth` first
   - Or use `mvn install -pl venus -am` to build with dependencies

4. **Tests fail due to database connection**
   - Ensure H2 test database is configured
   - Check `application-test.yml` settings
