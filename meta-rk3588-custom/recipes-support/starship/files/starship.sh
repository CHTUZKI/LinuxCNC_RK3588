# Starship prompt initialization for bash
# Sourced by /etc/profile.d (login shells) and /etc/bash.bashrc (non-login shells)
if [ -x /usr/bin/starship ]; then
    export STARSHIP_CONFIG="${STARSHIP_CONFIG:-/etc/starship.toml}"
    eval "$(starship init bash)"
fi
