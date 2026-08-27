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

macro(configure_extra)

  if (${SYNERGY_EXTRA_ROOT} STREQUAL "")
    message(FATAL_ERROR "SYNERGY_EXTRA_ROOT not set")
  endif()

  include_directories("${SYNERGY_EXTRA_ROOT}/src/lib")
  include_directories("${PROJECT_BINARY_DIR}/synergy/lib")

  configure_meta()
  configure_dist()
  configure_bin_names()
  configure_gui_hook()

  option(SYNERGY_VERSION_CHECK "Enable version check" ON)
  if (SYNERGY_VERSION_CHECK)
    message(STATUS "Enabling version check")
    add_definitions(-DSYNERGY_VERSION_CHECK)
  endif()

endmacro()

macro(configure_gui_hook)

  set(DESKFLOW_GUI_HOOK_HEADER "synergy/hooks/gui_hook.h")
  set(DESKFLOW_GUI_HOOK_LIB "synergy-gui")

  add_definitions(-DDESKFLOW_GUI_HOOK_HEADER="${DESKFLOW_GUI_HOOK_HEADER}")

  set(DESKFLOW_GUI_HOOK_MAIN_WINDOW
    "synergy::hooks::onMainWindow(this, &m_AppConfig, &m_CoreProcess);"
  )
  set(DESKFLOW_GUI_HOOK_APP_START
    "if (!synergy::hooks::onAppStart()) return 0;"
  )
  set(DESKFLOW_GUI_HOOK_SETTINGS
    "synergy::hooks::onSettings(\
        this, m_pCheckBoxEnableTls, m_pCheckBoxInvertConnection, \
        m_pRadioSystemScope, m_pRadioUserScope);"
  )
  set(DESKFLOW_GUI_HOOK_VERSION
    "synergy::hooks::onVersionCheck(url);"
  )
  set(DESKFLOW_GUI_HOOK_CORE_START
    "if (!synergy::hooks::onCoreStart()) return;"
  )
  set(DESKFLOW_GUI_HOOK_TEST_START
    "synergy::hooks::onTestStart();"
  )

  configure_file(${SYNERGY_EXTRA_ROOT}/src/lib/synergy/hooks/gui_hook_config.h.in
                 ${PROJECT_BINARY_DIR}/synergy/lib/synergy/hooks/gui_hook_config.h)

endmacro()

macro(configure_meta)

  if(NOT "${SYNERGY_PRODUCT_NAME}" STREQUAL "")
    message(STATUS "Using configured product name")
  else()
    message(STATUS "Using default product name")
    set(SYNERGY_PRODUCT_NAME
        "Synergy (developer build)"
        CACHE STRING "Product name")
  endif()

  message(STATUS "Product name: ${SYNERGY_PRODUCT_NAME}")
  add_definitions(-DSYNERGY_PRODUCT_NAME="${SYNERGY_PRODUCT_NAME}")

  set(DESKFLOW_APP_ID
      "synergy"
      CACHE STRING "ID of the app for filenames, etc")

  set(DESKFLOW_APP_NAME
      "Synergy"
      CACHE STRING "Name of the app")

  set(DESKFLOW_DOMAIN
      "symless.com"
      CACHE STRING "Domain of the app maintainer (not a URL)")

  set(DESKFLOW_AUTHOR_NAME
      "Symless"
      CACHE STRING "Author name")

  set(DESKFLOW_MAINTAINER
      "Synergy <support@symless.com>"
      CACHE STRING "Maintainer email address in RFC 5322 mailbox format")

  set(DESKFLOW_WEBSITE_URL
      "https://symless.com/synergy"
      CACHE STRING "URL of the app website")

  set(DESKFLOW_VERSION_URL
      "https://api.symless.com/version/synergy1"
      CACHE STRING "URL to get the latest version")

  set(DESKFLOW_HELP_TEXT
      "Get technical support"
      CACHE STRING "Text label for the help menu item")

  set(DESKFLOW_RES_DIR
      "${SYNERGY_EXTRA_ROOT}/res"
      CACHE STRING "Path to the res directory")

  set(DESKFLOW_MAC_BUNDLE_CODE
      "SYN1"
      CACHE STRING "Mac bundle code (4 characters)")

  set(DESKFLOW_SHOW_DEV_THANKS
      false
      CACHE BOOL "Show developer thanks message")

  add_definitions(-DDESKFLOW_APP_ID="${DESKFLOW_APP_ID}")
  add_definitions(-DDESKFLOW_APP_NAME="${DESKFLOW_APP_NAME}")

endmacro()

macro(configure_dist)

  set(DESKFLOW_MSI_64_GUID
      "E8A4FA54-14B9-4FD1-8E00-7BC46555FDA0"
      CACHE STRING "GUID for 64-bit MSI installer")

  set(DESKFLOW_MSI_32_GUID
      "BE0B9FD8-45E2-4A8E-A0D8-1F774D074A78"
      CACHE STRING "GUID for 32-bit MSI installer")

  set(DESKFLOW_BUNDLE_SOURCE_DIR
      ${SYNERGY_EXTRA_ROOT}/res/dist/mac/bundle
      CACHE PATH "Path to the macOS app bundle")

endmacro()

macro(configure_bin_names)

  set(GUI_BINARY_NAME
      "synergy"
      CACHE STRING "Filename of the GUI binary")

  set(SERVER_BINARY_NAME
      "synergy-server"
      CACHE STRING "Filename of the server binary")

  set(CLIENT_BINARY_NAME
      "synergy-client"
      CACHE STRING "Filename of the client binary")

  set(CORE_BINARY_NAME
      "synergy-core"
      CACHE STRING "Filename of the core binary")

  set(DAEMON_BINARY_NAME
      "synergy-daemon"
      CACHE STRING "Filename of the daemon binary")

  set(LEGACY_BINARY_NAME
      "synergy-legacy"
      CACHE STRING "Filename of the legacy binary")

endmacro()

configure_extra()
