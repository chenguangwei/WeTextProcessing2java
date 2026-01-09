# Distributed under the OSI-approved BSD 3-Clause License.  See accompanying
# file LICENSE.rst or https://cmake.org/licensing for details.

cmake_minimum_required(VERSION ${CMAKE_VERSION}) # this file comes with cmake

# If CMAKE_DISABLE_SOURCE_CHANGES is set to true and the source directory is an
# existing directory in our source tree, calling file(MAKE_DIRECTORY) on it
# would cause a fatal error, even though it would be a no-op.
if(NOT EXISTS "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-src")
  file(MAKE_DIRECTORY "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-src")
endif()
file(MAKE_DIRECTORY
  "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-build"
  "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix"
  "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix/tmp"
  "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix/src/gflags-populate-stamp"
  "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix/src"
  "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix/src/gflags-populate-stamp"
)

set(configSubDirs )
foreach(subDir IN LISTS configSubDirs)
    file(MAKE_DIRECTORY "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix/src/gflags-populate-stamp/${subDir}")
endforeach()
if(cfgdir)
  file(MAKE_DIRECTORY "/Users/chenguangwei/Documents/workspace/WeTextProcessing/runtime/fc_base-AppleClang/gflags-subbuild/gflags-populate-prefix/src/gflags-populate-stamp${cfgdir}") # cfgdir has leading slash
endif()
