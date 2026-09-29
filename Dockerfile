FROM maven:3.9.11-eclipse-temurin-17-alpine AS build

WORKDIR /app

# Cacheia dependências enquanto o pom.xml não mudar.
COPY pom.xml ./
RUN mvn -B -ntp dependency:resolve

COPY src ./src
RUN mvn -B -ntp -Dmaven.test.skip=true package

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
COPY --from=build --chown=app:app /app/target/*.jar /app/app.jar

USER app
EXPOSE 8080

ENTRYPOINT ["java", "-XX:MaxRAMPercentage=70.0", "-XX:+UseSerialGC", "-jar", "/app/app.jar"]
