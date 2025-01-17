FROM amazoncorretto:21-alpine3.15

WORKDIR /app

COPY build/libs/tenpo-backend-challenge-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]