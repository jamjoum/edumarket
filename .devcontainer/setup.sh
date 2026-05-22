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
