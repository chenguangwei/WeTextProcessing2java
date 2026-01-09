# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file LICENSE.rst or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION 4.2.1)

# Reject any attempt to use a toolchain file. We must not use one because
# we could be downloading it here. If the CMAKE_TOOLCHAIN_FILE environment
# variable is set, the cache variable will have been initialized from it.
unset(CMAKE_TOOLCHAIN_FILE CACHE)
unset(ENV{CMAKE_TOOLCHAIN_FILE})

# We name the project and the target for the ExternalProject_Add() call
# to something that will highlight to the user what we are working on if
# something goes wrong and an error message is produced.

project(openfst-populate NONE)



include(ExternalProject)
ExternalProject_Add(openfst-populate
                     "UPDATE_DISCONNECTED" "False" "DOWNLOAD_EXTRACT_TIMESTAMP" "TRUE" "EXTERNALPROJECT_INTERNAL_ARGUMENT_SEPARATOR" "URL" "https://github.com/kkm000/openfst/archive/refs/tags/win/1.7.2.1.tar.gz" "URL_HASH" "SHA256=e04e1dabcecf3a687ace699ccb43a8a27da385777a56e69da6e103344cc66bca" "PATCH_COMMAND" "/opt/homebrew/bin/cmake" "-E" "copy_directory" "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/patch/openfst" "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/openfst-src"
                    SOURCE_DIR          "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/openfst-src"
                    BINARY_DIR          "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/openfst-build"
                    CONFIGURE_COMMAND   ""
                    BUILD_COMMAND       ""
                    INSTALL_COMMAND     ""
                    TEST_COMMAND        ""
                    USES_TERMINAL_DOWNLOAD  YES
                    USES_TERMINAL_UPDATE    YES
                    USES_TERMINAL_PATCH     YES
)


