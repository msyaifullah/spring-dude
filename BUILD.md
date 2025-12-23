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

---

## Local Maven Configuration (~/.m2)

Maven uses a local directory `~/.m2` for caching dependencies and storing configuration.

### Directory Structure

```
~/.m2/
├── settings.xml           # Maven settings (credentials, mirrors, proxies)
├── settings-security.xml  # Encrypted master password (optional)
└── repository/            # Local repository cache
    ├── com/
    ├── org/
    └── ...
```

### Settings File Location

| OS | Path |
|----|------|
| macOS/Linux | `~/.m2/settings.xml` |
| Windows | `C:\Users\{username}\.m2\settings.xml` |

### Complete settings.xml Example

Create or edit `~/.m2/settings.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<settings xmlns="http://maven.apache.org/SETTINGS/1.2.0"
          xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.2.0
                              https://maven.apache.org/xsd/settings-1.2.0.xsd">

    <!-- Local repository location (optional, defaults to ~/.m2/repository) -->
    <localRepository>${user.home}/.m2/repository</localRepository>

    <!-- Offline mode (optional) -->
    <offline>false</offline>

    <!-- Server credentials for deployment -->
    <servers>
        <!-- JFrog Artifactory -->
        <server>
            <id>jfrog-releases</id>
            <username>your-username</username>
            <password>your-password-or-token</password>
        </server>
        <server>
            <id>jfrog-snapshots</id>
            <username>your-username</username>
            <password>your-password-or-token</password>
        </server>

        <!-- Nexus Repository -->
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

        <!-- GitHub Packages -->
        <server>
            <id>github</id>
            <username>your-github-username</username>
            <password>your-github-token</password>
        </server>
    </servers>

    <!-- Mirrors (redirect repository requests) -->
    <mirrors>
        <!-- Example: Mirror all requests to corporate Artifactory -->
        <!--
        <mirror>
            <id>corporate-artifactory</id>
            <name>Corporate Artifactory Mirror</name>
            <url>https://your-company.jfrog.io/artifactory/maven-virtual</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
        -->

        <!-- Example: Mirror only central to Artifactory, allow others -->
        <!--
        <mirror>
            <id>artifactory-central</id>
            <name>Artifactory Central Mirror</name>
            <url>https://your-company.jfrog.io/artifactory/libs-release</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
        -->
    </mirrors>

    <!-- Proxy settings (if behind corporate proxy) -->
    <!--
    <proxies>
        <proxy>
            <id>corporate-proxy</id>
            <active>true</active>
            <protocol>http</protocol>
            <host>proxy.your-company.com</host>
            <port>8080</port>
            <username>proxy-user</username>
            <password>proxy-password</password>
            <nonProxyHosts>localhost|127.0.0.1|*.your-company.com</nonProxyHosts>
        </proxy>
    </proxies>
    -->

    <!-- Profiles -->
    <profiles>
        <!-- Profile for using Maven Central directly -->
        <profile>
            <id>maven-central</id>
            <repositories>
                <repository>
                    <id>central</id>
                    <name>Maven Central</name>
                    <url>https://repo.maven.apache.org/maven2</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
            </repositories>
            <pluginRepositories>
                <pluginRepository>
                    <id>central-plugins</id>
                    <name>Maven Central Plugins</name>
                    <url>https://repo.maven.apache.org/maven2</url>
                </pluginRepository>
            </pluginRepositories>
        </profile>

        <!-- Profile for JFrog Artifactory -->
        <profile>
            <id>jfrog</id>
            <repositories>
                <repository>
                    <id>jfrog-releases</id>
                    <name>JFrog Releases</name>
                    <url>https://your-org.jfrog.io/artifactory/libs-release</url>
                    <releases>
                        <enabled>true</enabled>
                    </releases>
                    <snapshots>
                        <enabled>false</enabled>
                    </snapshots>
                </repository>
                <repository>
                    <id>jfrog-snapshots</id>
                    <name>JFrog Snapshots</name>
                    <url>https://your-org.jfrog.io/artifactory/libs-snapshot</url>
                    <releases>
                        <enabled>false</enabled>
                    </releases>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>

        <!-- Profile for Nexus -->
        <profile>
            <id>nexus</id>
            <repositories>
                <repository>
                    <id>nexus-releases</id>
                    <name>Nexus Releases</name>
                    <url>https://your-nexus.com/repository/maven-releases/</url>
                </repository>
                <repository>
                    <id>nexus-snapshots</id>
                    <name>Nexus Snapshots</name>
                    <url>https://your-nexus.com/repository/maven-snapshots/</url>
                    <snapshots>
                        <enabled>true</enabled>
                    </snapshots>
                </repository>
            </repositories>
        </profile>
    </profiles>

    <!-- Active profiles (activate by default) -->
    <activeProfiles>
        <activeProfile>maven-central</activeProfile>
        <!-- Uncomment to activate JFrog or Nexus -->
        <!-- <activeProfile>jfrog</activeProfile> -->
        <!-- <activeProfile>nexus</activeProfile> -->
    </activeProfiles>

</settings>
```

### Common Configuration Scenarios

#### 1. Use Maven Central Only (Default)

```xml
<settings>
    <activeProfiles>
        <activeProfile>maven-central</activeProfile>
    </activeProfiles>
</settings>
```

#### 2. Use Corporate JFrog with Maven Central Fallback

```xml
<settings>
    <mirrors>
        <!-- Mirror central to JFrog, but allow direct access to others -->
        <mirror>
            <id>jfrog-central</id>
            <url>https://your-org.jfrog.io/artifactory/libs-release</url>
            <mirrorOf>central</mirrorOf>
        </mirror>
    </mirrors>
</settings>
```

#### 3. Disable Mirror (Fix Inaccessible Repository Error)

If you see errors like `airasia.jfrog.io: nodename nor servname provided`, comment out or remove the mirror:

```xml
<settings>
    <mirrors>
        <!-- Comment out inaccessible mirrors -->
        <!--
        <mirror>
            <id>central</id>
            <url>https://airasia.jfrog.io/airasia/libs-release</url>
            <mirrorOf>*</mirrorOf>
        </mirror>
        -->
    </mirrors>
</settings>
```

### Encrypting Passwords

For security, encrypt passwords in settings.xml:

```bash
# Create master password
mvn --encrypt-master-password your-master-password
# Output: {jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}

# Add to ~/.m2/settings-security.xml
```

Create `~/.m2/settings-security.xml`:

```xml
<settingsSecurity>
    <master>{jSMOWnoPFgsHVpMvz5VrIt5kRbzGpI8u+9EF1iFQyJQ=}</master>
</settingsSecurity>
```

Then encrypt server passwords:

```bash
mvn --encrypt-password your-server-password
# Output: {COQLCE6DU6GtcS5P=}

# Use in settings.xml
<server>
    <id>my-server</id>
    <username>user</username>
    <password>{COQLCE6DU6GtcS5P=}</password>
</server>
```

### Clearing Local Repository Cache

```bash
# Clear entire local repository (re-downloads everything)
rm -rf ~/.m2/repository

# Clear specific artifact
rm -rf ~/.m2/repository/com/mysql/mysql-connector-j

# Clear cached failures and force update
mvn dependency:resolve -U

# Purge local repository of snapshots
mvn dependency:purge-local-repository -DsnapshotsOnly=true
```

### Useful Maven Commands

```bash
# Show effective settings
mvn help:effective-settings

# Show effective POM (merged with parent)
mvn help:effective-pom

# Display dependency tree
mvn dependency:tree

# Check for dependency updates
mvn versions:display-dependency-updates

# Analyze dependencies (find unused/undeclared)
mvn dependency:analyze
```

---

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

## Development with Auto-Reload

The project includes Spring Boot DevTools for automatic restart when code changes are detected.

### Quick Start

```bash
# Use the dev script (recommended)
./dev.sh

# Or run manually
mvn spring-boot:run -pl venus -Dspring-boot.run.profiles=local
```

### Dev Script Commands

| Command | Description |
|---------|-------------|
| `./dev.sh` | Run with auto-reload (default) |
| `./dev.sh debug` | Run with remote debugging (port 5005) |
| `./dev.sh clean` | Clean build and run |
| `./dev.sh test` | Run all tests |
| `./dev.sh build` | Build without running |
| `./dev.sh reload` | Force a reload |

### How Auto-Reload Works

1. **Automatic Restart**: When you modify Java files, Spring Boot DevTools automatically restarts the application
2. **LiveReload**: Browser automatically refreshes when static resources change (requires LiveReload browser extension)
3. **Fast Restart**: Uses a custom classloader for faster restarts (typically 1-2 seconds)

### Development URLs

| URL | Description |
|-----|-------------|
| http://localhost:8080 | Application |
| http://localhost:8080/h2-console | H2 Database Console |
| http://localhost:35729 | LiveReload Server |

### IDE Configuration

#### IntelliJ IDEA

1. Enable auto-build:
   - `Settings > Build, Execution, Deployment > Compiler`
   - Check "Build project automatically"

2. Enable auto-make while running:
   - `Settings > Advanced Settings`
   - Check "Allow auto-make to start even if developed application is currently running"

3. Or use keyboard shortcut:
   - Press `Cmd + F9` (Mac) or `Ctrl + F9` (Windows/Linux) to rebuild

#### VS Code

1. Install "Spring Boot Extension Pack"
2. Auto-save is usually enabled by default
3. Java files are compiled on save

#### Eclipse/STS

1. Auto-build is usually enabled by default
2. Check `Project > Build Automatically`

### Trigger Files

You can force a restart by touching the trigger file:

```bash
# Force restart
touch venus/src/main/resources/.reloadtrigger

# Or use the dev script
./dev.sh reload
```

### Remote Debugging

To attach a debugger:

```bash
# Start in debug mode
./dev.sh debug

# Or manually
mvn spring-boot:run -pl venus \
    -Dspring-boot.run.profiles=local \
    -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=*:5005"
```

Then connect your IDE debugger to `localhost:5005`.

### Configuration Files

| Profile | File | Description |
|---------|------|-------------|
| local | `application-local.properties` | Local dev with H2 DB, auto-reload |
| dev | `application-dev.properties` | Development with MySQL |
| gitlab | `application-gitlab.properties` | CI/CD environment |

### Disable Auto-Reload

If you need to disable auto-reload temporarily:

```bash
# Via command line
mvn spring-boot:run -pl venus \
    -Dspring-boot.run.profiles=local \
    -Dspring.devtools.restart.enabled=false

# Or in application-local.properties
spring.devtools.restart.enabled=false
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
