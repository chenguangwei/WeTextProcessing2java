cmake_minimum_required(VERSION 3.14 FATAL_ERROR)

project(wetext_jni VERSION 0.1)
set(CMAKE_CXX_STANDARD 14)

# Find JNI
find_package(JNI REQUIRED)
if(JNI_FOUND)
  message(STATUS "JNI_INCLUDE_DIRS=${JNI_INCLUDE_DIRS}")
  message(STATUS "JNI_LIBRARIES=${JNI_LIBRARIES}")
endif()

# Include JNI headers
include_directories(${JNI_INCLUDE_DIRS})

# Include parent runtime directory for processor headers
set(RUNTIME_DIR ${CMAKE_CURRENT_SOURCE_DIR}/..)
include_directories(${RUNTIME_DIR})

# Find the pre-built wetext_processor_c library
set(RUNTIME_BUILD_DIR ${RUNTIME_DIR}/build)
find_library(WETEXT_PROCESSOR_C_LIB
  NAMES wetext_processor_c
  PATHS ${RUNTIME_BUILD_DIR}/processor
  NO_DEFAULT_PATH
)

if(NOT WETEXT_PROCESSOR_C_LIB)
  message(FATAL_ERROR "wetext_processor_c library not found. Please build the runtime first.")
endif()

message(STATUS "Found wetext_processor_c: ${WETEXT_PROCESSOR_C_LIB}")

# Build JNI shared library
add_library(wetext_jni SHARED
  src/main/cpp/wetext_jni.cc
)

# Link against wetext_processor_c (the C API shared library)
target_link_libraries(wetext_jni
  ${WETEXT_PROCESSOR_C_LIB}
)

# Set output directory
set_target_properties(wetext_jni PROPERTIES
  LIBRARY_OUTPUT_DIRECTORY ${CMAKE_BINARY_DIR}/lib
)

# Set RPATH to find dependencies at runtime
if(APPLE)
  set_target_properties(wetext_jni PROPERTIES
    SUFFIX ".dylib"
    INSTALL_RPATH "@loader_path"
    BUILD_WITH_INSTALL_RPATH TRUE
  )
elseif(UNIX)
  set_target_properties(wetext_jni PROPERTIES
    SUFFIX ".so"
    INSTALL_RPATH "$ORIGIN"
    BUILD_WITH_INSTALL_RPATH TRUE
  )
endif()
