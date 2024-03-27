FROM ghcr.io/navikt/baseimages/temurin:21

ADD eux-journalarkivar-webapp/target/eux-journalarkivar.jar /app/app.jar
