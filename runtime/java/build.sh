#!/bin/bash
# Copyright (c) 2024 WeTextProcessing Authors. All Rights Reserved.
#
# Build script for WeTextProcessing Java JNI example
#
# Usage:
#   ./build.sh          # Build everything
#   ./build.sh clean    # Clean build artifacts
#   ./build.sh run      # Build and run example

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
RUNTIME_DIR="$(dirname "$SCRIPT_DIR")"
PROJECT_ROOT="$(dirname "$RUNTIME_DIR")"

BUILD_DIR="${SCRIPT_DIR}/build"
CLASSES_DIR="${BUILD_DIR}/classes"
LIB_DIR="${BUILD_DIR}/lib"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

clean() {
    log_info "Cleaning build directory..."
    rm -rf "${BUILD_DIR}"
    log_info "Clean complete."
}

build_native() {
    log_info "Building native libraries..."

    # First, build the main runtime library if not already built
    RUNTIME_BUILD_DIR="${RUNTIME_DIR}/build"
    if [ ! -f "${RUNTIME_BUILD_DIR}/lib/libwetext_processor_c.dylib" ] && \
       [ ! -f "${RUNTIME_BUILD_DIR}/lib/libwetext_processor_c.so" ]; then
        log_info "Building main runtime library first..."
        cmake -B "${RUNTIME_BUILD_DIR}" -S "${RUNTIME_DIR}" -DCMAKE_BUILD_TYPE=Release \
            -DCMAKE_POLICY_VERSION_MINIMUM=3.5
        cmake --build "${RUNTIME_BUILD_DIR}" -j$(nproc 2>/dev/null || sysctl -n hw.ncpu)
    fi

    # Build JNI library
    log_info "Building JNI library..."
    mkdir -p "${BUILD_DIR}"
    cd "${BUILD_DIR}"

    cmake .. \
        -DCMAKE_BUILD_TYPE=Release \
        -DCMAKE_PREFIX_PATH="${RUNTIME_BUILD_DIR}" \
        -DCMAKE_POLICY_VERSION_MINIMUM=3.5

    cmake --build . -j$(nproc 2>/dev/null || sysctl -n hw.ncpu)

    # Copy dependent libraries to lib directory
    mkdir -p "${LIB_DIR}"

    # Find and copy wetext_processor_c library
    if [ -f "${RUNTIME_BUILD_DIR}/processor/libwetext_processor_c.dylib" ]; then
        cp "${RUNTIME_BUILD_DIR}/processor/libwetext_processor_c.dylib" "${LIB_DIR}/"
    elif [ -f "${RUNTIME_BUILD_DIR}/processor/libwetext_processor_c.so" ]; then
        cp "${RUNTIME_BUILD_DIR}/processor/libwetext_processor_c.so" "${LIB_DIR}/"
    fi

    cd "${SCRIPT_DIR}"
    log_info "Native libraries built successfully."
}

build_java() {
    log_info "Compiling Java classes..."

    mkdir -p "${CLASSES_DIR}"

    # Find all Java source files
    JAVA_SOURCES=$(find "${SCRIPT_DIR}/src/main/java" -name "*.java")

    # Compile
    javac -d "${CLASSES_DIR}" ${JAVA_SOURCES}

    log_info "Java classes compiled successfully."
}

build_all() {
    build_native
    build_java
    log_info "Build complete!"
    echo ""
    log_info "To run the example:"
    echo ""
    echo "  # For Chinese TN:"
    echo "  ./build.sh run tn"
    echo ""
    echo "  # For Chinese ITN:"
    echo "  ./build.sh run itn"
    echo ""
    echo "  # With custom text:"
    echo "  ./build.sh run tn \"你的文本\""
    echo ""
}

run_example() {
    MODE="${1:-tn}"
    TEXT="${2:-}"

    # Determine FST paths based on mode
    if [ "$MODE" = "tn" ]; then
        TAGGER="${PROJECT_ROOT}/tn/zh_tn_tagger.fst"
        VERBALIZER="${PROJECT_ROOT}/tn/zh_tn_verbalizer.fst"
    elif [ "$MODE" = "itn" ]; then
        TAGGER="${PROJECT_ROOT}/itn/zh_itn_tagger.fst"
        VERBALIZER="${PROJECT_ROOT}/itn/zh_itn_verbalizer.fst"
    else
        # Assume mode is a path prefix
        TAGGER="${MODE}_tagger.fst"
        VERBALIZER="${MODE}_verbalizer.fst"
    fi

    # Check if FST files exist
    if [ ! -f "$TAGGER" ]; then
        log_error "Tagger FST not found: $TAGGER"
        log_info "Please generate FST files first by running:"
        echo "  python -m tn --text \"测试\" --overwrite_cache"
        echo "  python -m itn --text \"测试\" --overwrite_cache"
        exit 1
    fi

    # Set library path based on OS
    if [[ "$OSTYPE" == "darwin"* ]]; then
        export DYLD_LIBRARY_PATH="${LIB_DIR}:${DYLD_LIBRARY_PATH}"
    else
        export LD_LIBRARY_PATH="${LIB_DIR}:${LD_LIBRARY_PATH}"
    fi

    log_info "Running example..."
    echo ""

    if [ -n "$TEXT" ]; then
        java -Djava.library.path="${LIB_DIR}" \
             -cp "${CLASSES_DIR}" \
             com.wetext.Example "$TAGGER" "$VERBALIZER" "$TEXT"
    else
        java -Djava.library.path="${LIB_DIR}" \
             -cp "${CLASSES_DIR}" \
             com.wetext.Example "$TAGGER" "$VERBALIZER"
    fi
}

# Main entry point
case "${1:-build}" in
    clean)
        clean
        ;;
    native)
        build_native
        ;;
    java)
        build_java
        ;;
    build)
        build_all
        ;;
    run)
        # Ensure build is done
        if [ ! -d "${CLASSES_DIR}" ] || [ ! -d "${LIB_DIR}" ]; then
            build_all
        fi
        run_example "${2:-tn}" "${3:-}"
        ;;
    *)
        echo "Usage: $0 {clean|native|java|build|run [tn|itn] [text]}"
        exit 1
        ;;
esac
