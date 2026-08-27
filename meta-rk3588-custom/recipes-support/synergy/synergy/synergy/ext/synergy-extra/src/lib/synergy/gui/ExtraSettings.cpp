/*
 * Synergy -- mouse and keyboard sharing utility
 * Copyright (C) 2024 Symless Ltd.
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

#include "ExtraSettings.h"

#include <QSettings>
#include <QtCore>

using namespace deskflow::gui::proxy;

namespace synergy::gui {

const auto kSerialKeySettingKey = "serialKey";
const auto kActivatedSettingKey = "activated";
const auto kGraceStartSettingKey = "graceStartEpochSecs";

void ExtraSettings::load()
{
  const auto &settings = getActiveSettings();
  m_serialKey = settings.value(kSerialKeySettingKey).toString();
  m_activated = settings.value(kActivatedSettingKey).toBool();
  m_graceStartEpochSecs = settings.value(kGraceStartSettingKey).toLongLong();
}

void ExtraSettings::sync()
{
  auto &settings = getActiveSettings();
  if (!settings.isWritable()) {
    qCritical() << "unable to save to settings, file not writable:" << settings.fileName();
    return;
  }

  settings.setValue(kSerialKeySettingKey, m_serialKey);
  settings.setValue(kActivatedSettingKey, m_activated);
  settings.setValue(kGraceStartSettingKey, m_graceStartEpochSecs);
  settings.sync();
}

} // namespace synergy::gui
