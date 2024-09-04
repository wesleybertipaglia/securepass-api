FROM maven:latest as build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=build /app/target/securepass-0.0.1-SNAPSHOT.jar /app/securepass.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "securepass.jar"]