#!/bin/sh
echo "curl --data \"name=\$GL_REPO\"  https://$1/callback/git" > itpkg
chmod +x itpkg
