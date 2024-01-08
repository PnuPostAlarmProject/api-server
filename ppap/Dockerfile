# 1. build
FROM gradle:7.4.1-jdk17 as build

WORKDIR /home/gradle/project

COPY . .

#ENV TESTCONTAINERS_HOST_OVERRIDE=host.docker.internal
RUN gradle wrapper
RUN ./gradlew clean build

# 2. Run the application
FROM eclipse-temurin:17-jdk-jammy

WORKDIR /opt/ppap

COPY --from=build /home/gradle/project/build/libs/ppap.jar .

ENV DATABASE_URI=${DATABASE_URI}
ENV DATABASE_PASSWORD=${DATABASE_PASSWORD}

CMD ["java", "-Dspring.profiles.active=stage", "-jar", "ppap.jar"]