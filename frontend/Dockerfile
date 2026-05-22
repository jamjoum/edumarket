# ═══════════════════════════════════════════════════════════════
# Stage 1 – Build Angular
# ═══════════════════════════════════════════════════════════════
FROM node:20-alpine AS builder

WORKDIR /app

# Cache des dépendances npm
COPY package*.json ./
RUN npm ci --prefer-offline

# Build de production
COPY . .
RUN npm run build

# ═══════════════════════════════════════════════════════════════
# Stage 2 – Nginx (serving SPA + proxy API)
# ═══════════════════════════════════════════════════════════════
FROM nginx:1.27-alpine AS runtime

# Copier la config Nginx personnalisée
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers buildés
COPY --from=builder /app/dist/edumarket-frontend/browser /usr/share/nginx/html

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -qO- http://localhost/index.html | grep -q "EduMarket"

CMD ["nginx", "-g", "daemon off;"]
