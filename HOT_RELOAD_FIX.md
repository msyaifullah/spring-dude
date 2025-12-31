# Hot Reload Fix Guide

## Quick Fix Checklist

### 1. Ensure Application is Running with Local Profile

The hot reload configuration is in `application-local.properties`. Make sure you're running with the `local` profile:

```bash
# Using dev script (recommended)
./dev.sh

# Or manually
mvn spring-boot:run -pl venus -Dspring-boot.run.profiles=local
```

**Important:** If you're running from an IDE, make sure the active profile is set to `local`.

---

## IDE-Specific Fixes

### IntelliJ IDEA / Cursor

1. **Enable Auto-Build:**
   - Go to `Settings` (or `Preferences` on Mac) → `Build, Execution, Deployment` → `Compiler`
   - ✅ Check **"Build project automatically"**

2. **Enable Auto-Make While Running:**
   - Go to `Settings` → `Advanced Settings`
   - ✅ Check **"Allow auto-make to start even if developed application is currently running"**

3. **Verify Run Configuration:**
   - Open Run Configuration for your Spring Boot app
   - In "Active profiles" field, enter: `local`
   - Or add to VM options: `-Dspring.profiles.active=local`

4. **Manual Build Trigger:**
   - Press `Cmd + F9` (Mac) or `Ctrl + F9` (Windows/Linux) to rebuild
   - This will trigger DevTools to restart the application

5. **Check DevTools is Enabled:**
   - Make sure `spring-boot-devtools` dependency is not excluded
   - Verify in `venus/pom.xml` that devtools is present (it is ✅)

### VS Code

1. **Install Extensions:**
   - Install "Spring Boot Extension Pack"
   - Install "Language Support for Java"

2. **Enable Auto-Save:**
   - `File` → `Auto Save` (or `Cmd/Ctrl + K, S`)
   - Set to "afterDelay" with 1000ms

3. **Java Compilation:**
   - VS Code should auto-compile on save
   - Check the "Problems" panel for compilation errors

### Eclipse / STS

1. **Enable Auto-Build:**
   - `Project` → `Build Automatically` (should be checked ✅)

2. **Run Configuration:**
   - Right-click on `EggsApplication.java` → `Run As` → `Run Configurations...`
   - Go to `Arguments` tab
   - In "Program arguments", add: `--spring.profiles.active=local`

---

## Manual Trigger Methods

### Method 1: Use Trigger File

The application is configured to watch for `.reloadtrigger` file:

```bash
# From project root
touch venus/.reloadtrigger

# Or use the dev script
./dev.sh reload
```

### Method 2: Manual Rebuild

In your IDE:
- **IntelliJ/Cursor:** `Cmd/Ctrl + F9` (Build Project)
- **VS Code:** `Cmd/Ctrl + Shift + P` → "Java: Rebuild Projects"
- **Eclipse:** `Project` → `Clean...` → Select project → `Clean`

### Method 3: Restart Application

If hot reload still doesn't work, restart the application:
- Stop the running application
- Start it again with `./dev.sh` or your IDE run configuration

---

## Verify Hot Reload is Working

1. **Check Logs:**
   When you save a file, you should see in the console:
   ```
   Restarting due to changes detected...
   ```

2. **Test It:**
   - Make a small change in a controller (e.g., change a log message)
   - Save the file
   - Watch the console for restart message
   - The change should be reflected without manual restart

3. **Check DevTools Status:**
   Look for this in startup logs:
   ```
   LiveReload server is running on port 35729
   ```

---

## Common Issues and Solutions

### Issue 1: Changes Not Detected

**Solution:**
- Make sure you're using the `local` profile
- Check that `spring.devtools.restart.enabled=true` in `application-local.properties`
- Verify the file is being saved (check file timestamp)

### Issue 2: Application Doesn't Restart

**Solution:**
- Manually trigger rebuild: `Cmd/Ctrl + F9`
- Or use trigger file: `touch venus/.reloadtrigger`
- Check for compilation errors that might prevent restart

### Issue 3: Changes Not Reflected

**Solution:**
- Wait a few seconds after saving (poll interval is 1 second)
- Check if there are compilation errors
- Verify the change was actually saved
- Try a full restart: stop and start the application

### Issue 4: IDE Not Compiling Automatically

**Solution:**
- Enable auto-build in IDE settings (see IDE-specific fixes above)
- Manually build after each change: `Cmd/Ctrl + F9`
- Check IDE's "Problems" or "Build" panel for errors

### Issue 5: Running from JAR Instead of IDE/Maven

**Solution:**
- DevTools only works when running via `mvn spring-boot:run` or from IDE
- It does NOT work when running a compiled JAR file
- Always use `./dev.sh` or IDE run configuration for development

---

## Configuration Check

Verify these settings in `venus/src/main/resources/application-local.properties`:

```properties
# Should be true
spring.devtools.restart.enabled=true

# Poll interval (how often to check for changes)
spring.devtools.restart.poll-interval=1s

# Quiet period (wait time before restarting)
spring.devtools.restart.quiet-period=400ms
```

---

## Alternative: Use Spring Boot DevTools Remote

If local hot reload still doesn't work, you can use remote DevTools:

1. **Add to application-local.properties:**
   ```properties
   spring.devtools.remote.secret=mysecret
   ```

2. **Run application:**
   ```bash
   ./dev.sh
   ```

3. **In another terminal, run remote client:**
   ```bash
   mvn spring-boot:run -pl venus -Dspring-boot.run.profiles=local -Dspring.devtools.remote.secret=mysecret
   ```

---

## Quick Test

To quickly test if hot reload is working:

1. Open `UserController.java`
2. Add a log statement:
   ```java
   log.info("Hot reload test - {}", new Date());
   ```
3. Save the file
4. Watch the console - you should see the restart message
5. Make a request to any endpoint
6. Check logs for your test message

---

## Still Not Working?

If hot reload still doesn't work after trying all above:

1. **Check Maven Configuration:**
   ```bash
   mvn clean compile -pl venus
   ```

2. **Verify DevTools Dependency:**
   ```bash
   mvn dependency:tree -pl venus | grep devtools
   ```
   Should show: `spring-boot-devtools`

3. **Check Application Logs:**
   Look for DevTools-related messages at startup

4. **Try Full Clean Restart:**
   ```bash
   ./dev.sh clean
   ```

5. **Verify Profile:**
   Check that `local` profile is active in application logs:
   ```
   The following profiles are active: local
   ```

---

## Recommended Workflow

For best hot reload experience:

1. **Always use `./dev.sh` to start the application**
2. **Enable auto-build in your IDE**
3. **Save files frequently** (or enable auto-save)
4. **Watch the console** for restart messages
5. **If changes don't appear**, manually trigger rebuild (`Cmd/Ctrl + F9`)

---

## Notes

- Hot reload works for Java code changes
- Static resources (HTML, CSS, JS) may require browser refresh
- Database schema changes require application restart
- Configuration file changes require restart
- Bean definition changes require restart

