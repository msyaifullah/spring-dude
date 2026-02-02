# Pre-Commit Hook for Code Formatting

This project includes a pre-commit Git hook that automatically formats code using Spotless before each commit.

## How It Works

When you commit code, the pre-commit hook will:

1. **Check for staged Java files** - Only runs if Java files are being committed
2. **Format code automatically** - Runs `mvn spotless:apply` to format all code
3. **Stage formatted files** - Automatically adds formatted files back to the staging area
4. **Verify formatting** - Ensures all code meets formatting standards
5. **Allow commit** - Proceeds with the commit if formatting passes

## Installation

The hook is automatically installed when you build the project:

```bash
# Build the project (hook will be installed automatically)
mvn clean install

# Or just run the install goal
mvn git-build-hook:install
```

The hook is located at: `tools/hooks/pre-commit`

## Usage

### Normal Workflow

Simply commit as usual - the hook will run automatically:

```bash
git add .
git commit -m "feat: add new feature"
```

The hook will:
- Format your code
- Stage the formatted files
- Continue with the commit

### What You'll See

When you commit, you'll see output like:

```
[PRE-COMMIT] Running code formatting check...
[PRE-COMMIT] Formatting staged Java files...
[PRE-COMMIT] Code was automatically formatted. Staging formatted files...
  + src/main/java/com/example/MyClass.java
[PRE-COMMIT] Verifying code formatting...
[PRE-COMMIT] Code formatting check passed!
```

### If Formatting Fails

If there are formatting issues that can't be auto-fixed:

```
[PRE-COMMIT] Code formatting check failed!
[PRE-COMMIT] Please run 'mvn spotless:apply' or 'make format' to fix formatting issues.
```

In this case:
1. Fix the formatting issues manually
2. Run `make format` or `mvn spotless:apply`
3. Stage the changes: `git add .`
4. Commit again

## Bypassing the Hook (Emergency Only)

If you need to bypass the hook in an emergency (not recommended):

```bash
git commit --no-verify -m "emergency: urgent fix"
```

**Warning**: Only use `--no-verify` in true emergencies. Formatted code is required for the project.

## Manual Hook Installation

If the hook isn't installed automatically, you can install it manually:

```bash
# Make the hook executable
chmod +x tools/hooks/pre-commit

# Copy to .git/hooks (if using standard Git hooks)
cp tools/hooks/pre-commit .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

Or use the Maven plugin:

```bash
mvn git-build-hook:install
```

## Troubleshooting

### Hook Not Running

1. **Check if hook is installed**:
   ```bash
   ls -la .git/hooks/pre-commit
   ```

2. **Check if hook is executable**:
   ```bash
   chmod +x .git/hooks/pre-commit
   ```

3. **Reinstall hooks**:
   ```bash
   mvn git-build-hook:install
   ```

### Maven Not Found

If you see "Maven not found" warning:
- Install Maven or ensure it's in your PATH
- The hook will skip formatting if Maven is unavailable

### Spotless Plugin Not Configured

If you see "Spotless plugin not configured":
- Ensure Spotless is properly configured in `pom.xml`
- Run `mvn clean install` to verify configuration

### Slow Commits

The hook runs Maven, which can be slow. To speed up:
- Use `mvn spotless:apply -q` (quiet mode) - already configured
- Consider using a faster formatter for pre-commit (future enhancement)

## Customization

### Modify Hook Behavior

Edit `tools/hooks/pre-commit` to customize:
- Which files to format
- Formatting commands
- Error handling
- Output messages

### Skip Formatting for Specific Commits

You can temporarily disable formatting by modifying the hook, but it's better to:
1. Format code manually: `make format`
2. Commit normally

## Integration with Other Hooks

This hook works alongside the existing `commit-msg` hook:
1. **pre-commit** - Formats code (runs first)
2. **commit-msg** - Validates commit message format (runs after)

Both hooks must pass for the commit to succeed.

## Best Practices

1. **Let the hook format code** - Don't manually format before commit
2. **Review formatted changes** - Check what the hook changed before committing
3. **Keep hook updated** - Update hook if formatting rules change
4. **Don't bypass** - Avoid `--no-verify` unless absolutely necessary

## Files Modified

- `tools/hooks/pre-commit` - The pre-commit hook script
- `venus/pom.xml` - Updated to install pre-commit hook
- `.git/hooks/pre-commit` - Installed hook (created automatically)

## Related Documentation

- [Spotless Usage Guide](./SPOTLESS_USAGE.md) - How to use Spotless manually
- [Productivity Improvements](./PRODUCTIVITY_IMPROVEMENTS.md) - All tooling improvements

