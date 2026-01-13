FROM gcr.io/distroless/java25
COPY eux-journalarkivar-webapp/target/eux-journalarkivar.jar /app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
