FROM ghcr.io/navikt/baseimages/temurin:21

ADD eux-journal-webapp/target/eux-journalarkivar.jar /app/app.jar
