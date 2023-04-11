FROM openjdk:17.0.2-jdk

ARG JAR_FILE=build/libs/*.jar

COPY ${JAR_FILE} app.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/app.jar"]
