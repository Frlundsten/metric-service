FROM maven:3.9.9-eclipse-temurin-24-alpine AS build
COPY ./src /app/src
COPY pom.xml /app
RUN mvn -f /app/pom.xml clean package -DskipTests
  
FROM eclipse-temurin:24-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
COPY --from=build /app/target/lib lib

EXPOSE 8080
ENTRYPOINT ["java","--enable-preview", "-jar", "app.jar"]