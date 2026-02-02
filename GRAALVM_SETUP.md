# GraalVM Setup with SDKMAN

This guide explains how to set up GraalVM for native image compilation using SDKMAN.

## Quick Setup

### 1. List Available GraalVM Versions

```bash
sdk list java | grep graalvm
```

You'll see output like:
```
   | 17.0.10-graalce    | graal | local only | 17.0.10-graalce
   | 21.0.1-graalce     | graal | local only | 21.0.1-graalce
```

### 2. Install GraalVM (Latest LTS - Java 21 recommended)

```bash
# Install GraalVM Community Edition for Java 21 LTS (Latest LTS)
sdk install java 21.0.1-graalce

# Or install Java 17 LTS if you prefer
# sdk install java 17.0.10-graalce

# Or install the latest available GraalVM version automatically
sdk install java $(sdk list java | grep -E 'graalvm|graalce' | head -1 | awk '{print $NF}')
```

### 3. Set GraalVM as Current Java Version

```bash
# Use the installed GraalVM (Java 21 LTS)
sdk use java 21.0.1-graalce

# Or set it as default
sdk default java 21.0.1-graalce

# For Java 17 LTS:
# sdk use java 17.0.10-graalce
# sdk default java 17.0.10-graalce
```

### 4. Install Native Image Tool

The `native-image` tool is required for building native executables:

```bash
gu install native-image
```

If `gu` command is not found, it's usually at:
```bash
${JAVA_HOME}/bin/gu install native-image
```

### 5. Verify Installation

```bash
# Check Java version (should show GraalVM)
java -version

# Check if native-image is available
native-image --version

# Or use the Makefile check
make check-graalvm
```

## Usage

Once GraalVM is set up:

```bash
# Build native executable locally
make build-native

# Run native executable
make run-native
```

## Java Version Compatibility

**Note:** This project is currently configured for Java 17, but Spring Boot 3.3.7 also supports Java 21 LTS. 

- **Docker builds** (`make docker-build-native`) will use Java 21 LTS automatically
- **Local builds** will use whatever GraalVM version you have installed via SDKMAN
- Both Java 17 and Java 21 LTS work with this project

If you want to update the project to use Java 21, you can update `venus/pom.xml`:
```xml
<properties>
    <java.version>21</java.version>
</properties>
```

## Troubleshooting

### Issue: "native-image is not installed in your JAVA_HOME"

**Solution:**
1. Make sure you're using GraalVM (not regular JDK):
   ```bash
   sdk use java 21.0.1-graalce  # or 17.x-graalce for Java 17 LTS
   java -version  # Should show "GraalVM"
   ```

2. Install native-image tool:
   ```bash
   gu install native-image
   ```

3. Verify:
   ```bash
   which native-image
   native-image --version
   ```

### Issue: "GRAALVM_HOME is not set"

**Solution:**
The plugin should work with `JAVA_HOME` if it points to GraalVM. You can also set `GRAALVM_HOME` explicitly:

```bash
export GRAALVM_HOME=$(sdk home java current)
export PATH=$GRAALVM_HOME/bin:$PATH
```

### Issue: Build takes too long

**Note:** Native image compilation typically takes 5-15 minutes. This is normal. The resulting executable will start much faster and use less memory.

## Alternative: Use Docker for Native Builds

If you don't want to install GraalVM locally, you can use Docker instead:

```bash
# Build native image using Docker (no local GraalVM needed)
make docker-build-native

# Run native container
make docker-run-native
```

This uses `Dockerfile.native` which includes GraalVM in the build container.
