# How to Change JaCoCo Coverage Thresholds

## Current Configuration

The JaCoCo coverage thresholds are configured in both `earth/pom.xml` and `venus/pom.xml`.

**Current settings:**
- **Package level**: 30% line coverage minimum (`0.30`)
- **Class level**: 30% line coverage minimum (`0.30`)
- **Build behavior**: Non-blocking (`haltOnFailure: false`)

## Where to Change

Edit the `<minimum>` value in both module POM files:

### Location 1: `earth/pom.xml`

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <executions>
        <execution>
            <id>check</id>
            <goals>
                <goal>check</goal>
            </goals>
            <configuration>
                <haltOnFailure>false</haltOnFailure>
                <rules>
                    <rule>
                        <element>PACKAGE</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.30</minimum>  <!-- Change this value -->
                            </limit>
                        </limits>
                    </rule>
                    <rule>
                        <element>CLASS</element>
                        <limits>
                            <limit>
                                <counter>LINE</counter>
                                <value>COVEREDRATIO</value>
                                <minimum>0.30</minimum>  <!-- Change this value -->
                            </limit>
                        </limits>
                    </rule>
                </rules>
            </configuration>
        </execution>
    </executions>
</plugin>
```

### Location 2: `venus/pom.xml`

Same structure - update the `<minimum>` values in the same way.

## Common Threshold Values

| Value | Percentage | Use Case |
|-------|-----------|----------|
| `0.00` | 0% | No enforcement (just generate reports) |
| `0.20` | 20% | Very lenient, for new projects |
| `0.30` | 30% | Current setting - reasonable starting point |
| `0.50` | 50% | Moderate - good for established projects |
| `0.60` | 60% | Good coverage target |
| `0.70` | 70% | High coverage requirement |
| `0.80` | 80% | Very high coverage requirement |
| `0.90` | 90% | Excellent coverage (hard to maintain) |

## Options

### 1. Change Threshold Value

To change from 30% to 50%:

```xml
<minimum>0.50</minimum>  <!-- Changed from 0.30 to 0.50 -->
```

### 2. Disable Threshold Check (Keep Reports Only)

To disable threshold checking but still generate reports:

**Option A**: Remove the `check` execution entirely
```xml
<!-- Just comment out or remove this execution -->
<!--
<execution>
    <id>check</id>
    ...
</execution>
-->
```

**Option B**: Set threshold to 0
```xml
<minimum>0.00</minimum>
```

### 3. Make Build Fail on Low Coverage

To make the build fail if coverage is below threshold:

```xml
<haltOnFailure>true</haltOnFailure>  <!-- Changed from false to true -->
```

### 4. Different Thresholds for Different Modules

You can set different thresholds for `earth` and `venus`:

**earth/pom.xml** (library module - might need lower threshold):
```xml
<minimum>0.20</minimum>
```

**venus/pom.xml** (main app - might need higher threshold):
```xml
<minimum>0.50</minimum>
```

## Coverage Counters

You can also change what type of coverage to check:

### Available Counters

- `LINE` - Line coverage (most common)
- `BRANCH` - Branch coverage (if/else, switch)
- `INSTRUCTION` - Bytecode instruction coverage
- `METHOD` - Method coverage
- `CLASS` - Class coverage

### Example: Check Branch Coverage Instead

```xml
<limit>
    <counter>BRANCH</counter>  <!-- Changed from LINE -->
    <value>COVEREDRATIO</value>
    <minimum>0.30</minimum>
</limit>
```

### Example: Check Multiple Counters

```xml
<rule>
    <element>PACKAGE</element>
    <limits>
        <limit>
            <counter>LINE</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.30</minimum>
        </limit>
        <limit>
            <counter>BRANCH</counter>
            <value>COVEREDRATIO</value>
            <minimum>0.25</minimum>
        </limit>
    </limits>
</rule>
```

## Quick Examples

### Example 1: Set to 50% and Fail Build

```xml
<configuration>
    <haltOnFailure>true</haltOnFailure>
    <rules>
        <rule>
            <element>PACKAGE</element>
            <limits>
                <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.50</minimum>
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

### Example 2: Disable Threshold Check

```xml
<configuration>
    <haltOnFailure>false</haltOnFailure>
    <rules>
        <rule>
            <element>PACKAGE</element>
            <limits>
                <limit>
                    <counter>LINE</counter>
                    <value>COVEREDRATIO</value>
                    <minimum>0.00</minimum>  <!-- No enforcement -->
                </limit>
            </limits>
        </rule>
    </rules>
</configuration>
```

### Example 3: Only Warn, Don't Fail

```xml
<configuration>
    <haltOnFailure>false</haltOnFailure>  <!-- Current setting -->
    <rules>
        <!-- ... rules ... -->
    </rules>
</configuration>
```

## Testing Your Changes

After changing thresholds, test with:

```bash
# Run tests with coverage check
mvn clean test jacoco:check

# Or use the Makefile
make coverage-check
```

## Current Status

✅ **Current configuration:**
- Threshold: 30% (0.30)
- Behavior: Non-blocking (warns but doesn't fail build)
- Applied to: Both `earth` and `venus` modules

This means:
- Coverage reports are still generated
- If coverage is below 30%, you'll see a warning
- The build will **not fail** due to low coverage
- You can gradually increase the threshold as coverage improves

## Recommendations

1. **Start low**: Begin with 20-30% and increase gradually
2. **Different per module**: Library modules might need lower thresholds
3. **Non-blocking first**: Use `haltOnFailure: false` initially
4. **Increase over time**: As you add tests, increase thresholds
5. **Focus on critical code**: Consider excluding DTOs, config classes from coverage

## Excluding Code from Coverage

If you want to exclude certain packages/classes from coverage checks:

```xml
<configuration>
    <excludes>
        <exclude>**/dto/**</exclude>
        <exclude>**/config/**</exclude>
        <exclude>**/*Application.class</exclude>
    </excludes>
    <rules>
        <!-- ... rules ... -->
    </rules>
</configuration>
```

