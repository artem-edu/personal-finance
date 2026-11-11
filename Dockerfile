FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

COPY pom.xml .
RUN mvn -B dependency:go-offline

COPY src ./src
RUN mvn -B clean package

RUN mvn -B dependency:copy-dependencies -DoutputDirectory=target/lib -DincludeScope=runtime

FROM eclipse-temurin:17-jre

COPY --from=build /build/target/PersonalFinance-1.0-SNAPSHOT.jar /opt/app/app.jar
COPY --from=build /build/target/lib /opt/app/lib/
COPY db /opt/app/db
COPY docker-entrypoint.sh /usr/local/bin/

# рабочая директория: приложение хранит здесь db/ и выгружает отчёты
WORKDIR /data

ENTRYPOINT ["docker-entrypoint.sh"]
