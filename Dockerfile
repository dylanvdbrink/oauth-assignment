FROM maven:3.9.0-eclipse-temurin-17-focal AS build

COPY . .

RUN mvn clean package -DskipTests

FROM amazoncorretto:17 AS run

COPY --from=build target/oauth-assignment-*.jar .
RUN mv oauth-assignment-*.jar oauth-assignment.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "oauth-assignment.jar"]