#!/bin/sh

ITPKG_HOME=/var/www/itpkg
PATH="$HOME/.rbenv/bin:$PATH"
export ITPKG_HOME PATH

eval "$(rbenv init -)"

