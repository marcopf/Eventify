FROM eclipse-temurin:17-jdk-alpine
RUN addgroup --gid 1001 eventify; adduser  --ingroup eventify --disabled-password eventify
USER eventify
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
