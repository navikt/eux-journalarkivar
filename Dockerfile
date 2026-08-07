FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
ENV TZ="Europe/Oslo"
ENV JDK_JAVA_OPTIONS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
COPY eux-journalarkivar-webapp/target/eux-journalarkivar.jar /app.jar
CMD ["-jar", "/app.jar"]
