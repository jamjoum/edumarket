# ═══════════════════════════════════════════════════════════════
# Stage 1 – Build (Maven + Java 21)
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jdk-alpine AS builder

WORKDIR /build

# Copie uniquement le pom.xml pour tirer parti du cache Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B --no-transfer-progress 2>/dev/null || true

# Copie du code source et build
COPY src ./src
RUN ./mvnw clean package -DskipTests --no-transfer-progress 2>/dev/null || \
    mvn clean package -DskipTests --no-transfer-progress

# ═══════════════════════════════════════════════════════════════
# Stage 2 – Runtime (JRE uniquement)
# ═══════════════════════════════════════════════════════════════
FROM eclipse-temurin:21-jre-alpine AS runtime

# Sécurité : utilisateur non-root
RUN addgroup -S edumarket && adduser -S edumarket -G edumarket
USER edumarket

WORKDIR /app

COPY --from=builder /build/target/*.jar app.jar

# Virtual Threads : pas de configuration JVM supplémentaire nécessaire
# spring.threads.virtual.enabled=true dans application.yml suffit
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseZGC"

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
  CMD wget -qO- http://localhost:8080/actuator/health | grep -q '"status":"UP"'

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
