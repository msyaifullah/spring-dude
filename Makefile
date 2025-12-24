.PHONY: help build test clean run dev format check docker-build docker-run docker-compose-up docker-compose-down

help: ## Show this help message
	@echo "Available commands:"
	@echo ""
	@grep -E '^[a-zA-Z_-]+:.*?## .*$$' $(MAKEFILE_LIST) | awk 'BEGIN {FS = ":.*?## "}; {printf "  \033[36m%-20s\033[0m %s\n", $$1, $$2}'

build: ## Build the project (skip tests)
	mvn clean install -DskipTests

build-test: ## Build the project with tests
	mvn clean install

test: ## Run all tests
	mvn test

test-module: ## Run tests for specific module (usage: make test-module MODULE=earth)
	mvn test -pl $(MODULE)

clean: ## Clean build artifacts
	mvn clean

run: ## Run the application using dev.sh
	./dev.sh

dev: ## Run in development mode
	./dev.sh

debug: ## Run in debug mode
	./dev.sh debug

format: ## Format code using Spotless (if configured)
	@if mvn help:evaluate -Dexpression=spotless-maven-plugin.version -q -DforceStdout > /dev/null 2>&1; then \
		mvn spotless:apply; \
	else \
		echo "Spotless plugin not configured. Skipping formatting."; \
	fi

check: ## Run all quality checks (format, style, bugs)
	@echo "Running quality checks..."
	@if mvn help:evaluate -Dexpression=spotless-maven-plugin.version -q -DforceStdout > /dev/null 2>&1; then \
		mvn spotless:check; \
	fi
	@if mvn help:evaluate -Dexpression=maven-checkstyle-plugin.version -q -DforceStdout > /dev/null 2>&1; then \
		mvn checkstyle:check; \
	fi
	@if mvn help:evaluate -Dexpression=spotbugs-maven-plugin.version -q -DforceStdout > /dev/null 2>&1; then \
		mvn spotbugs:check; \
	fi
	@echo "Quality checks completed!"

deps-check: ## Check for dependency updates
	mvn versions:display-dependency-updates versions:display-plugin-updates

deps-tree: ## Show dependency tree
	mvn dependency:tree

docker-build: ## Build Docker image
	docker build -t spring-dude:latest .

docker-run: ## Run Docker container
	docker run --env-file env_file -p 8080:8080 spring-dude:latest

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

