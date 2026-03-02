.PHONY: help build test clean run dev format format-check check coverage coverage-report coverage-check docker-build docker-run docker-build-native docker-run-native docker-compose-up docker-compose-down build-native run-native gu-install-native-image gu-list gu-list-installed gu benchmark benchmark-native benchmark-jar benchmark-compare

help: ## Show this help message
	@echo "Available commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project (skip tests)
	mvn clean install -DskipTests

build-test: ## Build the project with tests
	mvn clean install

# Helper to get gu command path
GU_CMD := $(shell if command -v java &> /dev/null; then JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); if [ -f "$$JAVA_HOME/bin/gu" ]; then echo "$$JAVA_HOME/bin/gu"; elif command -v gu &> /dev/null; then echo "gu"; else echo ""; fi; fi)

check-graalvm: ## Check if GraalVM is installed and configured
	@echo "Checking GraalVM installation..."
	@NATIVE_IMAGE_FOUND=0; \
	if command -v java &> /dev/null; then \
		JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
		if [ -f "$$JAVA_HOME/bin/native-image" ]; then \
			NATIVE_IMAGE_FOUND=1; \
		fi; \
	fi; \
	if [ $$NATIVE_IMAGE_FOUND -eq 0 ] && [ -f .sdkmanrc ]; then \
		GRAALVM_VERSION=$$(grep "^java=" .sdkmanrc | cut -d'=' -f2 | tr -d ' '); \
		if [ -n "$$GRAALVM_VERSION" ]; then \
			SDKMAN_BASE="/opt/homebrew/opt/sdkman-cli/libexec/candidates/java"; \
			if [ ! -d "$$SDKMAN_BASE" ]; then \
				SDKMAN_BASE="$$HOME/.sdkman/candidates/java"; \
			fi; \
			if [ -f "$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/native-image" ]; then \
				NATIVE_IMAGE_FOUND=1; \
				JAVA_HOME="$$SDKMAN_BASE/$$GRAALVM_VERSION"; \
			fi; \
		fi; \
	fi; \
	if command -v java &> /dev/null; then \
		if [ $$NATIVE_IMAGE_FOUND -eq 1 ]; then \
			echo "✓ GraalVM found at: $$JAVA_HOME"; \
			java -version 2>&1 | head -n 1; \
			echo "✓ native-image tool found"; \
		else \
			JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
			echo "✗ GraalVM/native-image not found in JAVA_HOME: $$JAVA_HOME"; \
			echo ""; \
			echo "To install GraalVM LTS with SDKMAN:"; \
			echo "  1. sdk list java | grep -E 'graalvm|graalce'"; \
			echo "  2. sdk install java 21.0.1-graalce  # Latest LTS (Java 21)"; \
			echo "     # Or: sdk install java 17.x-graalce  # Java 17 LTS"; \
			echo "  3. sdk env  # Use project's .sdkmanrc"; \
			echo "  4. make gu-install-native-image  # (if gu is available)"; \
			exit 1; \
		fi; \
	else \
		echo "✗ Java not found. Please install Java/GraalVM first."; \
		exit 1; \
	fi

gu-install-native-image: ## Install native-image tool using gu
	@GU_CMD=""; \
	GRAALVM_HOME=""; \
	if command -v java &> /dev/null; then \
		JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
		if [ -f "$$JAVA_HOME/bin/gu" ]; then \
			GU_CMD="$$JAVA_HOME/bin/gu"; \
			GRAALVM_HOME="$$JAVA_HOME"; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && [ -f .sdkmanrc ]; then \
		GRAALVM_VERSION=$$(grep "^java=" .sdkmanrc | cut -d'=' -f2 | tr -d ' '); \
		if [ -n "$$GRAALVM_VERSION" ]; then \
			SDKMAN_BASE="/opt/homebrew/opt/sdkman-cli/libexec/candidates/java"; \
			if [ ! -d "$$SDKMAN_BASE" ]; then \
				SDKMAN_BASE="$$HOME/.sdkman/candidates/java"; \
			fi; \
			if [ -f "$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu" ]; then \
				GU_CMD="$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu"; \
				GRAALVM_HOME="$$SDKMAN_BASE/$$GRAALVM_VERSION"; \
			fi; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && command -v gu &> /dev/null; then \
		GU_CMD="gu"; \
	fi; \
	if [ -n "$$GU_CMD" ]; then \
		echo "Installing native-image using gu..."; \
		$$GU_CMD install native-image; \
	elif [ -n "$$GRAALVM_HOME" ]; then \
		echo "✗ gu command not found in GraalVM installation: $$GRAALVM_HOME"; \
		echo ""; \
		echo "This may indicate an incomplete GraalVM installation."; \
		echo "Try reinstalling GraalVM:"; \
		echo "  sdk uninstall java 21.0.1-graalce"; \
		echo "  sdk install java 21.0.1-graalce"; \
		echo "  sdk env"; \
		echo "  make gu-install-native-image"; \
		exit 1; \
	else \
		echo "✗ gu command not found."; \
		echo ""; \
		echo "Make sure GraalVM is installed and active:"; \
		echo "  1. Run: sdk env"; \
		echo "  2. Then try again: make gu-install-native-image"; \
		echo ""; \
		echo "Or install GraalVM if not installed:"; \
		echo "  sdk install java 21.0.1-graalce"; \
		exit 1; \
	fi

gu-list: ## List all available GraalVM components
	@GU_CMD=""; \
	if command -v java &> /dev/null; then \
		JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
		if [ -f "$$JAVA_HOME/bin/gu" ]; then \
			GU_CMD="$$JAVA_HOME/bin/gu"; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && [ -f .sdkmanrc ]; then \
		GRAALVM_VERSION=$$(grep "^java=" .sdkmanrc | cut -d'=' -f2 | tr -d ' '); \
		if [ -n "$$GRAALVM_VERSION" ]; then \
			SDKMAN_BASE="/opt/homebrew/opt/sdkman-cli/libexec/candidates/java"; \
			if [ ! -d "$$SDKMAN_BASE" ]; then \
				SDKMAN_BASE="$$HOME/.sdkman/candidates/java"; \
			fi; \
			if [ -f "$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu" ]; then \
				GU_CMD="$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu"; \
			fi; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && command -v gu &> /dev/null; then \
		GU_CMD="gu"; \
	fi; \
	if [ -n "$$GU_CMD" ]; then \
		$$GU_CMD list; \
	else \
		echo "✗ gu command not found. Run 'sdk env' first."; \
		exit 1; \
	fi

gu-list-installed: ## List installed GraalVM components
	@GU_CMD=""; \
	if command -v java &> /dev/null; then \
		JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
		if [ -f "$$JAVA_HOME/bin/gu" ]; then \
			GU_CMD="$$JAVA_HOME/bin/gu"; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && [ -f .sdkmanrc ]; then \
		GRAALVM_VERSION=$$(grep "^java=" .sdkmanrc | cut -d'=' -f2 | tr -d ' '); \
		if [ -n "$$GRAALVM_VERSION" ]; then \
			SDKMAN_BASE="/opt/homebrew/opt/sdkman-cli/libexec/candidates/java"; \
			if [ ! -d "$$SDKMAN_BASE" ]; then \
				SDKMAN_BASE="$$HOME/.sdkman/candidates/java"; \
			fi; \
			if [ -f "$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu" ]; then \
				GU_CMD="$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu"; \
			fi; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && command -v gu &> /dev/null; then \
		GU_CMD="gu"; \
	fi; \
	if [ -n "$$GU_CMD" ]; then \
		$$GU_CMD list --installed; \
	else \
		echo "✗ gu command not found. Run 'sdk env' first."; \
		exit 1; \
	fi

gu: ## Run gu command (usage: make gu ARGS="install python")
	@if [ -z "$(ARGS)" ]; then \
		echo "Usage: make gu ARGS=\"<gu-command> <component>\""; \
		echo "Example: make gu ARGS=\"install python\""; \
		exit 1; \
	fi; \
	GU_CMD=""; \
	if command -v java &> /dev/null; then \
		JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
		if [ -f "$$JAVA_HOME/bin/gu" ]; then \
			GU_CMD="$$JAVA_HOME/bin/gu"; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && [ -f .sdkmanrc ]; then \
		GRAALVM_VERSION=$$(grep "^java=" .sdkmanrc | cut -d'=' -f2 | tr -d ' '); \
		if [ -n "$$GRAALVM_VERSION" ]; then \
			SDKMAN_BASE="/opt/homebrew/opt/sdkman-cli/libexec/candidates/java"; \
			if [ ! -d "$$SDKMAN_BASE" ]; then \
				SDKMAN_BASE="$$HOME/.sdkman/candidates/java"; \
			fi; \
			if [ -f "$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu" ]; then \
				GU_CMD="$$SDKMAN_BASE/$$GRAALVM_VERSION/bin/gu"; \
			fi; \
		fi; \
	fi; \
	if [ -z "$$GU_CMD" ] && command -v gu &> /dev/null; then \
		GU_CMD="gu"; \
	fi; \
	if [ -n "$$GU_CMD" ]; then \
		$$GU_CMD $(ARGS); \
	else \
		echo "✗ gu command not found. Run 'sdk env' first."; \
		exit 1; \
	fi

build-native: check-graalvm ## Build native executable (requires GraalVM installed locally)
	@echo "Building native executable (this may take 5-15 minutes)..."
	mvn clean package -Pnative -DskipTests
	@echo ""
	@echo "Native executable built at: venus/target/venus"
	@echo "Size: $$(du -h venus/target/venus | cut -f1)"

run-native: ## Run native executable (loads .env automatically)
	@if [ ! -f venus/target/venus ]; then \
		echo "Error: Native executable not found. Run 'make build-native' first."; \
		exit 1; \
	fi
	@if [ -f .env ]; then echo "Loading .env file..."; set -a; . ./.env; set +a; fi; \
	./venus/target/venus

test: ## Run all tests
	mvn test

test-module: ## Run tests for specific module (usage: make test-module MODULE=earth)
	mvn test -pl $(MODULE)

coverage: ## Run tests and generate coverage report
	mvn clean test jacoco:report
	@echo ""
	@echo "Coverage report generated at:"
	@echo "  - earth: earth/target/site/jacoco/index.html"
	@echo "  - venus: venus/target/site/jacoco/index.html"

coverage-report: ## Generate coverage report (open in browser if possible)
	mvn clean test jacoco:report
	@echo ""
	@echo "Coverage reports generated:"
	@if command -v open > /dev/null; then \
		echo "Opening coverage reports..."; \
		open earth/target/site/jacoco/index.html venus/target/site/jacoco/index.html 2>/dev/null || true; \
	elif command -v xdg-open > /dev/null; then \
		echo "Opening coverage reports..."; \
		xdg-open earth/target/site/jacoco/index.html venus/target/site/jacoco/index.html 2>/dev/null || true; \
	else \
		echo "  - earth: earth/target/site/jacoco/index.html"; \
		echo "  - venus: venus/target/site/jacoco/index.html"; \
		echo "  Open these files in your browser to view coverage reports."; \
	fi

coverage-check: ## Run tests with coverage check (fails if thresholds not met)
	mvn clean test jacoco:check
	@echo "Coverage check passed!"

clean: ## Clean build artifacts
	mvn clean

run: ## Run the application using dev.sh (loads .env automatically)
	@if [ -f .env ]; then echo "Loading .env file..."; set -a; . ./.env; set +a; fi; \
	./dev.sh

dev: ## Run in development mode (loads .env automatically)
	@if [ -f .env ]; then echo "Loading .env file..."; set -a; . ./.env; set +a; fi; \
	./dev.sh

debug: ## Run in debug mode (loads .env automatically)
	@if [ -f .env ]; then echo "Loading .env file..."; set -a; . ./.env; set +a; fi; \
	./dev.sh debug

run-maven: ## Run application directly with Maven (loads .env)
	@if [ -f .env ]; then echo "Loading .env file..."; set -a; . ./.env; set +a; fi; \
	mvn install -pl earth -DskipTests -q && \
	mvn spring-boot:run -pl venus -Dspring-boot.run.profiles=local

format: ## Format code using Spotless
	mvn spotless:apply
	@echo "Code formatted successfully!"

format-check: ## Check code formatting (without applying)
	mvn spotless:check

check: ## Run all quality checks (format, style, bugs)
	@echo "Running quality checks..."
	@echo "Checking code formatting..."
	mvn spotless:check
	@if mvn help:evaluate -Dexpression=maven-checkstyle-plugin.version -q -DforceStdout > /dev/null 2>&1; then \
		echo "Running Checkstyle..."; \
		mvn checkstyle:check; \
	fi
	@if mvn help:evaluate -Dexpression=spotbugs-maven-plugin.version -q -DforceStdout > /dev/null 2>&1; then \
		echo "Running SpotBugs..."; \
		mvn spotbugs:check; \
	fi
	@echo "Quality checks completed!"

deps-check: ## Check for dependency updates
	mvn versions:display-dependency-updates versions:display-plugin-updates

deps-tree: ## Show dependency tree
	mvn dependency:tree

docker-build: ## Build Docker image
	docker build -t spring-dude:latest .

docker-run: ## Run Docker container (uses .env if exists, otherwise env_file)
	@if [ -f .env ]; then \
		docker run --env-file .env -p 8080:8080 spring-dude:latest; \
	else \
		docker run --env-file env_file -p 8080:8080 spring-dude:latest; \
	fi

docker-build-native: ## Build Docker image with native executable (uses Dockerfile.native)
	@echo "Building native Docker image (this will take 10-20 minutes)..."
	docker build -f Dockerfile.native -t spring-dude-native:latest .
	@echo ""
	@echo "Native Docker image built: spring-dude-native:latest"
	@echo "Image size: $$(docker images spring-dude-native:latest --format '{{.Size}}')"

docker-run-native: ## Run native Docker container (uses .env if exists, otherwise env_file)
	@if [ -f .env ]; then \
		docker run --env-file .env -p 8080:8080 spring-dude-native:latest; \
	else \
		docker run --env-file env_file -p 8080:8080 spring-dude-native:latest; \
	fi

docker-compose-up: ## Start docker-compose services (MySQL, Redis)
	docker-compose up -d
	@echo "Services started. Waiting for MySQL to be ready..."
	@sleep 5
	@echo "MySQL and Redis should be ready now!"

docker-compose-down: ## Stop docker-compose services
	docker-compose down

docker-compose-logs: ## Show docker-compose logs
	docker-compose logs -f

setup: ## Initial setup - install dependencies and start services
	mvn clean install -DskipTests
	docker-compose up -d
	@echo "Setup complete! Services are starting..."

all: clean build test ## Clean, build, and test

benchmark: ## Run full benchmark comparison (native vs non-native)
	@echo "=========================================="
	@echo "  Benchmark: Native vs Non-Native"
	@echo "=========================================="
	@echo ""
	@echo "Step 1: Building both versions..."
	@echo ""
	@$(MAKE) build-native 2>&1 | grep -E "(Building|Native executable built|Size)" || true
	@$(MAKE) build 2>&1 | grep -E "(Building|BUILD SUCCESS)" | head -3 || true
	@echo ""
	@echo "Step 2: Comparing binary sizes..."
	@$(MAKE) benchmark-compare
	@echo ""
	@echo "Step 3: Benchmarking startup time and memory..."
	@echo ""
	@echo "⚠️  Note: For runtime benchmarks, start each version separately:"
	@echo "  - Native: make benchmark-native"
	@echo "  - JAR:    make benchmark-jar"

benchmark-native: ## Benchmark native executable (startup time, memory, size)
	@echo "=========================================="
	@echo "  Benchmark: Native Executable"
	@echo "=========================================="
	@if [ ! -f venus/target/venus ]; then \
		echo "✗ Native executable not found. Building..."; \
		$(MAKE) build-native > /dev/null 2>&1; \
	fi
	@echo ""
	@echo "📦 Binary Size:"
	@ls -lh venus/target/venus | awk '{print "   " $$5}'
	@echo ""
	@echo "⏱️  Startup Time Test:"
	@echo "   Starting native executable (will timeout after 10s)..."
	@if [ -f .env ]; then set -a; . ./.env; set +a; fi; \
	TIME_START=$$(date +%s%N); \
	timeout 10s ./venus/target/venus > /tmp/native-startup.log 2>&1 & \
	NATIVE_PID=$$!; \
	sleep 2; \
	if ps -p $$NATIVE_PID > /dev/null 2>&1; then \
		TIME_END=$$(date +%s%N); \
		STARTUP_TIME=$$(echo "scale=3; ($$TIME_END - $$TIME_START) / 1000000000" | bc); \
		echo "   ✓ Started in: $$STARTUP_TIME seconds"; \
		echo "   Process PID: $$NATIVE_PID"; \
		echo ""; \
		echo "💾 Memory Usage (RSS):"; \
		ps -o pid,rss,command -p $$NATIVE_PID 2>/dev/null | tail -1 | awk '{printf "   RSS: %.2f MB\n", $$2/1024}'; \
		echo ""; \
		echo "   To stop: kill $$NATIVE_PID"; \
		echo "   Or run: pkill -f venus/target/venus"; \
	else \
		echo "   ✗ Failed to start (check logs: /tmp/native-startup.log)"; \
	fi

benchmark-jar: ## Benchmark regular JAR (startup time, memory, size)
	@echo "=========================================="
	@echo "  Benchmark: Regular JAR"
	@echo "=========================================="
	@if [ ! -f venus/target/venus-*.jar ]; then \
		echo "✗ JAR not found. Building..."; \
		$(MAKE) build > /dev/null 2>&1; \
	fi
	@JAR_FILE=$$(ls venus/target/venus-*.jar 2>/dev/null | grep -v original | head -1); \
	if [ -z "$$JAR_FILE" ]; then \
		echo "✗ JAR file not found"; \
		exit 1; \
	fi
	@echo ""
	@echo "📦 JAR Size:"
	@ls -lh $$JAR_FILE | awk '{print "   " $$5}'
	@echo ""
	@echo "⏱️  Startup Time Test:"
	@echo "   Starting Spring Boot JAR (will timeout after 30s)..."
	@if [ -f .env ]; then set -a; . ./.env; set +a; fi; \
	TIME_START=$$(date +%s%N); \
	timeout 30s java -jar $$JAR_FILE > /tmp/jar-startup.log 2>&1 & \
	JAR_PID=$$!; \
	sleep 5; \
	if ps -p $$JAR_PID > /dev/null 2>&1; then \
		TIME_END=$$(date +%s%N); \
		STARTUP_TIME=$$(echo "scale=3; ($$TIME_END - $$TIME_START) / 1000000000" | bc); \
		echo "   ✓ Started in: $$STARTUP_TIME seconds"; \
		echo "   Process PID: $$JAR_PID"; \
		echo ""; \
		echo "💾 Memory Usage (RSS):"; \
		ps -o pid,rss,command -p $$JAR_PID 2>/dev/null | tail -1 | awk '{printf "   RSS: %.2f MB\n", $$2/1024}'; \
		echo ""; \
		echo "   To stop: kill $$JAR_PID"; \
		echo "   Or run: pkill -f 'java.*venus.*jar'"; \
	else \
		echo "   ✗ Failed to start (check logs: /tmp/jar-startup.log)"; \
	fi

benchmark-compare: ## Compare binary sizes and show summary
	@echo "=========================================="
	@echo "  Size Comparison"
	@echo "=========================================="
	@NATIVE_SIZE=0; \
	JAR_SIZE=0; \
	if [ -f venus/target/venus ]; then \
		NATIVE_SIZE=$$(stat -f%z venus/target/venus 2>/dev/null || stat -c%s venus/target/venus 2>/dev/null); \
		echo "✓ Native executable: $$(ls -lh venus/target/venus | awk '{print $$5}')"; \
	else \
		echo "✗ Native executable not found"; \
	fi; \
	JAR_FILE=$$(ls venus/target/venus-*.jar 2>/dev/null | grep -v original | head -1); \
	if [ -n "$$JAR_FILE" ] && [ -f "$$JAR_FILE" ]; then \
		JAR_SIZE=$$(stat -f%z "$$JAR_FILE" 2>/dev/null || stat -c%s "$$JAR_FILE" 2>/dev/null); \
		echo "✓ JAR file: $$(ls -lh $$JAR_FILE | awk '{print $$5}')"; \
	else \
		echo "✗ JAR file not found"; \
	fi; \
	if [ $$NATIVE_SIZE -gt 0 ] && [ $$JAR_SIZE -gt 0 ]; then \
		echo ""; \
		echo "📊 Comparison:"; \
		RATIO=$$(echo "scale=2; $$NATIVE_SIZE / $$JAR_SIZE" | bc); \
		if [ $$(echo "$$RATIO < 1" | bc) -eq 1 ]; then \
			echo "   Native is $$(echo "scale=1; (1 - $$RATIO) * 100" | bc)% smaller"; \
		else \
			echo "   Native is $$(echo "scale=1; ($$RATIO - 1) * 100" | bc)% larger"; \
		fi; \
	fi
