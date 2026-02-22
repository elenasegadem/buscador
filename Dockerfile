FROM maven:3.9.12-eclipse-temurin-25 AS build
COPY . .
RUN mvn clean package


FROM eclipse-temurin:25-jre
EXPOSE 8761
COPY --from=build /target/buscador-0.0.1-SNAPSHOT.jar ms-books-catalogue.jar
ENTRYPOINT ["java", "-jar", "ms-books-catalogue.jar"]