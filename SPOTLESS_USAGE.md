# Spotless Code Formatting Guide

This project uses [Spotless](https://github.com/diffplug/spotless) for automatic code formatting.

## Quick Start

### Format Code

```bash
# Using Maven directly
mvn spotless:apply

# Using Makefile
make format

# Using dev.sh
./dev.sh format
```

### Check Formatting (Without Applying)

```bash
# Using Maven directly
mvn spotless:check

# Using Makefile
make format-check

# Using dev.sh
./dev.sh format-check
```

## Configuration

Spotless is configured to use **Google Java Format** with the following settings:

- **Style**: Google Java Format (GOOGLE style)
- **Version**: 1.23.0
- **Features**:
  - Removes unused imports
  - Trims trailing whitespace
  - Ensures files end with newline

## How It Works

### Automatic Formatting on Build

Spotless is configured to run `spotless:check` during the `validate` phase. If code is not properly formatted, the build will fail.

### Formatting Rules

The formatter applies Google Java Format style, which includes:
- 2-space indentation (for Java)
- Line length: 100 characters (Google style default)
- Proper spacing around operators
- Consistent brace placement
- Proper import organization

## Usage Examples

### Format All Code

```bash
# Format all Java files in the project
make format
```

This will:
1. Format all Java files according to Google Java Format
2. Remove unused imports
3. Trim trailing whitespace
4. Ensure files end with newline

### Check Formatting Before Commit

```bash
# Check if code is properly formatted
make format-check
```

If formatting issues are found, the command will fail and show what needs to be fixed.

### Format and Check in CI

The `spotless:check` goal runs automatically during the build. If code is not formatted, the build fails.

## Integration with IDEs

### IntelliJ IDEA

1. Install the "Google Java Format" plugin
2. Enable "Reformat on save"
3. Or use the Spotless Maven plugin via Maven tool window

### VS Code

1. Install "Google Java Format" extension
2. Enable format on save
3. Or run `make format` manually

### Eclipse

1. Install Google Java Format plugin
2. Configure formatter to match Google style
3. Or use Maven to format: `mvn spotless:apply`

## Best Practices

1. **Format before committing**: Run `make format` or `./dev.sh format` before committing code
2. **Use format-check in CI**: Ensure all code is formatted before merging
3. **Configure IDE**: Set up your IDE to format on save to match Spotless
4. **Format on save**: Configure your IDE to automatically format files on save

## Troubleshooting

### Build Fails with Formatting Errors

If your build fails due to formatting issues:

```bash
# Auto-fix all formatting issues
make format

# Then commit the changes
git add .
git commit -m "fix: format code"
```

### Formatting Conflicts

If you have formatting conflicts in a merge:

```bash
# Resolve conflicts, then format
make format
```

### IDE Formatting Doesn't Match Spotless

Ensure your IDE is configured to use Google Java Format:
- IntelliJ: Install "Google Java Format" plugin
- VS Code: Install "Google Java Format" extension
- Eclipse: Install Google Java Format plugin

Or simply use Spotless:
```bash
make format
```

## Customization

To customize formatting rules, edit the Spotless configuration in `pom.xml`:

```xml
<plugin>
    <groupId>com.diffplug.spotless</groupId>
    <artifactId>spotless-maven-plugin</artifactId>
    <configuration>
        <java>
            <googleJavaFormat>
                <version>1.23.0</version>
                <style>GOOGLE</style>
            </googleJavaFormat>
            <!-- Add more rules here -->
            <removeUnusedImports/>
            <trimTrailingWhitespace/>
            <endWithNewline/>
        </java>
    </configuration>
</plugin>
```

## Excluding Files

To exclude specific files or directories from formatting:

```xml
<configuration>
    <java>
        <excludes>
            <exclude>**/generated/**</exclude>
            <exclude>**/target/**</exclude>
        </excludes>
        <!-- ... rest of config ... -->
    </java>
</configuration>
```

## Resources

- [Spotless Documentation](https://github.com/diffplug/spotless)
- [Google Java Format](https://github.com/google/google-java-format)
- [Spotless Maven Plugin](https://github.com/diffplug/spotless/tree/main/plugin-maven)

