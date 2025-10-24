# Use OpenJDK 25 as base image
FROM openjdk:25-jdk-slim

# Set working directory
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY owasp-suppressions.xml .

# Copy source code
COPY src src

# Make gradlew executable
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build -x test --no-daemon

# Copy the built JAR
RUN cp build/libs/microservice-a.jar app.jar

# Expose ports
EXPOSE 8080 8081

# Set JVM arguments for Java 25
ENV JAVA_OPTS="-Xms256m -Xmx512m --enable-preview -XX:+UseG1GC -XX:+UseContainerSupport"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]