FROM openjdk:21-slim
WORKDIR /app
COPY build/libs/nash-reception-projections.jar .
ENTRYPOINT ["java", "-jar", "/app/nash-reception-projections.jar"]
EXPOSE 8080