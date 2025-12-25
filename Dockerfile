# ---------- Build stage ----------
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build

# Copy only pom first (better caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source and build
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre-alpine

# OpenShift runs containers as random UID
# so we must allow group write access
RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir /app \
    && chown -R spring:spring /app \
    && chmod -R g=u /app

WORKDIR /app

COPY --from=build /build/target/quiz-application-1.0-SNAPSHOT.jar app.jar

EXPOSE 8081

USER spring

ENTRYPOINT ["java","-jar","/app/app.jar"]
