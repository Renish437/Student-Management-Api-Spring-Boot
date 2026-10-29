
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]

#  mvn clean package -DskipTests
# docker build -t student-management-app .
# docker run --env-file .env -d -p 8080:8080 --name student-management-container student-management-app