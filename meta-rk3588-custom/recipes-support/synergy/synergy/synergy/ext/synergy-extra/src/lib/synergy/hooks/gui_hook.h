/*
 * Synergy -- mouse and keyboard sharing utility
 * Copyright (C) 2024 - 2025 Symless Ltd.
 *
 * This package is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * found in the file LICENSE that should have accompanied this file.
 *
 * This package is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

#pragma once

#include "synergy/gui/FeatureHandler.h"
#include "synergy/gui/license/LicenseHandler.h"
#include "synergy/hooks/gui_hook_config.h" // IWYU pragma: keep

#include <QCheckBox>
#include <QDialog>
#include <QMainWindow>
#include <QRadioButton>

namespace deskflow::gui {
class CoreProcess;
}

namespace synergy::hooks {

inline void onMainWindow(QMainWindow *mainWindow, AppConfig *appConfig, deskflow::gui::CoreProcess *coreProcess)
{
  LicenseHandler::instance().handleMainWindow(mainWindow, appConfig, coreProcess);
  FeatureHandler::instance().handleMainWindow(appConfig);
}

inline bool onAppStart()
{
  return LicenseHandler::instance().handleAppStart();
}

inline void onSettings(
    QDialog *parent, QCheckBox *enableTls, QCheckBox *invertConnection, QRadioButton *systemScope,
    QRadioButton *userScope
)
{
  LicenseHandler::instance().handleSettings(parent, enableTls, invertConnection, systemScope, userScope);
  FeatureHandler::instance().handleSettings(parent, systemScope, userScope);
}

inline void onVersionCheck(QString &versionUrl)
{
  return LicenseHandler::instance().handleVersionCheck(versionUrl);
}

inline bool onCoreStart()
{
  return LicenseHandler::instance().handleCoreStart();
}

inline void onTestStart()
{
  LicenseHandler::instance().disable();
}

} // namespace synergy::hooks
