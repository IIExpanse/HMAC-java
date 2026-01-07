FROM eclipse-temurin:21.0.9_10-jre-alpine-3.23

COPY /app ./app
WORKDIR /app

ENTRYPOINT [ "java", "-jar", "HMAC-app.jar" ]

EXPOSE 8080