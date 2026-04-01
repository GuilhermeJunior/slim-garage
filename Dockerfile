FROM eclipse-temurin:21-jdk-jammy as builder

WORKDIR /app

COPY gradlew .
COPY gradlew.bat .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

COPY src src

RUN chmod +x gradlew && ./gradlew clean build -x test

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /app/build/libs/slim-garage-*.jar app.jar

EXPOSE 8080

ENV SPRING_DATASOURCE_URL="jdbc:mysql://mysql:3306/slim_garage?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"
ENV SPRING_DATASOURCE_USERNAME="app_user"
ENV SPRING_DATASOURCE_PASSWORD="app_pass"

ENTRYPOINT ["java", "-jar", "app.jar"]

