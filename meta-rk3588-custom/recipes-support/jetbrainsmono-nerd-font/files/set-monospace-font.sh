#!/bin/sh
# Set JetBrainsMono Nerd Font as the system-wide default monospace font
# so that xfce4-terminal renders Powerline/dev icons correctly with Starship.

FONT="JetBrainsMono Nerd Font"

# XFCE xsettings (MonospaceFontName uses Pango font description)
xfconf-query -c xsettings -p /Gtk/MonospaceFontName -s "${FONT} 10" -n -t string 2>/dev/null \
	|| xfconf-query -c xsettings -p /Gtk/MonospaceFontName -s "${FONT} 10" 2>/dev/null

# gsettings (org.gnome.desktop.interface monospace-font-name)
GSETTINGS_BACKEND=dconf gsettings set org.gnome.desktop.interface monospace-font-name \
	"'${FONT} 10'" 2>/dev/null

exit 0
