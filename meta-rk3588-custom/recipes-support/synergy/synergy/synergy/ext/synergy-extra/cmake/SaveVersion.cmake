# Synergy -- mouse and keyboard sharing utility
# Copyright (C) 2025 Symless Ltd.
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

include(${CMAKE_CURRENT_LIST_DIR}/Version.cmake)

version_from_git_tags(VERSION VERSION_MAJOR VERSION_MINOR VERSION_PATCH VERSION_REVISION)
message(STATUS "Version: " ${VERSION})

if ("${VERSION_FILE}" STREQUAL "")
  message(FATAL_ERROR "VERSION_FILE not set")
endif()

message(STATUS "Writing to file: ${VERSION_FILE}")
file(WRITE ${VERSION_FILE} ${VERSION})
