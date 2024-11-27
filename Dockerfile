FROM gcr.io/distroless/java21
COPY eux-journalarkivar-webapp/target/eux-journalarkivar.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
