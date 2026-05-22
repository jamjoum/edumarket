#!/usr/bin/env bash
set -euo pipefail

echo "[add-yarn-key] Adding Yarn APT key to avoid NO_PUBKEY errors..."

if command -v apt-get >/dev/null 2>&1; then
  export DEBIAN_FRONTEND=noninteractive
  apt-get install -y --no-install-recommends gnupg curl ca-certificates

  KEYRING_DIR=/usr/share/keyrings
  mkdir -p "$KEYRING_DIR"

  tmpkey=$(mktemp)
  trap 'rm -f "$tmpkey"' EXIT
  curl -fsSL https://dl.yarnpkg.com/debian/pubkey.gpg -o "$tmpkey"

  if gpg --batch --yes --dearmor -o "$KEYRING_DIR/yarn-archive-keyring.gpg" "$tmpkey"; then
    echo "[add-yarn-key] Yarn key installed to $KEYRING_DIR/yarn-archive-keyring.gpg"
  else
    echo "[add-yarn-key] fallback: apt-key add (deprecated)"
    apt-key add "$tmpkey"
  fi

  chmod 644 "$KEYRING_DIR/yarn-archive-keyring.gpg" || true
  echo "[add-yarn-key] Refreshing apt package index after adding key..."
  apt-get update -y
else
  echo "[add-yarn-key] apt-get not found — skipping key installation"
fi

echo "[add-yarn-key] done"
