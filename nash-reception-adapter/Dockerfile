FROM openjdk:21-slim
WORKDIR /app
COPY build/libs/nash-reception-adapter.jar .
ENTRYPOINT ["java", "-jar", "/app/nash-reception-adapter.jar"]
EXPOSE 8080