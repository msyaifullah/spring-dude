#!/bin/bash
# ============================================================================
# Development Runner Script
# ============================================================================
# Usage:
#   ./dev.sh              - Run with auto-reload (default)
#   ./dev.sh debug        - Run with debug mode (port 5005)
#   ./dev.sh clean        - Clean and run
#   ./dev.sh test         - Run tests
#   ./dev.sh build        - Build without running
# ============================================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Project directories
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VENUS_DIR="$PROJECT_ROOT/venus"

# Print colored message
print_msg() {
    echo -e "${GREEN}[DEV]${NC} $1"
}

print_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Show banner
show_banner() {
    echo -e "${BLUE}"
    echo "╔═══════════════════════════════════════════════════════════╗"
    echo "║          Spring-Dude Development Server                   ║"
    echo "║                                                           ║"
    echo "║  Auto-reload enabled - Changes will restart the server   ║"
    echo "╚═══════════════════════════════════════════════════════════╝"
    echo -e "${NC}"
}

# Check prerequisites
check_prereqs() {
    if ! command -v mvn &> /dev/null; then
        print_error "Maven is not installed. Please install Maven first."
        exit 1
    fi

    if ! command -v java &> /dev/null; then
        print_error "Java is not installed. Please install Java 17+ first."
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "$JAVA_VERSION" -lt 17 ]; then
        print_warn "Java version is $JAVA_VERSION. Java 17+ is recommended."
    fi
}

# Build earth module first
build_earth() {
    print_msg "Building earth module..."
    cd "$PROJECT_ROOT"
    mvn install -pl earth -DskipTests -q
    print_msg "Earth module built successfully."
}

# Run in development mode with auto-reload
run_dev() {
    show_banner
    check_prereqs
    build_earth

    print_msg "Starting Venus application with auto-reload..."
    print_info "Profile: local"
    print_info "Port: 8080"
    print_info "H2 Console: http://localhost:8080/h2-console"
    print_info "LiveReload: http://localhost:35729"
    echo ""
    print_msg "Press Ctrl+C to stop the server"
    echo ""

    cd "$PROJECT_ROOT"
    mvn spring-boot:run \
        -pl venus \
        -Dspring-boot.run.profiles=local \
        -Dspring-boot.run.jvmArguments="-Xmx512m -XX:+UseG1GC"
}

# Run in debug mode
run_debug() {
    show_banner
    check_prereqs
    build_earth

    print_msg "Starting Venus application in DEBUG mode..."
    print_info "Profile: local"
    print_info "Port: 8080"
    print_info "Debug Port: 5005"
    print_info "H2 Console: http://localhost:8080/h2-console"
    echo ""
    print_warn "Waiting for debugger to attach on port 5005..."
    echo ""

    cd "$PROJECT_ROOT"
    mvn spring-boot:run \
        -pl venus \
        -Dspring-boot.run.profiles=local \
        -Dspring-boot.run.jvmArguments="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=*:5005 -Xmx512m"
}

# Clean and run
run_clean() {
    print_msg "Cleaning project..."
    cd "$PROJECT_ROOT"
    mvn clean -q
    print_msg "Clean completed."
    run_dev
}

# Run tests
run_tests() {
    print_msg "Running all tests..."
    cd "$PROJECT_ROOT"
    mvn test
}

# Build only
run_build() {
    print_msg "Building project..."
    cd "$PROJECT_ROOT"
    mvn clean install -DskipTests
    print_msg "Build completed successfully."
}

# Force reload (touch trigger file)
force_reload() {
    TRIGGER_FILE="$VENUS_DIR/src/main/resources/.reloadtrigger"
    touch "$TRIGGER_FILE"
    print_msg "Reload triggered!"
}

# Show help
show_help() {
    echo "Usage: ./dev.sh [command]"
    echo ""
    echo "Commands:"
    echo "  (none)    Run with auto-reload (default)"
    echo "  debug     Run with remote debugging (port 5005)"
    echo "  clean     Clean build and run"
    echo "  test      Run all tests"
    echo "  build     Build without running"
    echo "  reload    Force a reload (touch trigger file)"
    echo "  help      Show this help message"
    echo ""
    echo "Examples:"
    echo "  ./dev.sh          # Start development server"
    echo "  ./dev.sh debug    # Start with debugger"
    echo "  ./dev.sh clean    # Clean and start"
}

# Main
case "${1:-}" in
    debug)
        run_debug
        ;;
    clean)
        run_clean
        ;;
    test)
        run_tests
        ;;
    build)
        run_build
        ;;
    reload)
        force_reload
        ;;
    help|--help|-h)
        show_help
        ;;
    "")
        run_dev
        ;;
    *)
        print_error "Unknown command: $1"
        show_help
        exit 1
        ;;
esac
