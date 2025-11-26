# ========================================
# ETAPA 1: BUILD (Compilación)
# ========================================
FROM alpine:latest as build

RUN apk update
RUN apk add openjdk17

COPY . .
RUN chmod +x ./gradlew
RUN ./gradlew bootJar --no-daemon

# ========================================
# ETAPA 2: RUNTIME (Ejecución)
# ========================================
# Imagen estable recomendada para Java 17 en Render
FROM eclipse-temurin:17-jre-jammy

EXPOSE 8080

COPY --from=build ./build/libs/*.jar ./app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]
