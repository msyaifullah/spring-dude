# JaCoCo Code Coverage Guide

This project uses [JaCoCo](https://www.jacoco.org/jacoco/) for code coverage analysis.

## Quick Start

### Generate Coverage Report

```bash
# Using Maven directly
mvn clean test jacoco:report

# Using Makefile
make coverage

# Using dev.sh
./dev.sh coverage
```

### View Coverage Reports

After running tests with coverage, HTML reports are generated at:
- **Earth module**: `earth/target/site/jacoco/index.html`
- **Venus module**: `venus/target/site/jacoco/index.html`

The reports will automatically open in your browser (if supported) when using:
```bash
make coverage-report
# or
./dev.sh coverage
```

## Coverage Thresholds

The project is configured with the following coverage thresholds:
- **Package level**: Minimum 50% line coverage
- **Class level**: Minimum 50% line coverage

If coverage falls below these thresholds, the build will fail.

### Adjusting Thresholds

Edit the `jacoco-maven-plugin` configuration in `venus/pom.xml` or `earth/pom.xml`:

```xml
<configuration>
    <rules>
        <rule>
            <element>PACKAGE</element>
            <limits>
                <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.60</minimum>  <!-- Change to 60% -->
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

## Available Commands

### Maven Commands

```bash
# Generate coverage report
mvn test jacoco:report

# Check coverage thresholds
mvn test jacoco:check

# Generate report for specific module
mvn test jacoco:report -pl earth
mvn test jacoco:report -pl venus
```

### Makefile Commands

```bash
make coverage          # Generate coverage report
make coverage-report   # Generate and open report in browser
make coverage-check    # Run tests with coverage check (fails if thresholds not met)
```

### dev.sh Commands

```bash
./dev.sh coverage      # Run tests and generate coverage report
```

## Coverage Metrics

JaCoCo tracks several coverage metrics:

- **Line Coverage**: Percentage of lines executed
- **Branch Coverage**: Percentage of branches (if/else, switch) executed
- **Instruction Coverage**: Percentage of bytecode instructions executed
- **Method Coverage**: Percentage of methods executed
- **Class Coverage**: Percentage of classes executed

## Excluding Code from Coverage

To exclude specific classes or packages from coverage analysis, add exclusions:

```xml
<configuration>
    <excludes>
        <exclude>**/config/**</exclude>
        <exclude>**/dto/**</exclude>
        <exclude>**/*Application.class</exclude>
    </excludes>
</configuration>
```

## CI/CD Integration

JaCoCo reports can be integrated into CI/CD pipelines:

1. Generate reports during test phase
2. Publish HTML reports as artifacts
3. Use coverage data for quality gates

Example GitLab CI configuration:

```yaml
test:
  script:
    - mvn clean test jacoco:report
  artifacts:
    reports:
      coverage_report:
        coverage_format: cobertura
        path: venus/target/site/jacoco/jacoco.xml
    paths:
      - venus/target/site/jacoco/
      - earth/target/site/jacoco/
    expire_in: 1 week
```

## Best Practices

1. **Aim for meaningful coverage**: Focus on testing business logic, not just achieving high percentages
2. **Review coverage reports regularly**: Identify untested code paths
3. **Set realistic thresholds**: Start with lower thresholds and increase gradually
4. **Exclude generated code**: Don't count DTOs, configuration classes, etc.
5. **Use coverage to guide testing**: Identify gaps in test coverage

## Troubleshooting

### Reports not generating

- Ensure tests are running: `mvn test`
- Check that JaCoCo plugin is properly configured in `pom.xml`
- Verify Maven can access the JaCoCo plugin

### Coverage thresholds too strict

- Temporarily lower thresholds during development
- Focus on critical paths first
- Gradually increase thresholds as coverage improves

### Reports not opening in browser

- Manually navigate to `target/site/jacoco/index.html`
- Check file permissions
- Use `make coverage-report` which handles cross-platform browser opening

## Resources

- [JaCoCo Official Documentation](https://www.jacoco.org/jacoco/trunk/doc/)
- [JaCoCo Maven Plugin](https://www.jacoco.org/jacoco/trunk/doc/maven.html)
- [Code Coverage Best Practices](https://www.jacoco.org/jacoco/trunk/doc/maven.html)

