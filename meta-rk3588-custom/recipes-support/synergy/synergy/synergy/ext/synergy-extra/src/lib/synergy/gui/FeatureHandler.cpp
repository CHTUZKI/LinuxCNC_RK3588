/*
 * Synergy -- mouse and keyboard sharing utility
 * Copyright (C) 2025 Symless Ltd.
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

#include "FeatureHandler.h"

#include "dialogs/UpgradeDialog.h"
#include "gui/config/AppConfig.h"
#include "gui/constants.h"
#include "gui/diagnostic.h"
#include "license/LicenseHandler.h"
#include "synergy/gui/constants.h"

#include <QApplication>
#include <QDebug>
#include <QDialog>
#include <QMessageBox>
#include <QRadioButton>

using namespace deskflow::gui;
using namespace synergy::gui;

void FeatureHandler::handleMainWindow(AppConfig *appConfig)
{
  m_appConfig = appConfig;
}

void FeatureHandler::handleSettings(QDialog *parent, QRadioButton *systemScope, QRadioButton *userScope) const
{
  const auto onSystemScopeToggle = [this, parent, systemScope, userScope](bool checked) {
    qDebug("system scope radio button toggled");
    systemScope->blockSignals(true);
    userScope->blockSignals(true);
    handleSettingsScopeRadioButton(parent, systemScope, userScope, checked);
    systemScope->blockSignals(false);
    userScope->blockSignals(false);
  };
  QObject::connect(systemScope, &QRadioButton::toggled, onSystemScopeToggle);

  checkSettingsScopeLicense(parent, systemScope, userScope, false);
}

bool FeatureHandler::checkSettingsScopeLicense(
    QDialog *parent, QRadioButton *systemScope, QRadioButton *userScope, bool showDialog
) const
{
  const auto &licenseHandler = LicenseHandler::instance();
  if (!licenseHandler.isEnabled()) {
    qDebug("license handler disabled, skipping settings scope check");
    return true;
  }

  const auto &license = licenseHandler.license();
  if (!license.isSettingsScopeAvailable() && systemScope->isChecked()) {
    qDebug("settings scope not available, showing upgrade dialog");
    userScope->setChecked(true);

    if (showDialog) {
      UpgradeDialog dialog(parent);
      dialog.showDialog(
          QString("Settings Scope"),
          QString("Please upgrade to %1 to enable the settings scope feature.").arg(kBusinessProductName), kUrlContact
      );
    }

    return false;
  }

  return true;
}

void FeatureHandler::handleSettingsScopeRadioButton(
    QDialog *parent, QRadioButton *systemScope, QRadioButton *userScope, bool checked
) const
{
  if (!checkSettingsScopeLicense(parent, systemScope, userScope, true)) {
    qDebug("settings scope not available, skipping feature handler");
    return;
  }

  const auto userScopeText = QStringLiteral("current user");
  const auto systemScopeText = QStringLiteral("all users");
  const auto from = checked ? userScopeText : systemScopeText;
  const auto to = checked ? systemScopeText : userScopeText;
  const auto result = QMessageBox::information(
      parent, "Switch settings profile",
      QString("Switching settings from %1 to %2 requires %3 to restart.\n\n"
              "Would you like to restart the application now?")
          .arg(from, to, QApplication::applicationName()),
      QMessageBox::Yes | QMessageBox::Cancel
  );

  if (result == QMessageBox::Yes) {
    auto &systemSettings = m_appConfig->settings().getSystemSettings();
    systemSettings.loadSystem();

    if (systemSettings.isEmpty()) {
      qDebug("system settings are empty, copying user settings");
      systemSettings.copyFrom(m_appConfig->settings().getUserSettings());
    }

    systemSettings.setValue(kSystemScopeSetting, checked);
    systemSettings.sync();

    // This seems rather clumsy and un-elegant at first glance, but actually when you consider
    // the complexities of hot-switching the settings scope while the application is running,
    // restarting the applocation is actually the lowest maintenance solution.
    deskflow::gui::diagnostic::restart();
  } else {
    if (checked) {
      userScope->setChecked(true);
    } else {
      systemScope->setChecked(true);
    }
  }
}
