# Stage 1: Build the Angular application
FROM node:23 AS angular-builder

WORKDIR /src

RUN npm i -g @angular/cli

COPY client/src src
COPY client/*.json .

RUN npm ci && ng build

# Stage 2: Build the Spring Boot application
FROM eclipse-temurin:23-noble AS spring-builder

WORKDIR /src

COPY server/.mvn .mvn
COPY server/src src
COPY server/mvnw .
COPY server/pom.xml .

# Copy the Angular build output into the backend's static resources folder
COPY --from=angular-builder /src/dist/client/browser/ src/main/resources/static

RUN chmod a+x mvnw && ./mvnw package -Dmaven.test.skip=true

# Stage 3: Final runtime image
FROM eclipse-temurin:23-jre-noble

WORKDIR /app

COPY --from=spring-builder /src/target/server-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080

ENV SPRING_DATASOURCE_URL="" 
ENV SPRING_DATASOURCE_USERNAME="" 
ENV SPRING_DATASOURCE_PASSWORD="" 

ENV SPRING_DATA_MONGODB_URI=""

EXPOSE ${PORT}

SHELL [ "/bin/sh", "-c" ]
ENTRYPOINT SERVER_PORT=${PORT} java -jar app.jar