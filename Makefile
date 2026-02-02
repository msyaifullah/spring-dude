.PHONY: help build test clean run dev format format-check check coverage coverage-report coverage-check docker-build docker-run docker-build-native docker-run-native docker-compose-up docker-compose-down build-native run-native

help: ## Show this help message
	@echo "Available commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project (skip tests)
	mvn clean install -DskipTests

build-test: ## Build the project with tests
	mvn clean install

check-graalvm: ## Check if GraalVM is installed and configured
	@echo "Checking GraalVM installation..."
	@if command -v java &> /dev/null; then \
		JAVA_HOME=$$(java -XshowSettings:properties -version 2>&1 | grep -E "java.home" | cut -d'=' -f2 | tr -d ' '); \
		if [ -f "$$JAVA_HOME/bin/native-image" ] || [ -n "$$GRAALVM_HOME" ]; then \
			echo "✓ GraalVM found at: $$JAVA_HOME"; \
			java -version 2>&1 | head -n 1; \
			if [ -f "$$JAVA_HOME/bin/native-image" ]; then \
				echo "✓ native-image tool found"; \
			else \
				echo "⚠ native-image tool not found. Install it with: gu install native-image"; \
			fi; \
		else \
			echo "✗ GraalVM not found in JAVA_HOME: $$JAVA_HOME"; \
			echo ""; \
			echo "To install GraalVM LTS with SDKMAN:"; \
			echo "  1. sdk list java | grep -E 'graalvm|graalce'"; \
			echo "  2. sdk install java 21.0.1-graalce  # Latest LTS (Java 21)"; \
			echo "     # Or: sdk install java 17.x-graalce  # Java 17 LTS"; \
			echo "  3. sdk use java 21.0.1-graalce"; \
			echo "  4. gu install native-image"; \
			exit 1; \
		fi; \
	else \
		echo "✗ Java not found. Please install Java/GraalVM first."; \
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

