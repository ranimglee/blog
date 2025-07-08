# Utiliser l'image de base OpenJDK 17
FROM openjdk:17-jdk-slim

# Exposer le port 8080 pour l'application
EXPOSE 8080

# Définir la variable d'environnement pour le répertoire d'application
ENV APP_HOME=/app

# Copier le fichier JAR généré dans le conteneur
COPY target/blog-0.0.1-SNAPSHOT.jar app.jar

# Définir le répertoire de travail dans le conteneur
WORKDIR $APP_HOME

# Définir le point d'entrée pour exécuter l'application avec le JAR
ENTRYPOINT ["java", "-jar", "tpFoyer-17-0.0.1.jar"]
