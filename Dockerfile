# Použijeme základní obraz pro Java aplikace
FROM openjdk:17-jdk-alpine

# Nastavíme pracovní adresář v kontejneru
WORKDIR /app

# Zkopírujeme JAR soubor aplikace do pracovního adresáře
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# Expose port 8080, na kterém běží Spring Boot aplikace
EXPOSE 8080

# Příkaz pro spuštění aplikace
ENTRYPOINT ["java", "-jar", "app.jar"]
