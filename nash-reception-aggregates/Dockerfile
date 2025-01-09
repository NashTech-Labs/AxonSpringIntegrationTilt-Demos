FROM openjdk:21-slim
WORKDIR /app
COPY build/libs/nash-reception-aggregates.jar .
ENTRYPOINT ["java", "-jar", "/app/nash-reception-aggregates.jar"]
EXPOSE 8080