# Synergy -- mouse and keyboard sharing utility
# Copyright (C) 2024 - 2025 Symless Ltd.
#
# This package is free software; you can redistribute it and/or
# modify it under the terms of the GNU General Public License
# found in the file LICENSE that should have accompanied this file.
#
# This package is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <http://www.gnu.org/licenses/>.

cmake_minimum_required(VERSION 3.22)

project(synergy-extra C CXX)

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_EXTENSIONS OFF)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_RUNTIME_OUTPUT_DIRECTORY "${PROJECT_BINARY_DIR}/bin")
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY "${PROJECT_BINARY_DIR}/lib")

if(WIN32)
  message(STATUS "Enabling warnings as errors (MSVC)")
  add_compile_options(/WX)
elseif(UNIX)
  message(STATUS "Enabling warnings as errors (GNU/Clang)")
  add_compile_options(-Werror)
endif()

if(APPLE)
  set(CMAKE_OSX_DEPLOYMENT_TARGET "12.0")
endif()

message(STATUS "GUI res dir: ${GUI_RES_DIR}")
set(GUI_QRC_FILE ${GUI_RES_DIR}/app.qrc)

# Activation is off by default to make life easier for contributors.
option(SYNERGY_ENABLE_ACTIVATION "Enable activation dialog, etc" OFF)

if(SYNERGY_ENABLE_ACTIVATION)
  message(STATUS "License activation is enabled")
  add_definitions(-DSYNERGY_ENABLE_ACTIVATION)
else()
  message(STATUS "License activation is disabled")
endif()

find_package(
  Qt6
  COMPONENTS Core Widgets Network
  REQUIRED)

message(STATUS "Qt version: ${Qt6_VERSION}")

add_subdirectory(src)
