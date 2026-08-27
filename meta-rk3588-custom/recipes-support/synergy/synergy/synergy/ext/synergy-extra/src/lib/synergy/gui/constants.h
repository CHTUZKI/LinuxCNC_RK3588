/*
 * synergy -- mouse and keyboard sharing utility
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

#pragma once

#include <QString>

#include <chrono>

namespace synergy::gui {

const auto kProProductName = "Synergy 1 Pro";
const auto kBusinessProductName = "Synergy 1 Business";

const auto kUrlApi = "https://symless.com/api";
const auto kUrlWebsite = DESKFLOW_WEBSITE_URL;
const auto kUrlSourceQuery = "utm_source=gui-s1";

const auto kLinkBuy = R"(<a href="%1" style="color: %2">Buy now</a>)";
const auto kLinkRenew = R"(<a href="%1" style="color: %2">Renew now</a>)";
const auto kLinkDownload = R"(<a href="%1" style="color: %2">Download now</a>)";

const auto kUrlPersonalUpgrade = QString("%1/purchase/upgrade?%2").arg(kUrlWebsite, kUrlSourceQuery);
const auto kUrlContact = QString("%1/contact?%2").arg(kUrlWebsite, kUrlSourceQuery);

const auto kUrlApiLicenseActivate = QString("%1/product/activate").arg(kUrlApi);
const auto kUrlApiLicenseCheck = QString("%1/product/check").arg(kUrlApi);

constexpr auto kLicenseGracePeriod = std::chrono::days{14};

} // namespace synergy::gui
