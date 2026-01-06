FROM eclipse-temurin:17-jdk-alpine

LABEL org.opencontainers.image.title="gymplus-backend"
LABEL org.opencontainers.image.description="Backend GymPlus - Spring Boot Application"
LABEL org.opencontainers.image.version="latest"

ENV TZ=America/Guayaquil
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

WORKDIR /app

# Point to the specific jar based on pom.xml artifactId and version
ARG JAR_FILE=target/backend-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:+UseG1GC -Dfile.encoding=UTF-8 -Duser.timezone=America/Guayaquil"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -Dspring.profiles.active=prod -jar app.jar"]
