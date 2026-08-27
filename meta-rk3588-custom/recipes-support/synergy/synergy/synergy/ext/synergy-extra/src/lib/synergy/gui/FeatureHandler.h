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

#pragma once

class AppConfig;
class QDialog;
class QRadioButton;

class FeatureHandler
{
public:
  static FeatureHandler &instance()
  {
    static FeatureHandler instance;
    return instance;
  }

  void handleMainWindow(AppConfig *appConfig);
  void handleSettings(QDialog *parent, QRadioButton *systemScope, QRadioButton *userScope) const;

private:
  void handleSettingsScopeRadioButton(QDialog *parent, QRadioButton *systemScope, QRadioButton *userScope, bool checked)
      const;
  bool
  checkSettingsScopeLicense(QDialog *parent, QRadioButton *systemScope, QRadioButton *userScope, bool showDialog) const;

  AppConfig *m_appConfig = nullptr;
};
