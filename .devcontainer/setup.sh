#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════════════════
# .devcontainer/setup.sh
# Script d'initialisation de l'environnement GitHub Codespaces / Dev Container
# ═══════════════════════════════════════════════════════════════════════════

set -euo pipefail

echo ""
echo "╔══════════════════════════════════════════════════════════╗"
echo "║         EduMarket – Setup Environnement Dev              ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""

# ── Configuration Java ────────────────────────────────────────────────────
echo "🔧 Configuration des chemins Java..."

# Créer les répertoires nécessaires
mkdir -p /usr/lib/jvm

# Détecter et créer un symlink pour Java via SDKMAN
if [ -d "/usr/local/sdkman/candidates/java" ]; then
  # Le chemin par défaut de SDKMAN après installation
  JAVA_PATH="/usr/local/sdkman/candidates/java/current"
  if [ -L "$JAVA_PATH" ] || [ -d "$JAVA_PATH" ]; then
    ln -sf "$JAVA_PATH" /usr/lib/jvm/java-21-openjdk-amd64 || true
    echo "✅ Symlink Java créé: /usr/lib/jvm/java-21-openjdk-amd64 → $JAVA_PATH"
  fi
fi

# Fallback: Détecter Java via PATH et créer le symlink
if [ ! -d "/usr/lib/jvm/java-21-openjdk-amd64" ]; then
  JAVA_BIN=$(which java 2>/dev/null || echo "")
  if [ -n "$JAVA_BIN" ]; then
    # Résoudre le chemin réel (suivre les symlinks)
    JAVA_HOME=$(readlink -f "$JAVA_BIN" 2>/dev/null | sed 's|/bin/java$||' || dirname "$JAVA_BIN" | sed 's|/bin$||')
    if [ -d "$JAVA_HOME" ]; then
      ln -sf "$JAVA_HOME" /usr/lib/jvm/java-21-openjdk-amd64 || true
      echo "✅ Symlink Java créé (fallback): /usr/lib/jvm/java-21-openjdk-amd64 → $JAVA_HOME"
    fi
  fi
fi

# Exporter JAVA_HOME pour les commandes suivantes
export JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"

echo "ℹ️  JAVA_HOME exportée: $JAVA_HOME"
echo ""

# Installer des paquets système utiles (psql, git, build tools) si apt est disponible
if command -v apt-get >/dev/null 2>&1; then
  echo "🔧 Installation des paquets système requis (psql, git, build-essential, ca-certificates)..."
  export DEBIAN_FRONTEND=noninteractive
  apt-get update -y
  apt-get install -y --no-install-recommends \
    postgresql-client \
    git \
    build-essential \
    ca-certificates \
    wget \
    unzip \
    gnupg \
    curl || true
  echo "✅ Paquets système installés (si disponibles)."
  echo ""
else
  echo "ℹ️  apt-get non trouvé — saut de l'installation des paquets système." 
fi

# ── Vérifications ─────────────────────────────────────────────────────────
echo "🔍 Vérification des outils..."
java  --version
mvn   --version
node  --version
npm   --version
docker --version
echo "✅ Tous les outils sont disponibles."
echo ""

# ── Installation des dépendances Frontend ─────────────────────────────────
echo "📦 Installation des dépendances Angular..."
cd frontend
npm install --legacy-peer-deps
cd ..
echo "✅ Dépendances Angular installées."
echo ""

# ── Téléchargement des dépendances Maven (offline cache) ─────────────────
echo "📦 Téléchargement des dépendances Maven..."
cd backend
mvn dependency:go-offline -q || echo "⚠️  Certaines dépendances Maven nécessitent une connexion."
cd ..
echo "✅ Cache Maven prêt."
echo ""

# ── Démarrage de la stack Docker ─────────────────────────────────────────
if command -v docker >/dev/null 2>&1; then
  echo "🐳 Démarrage de la stack Docker (PostgreSQL)..."
  docker compose up -d postgres

  echo "⏳ Attente démarrage PostgreSQL..."
  timeout 60 bash -c 'until docker compose exec -T postgres pg_isready -U edumarket 2>/dev/null; do sleep 2; done'
  echo "✅ PostgreSQL prêt !"
else
  echo "⚠️  Docker n'est pas disponible dans ce conteneur."
  echo "   La stack PostgreSQL ne sera pas démarrée automatiquement."
fi

echo ""

# ── Résumé ────────────────────────────────────────────────────────────────
echo "╔══════════════════════════════════════════════════════════╗"
echo "║  🚀 Environnement prêt !                                 ║"
echo "╠══════════════════════════════════════════════════════════╣"
echo "║  Lancer le backend  : cd backend && mvn spring-boot:run  ║"
echo "║  Lancer le frontend : cd frontend && npm start           ║"
echo "║  Stack complète     : docker compose up --build          ║"
echo "╠══════════════════════════════════════════════════════════╣"
echo "║  Frontend  → http://localhost:4200                       ║"
echo "║  API REST  → http://localhost:8080/api/v1                ║"
echo "║  Swagger   → http://localhost:8080/swagger-ui.html       ║"
echo "║  DB Admin  → docker compose --profile tools up pgadmin  ║"
echo "╚══════════════════════════════════════════════════════════╝"
echo ""
