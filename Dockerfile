FROM maven:3.9.4-eclipse-temurin-20 AS build
WORKDIR /app
COPY ai-support/pom.xml .
COPY ai-support/src ./src
RUN mvn clean package -DskipTests

FROM eclipse-temurin:20-jdk
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
