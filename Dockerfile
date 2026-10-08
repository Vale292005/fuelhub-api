# Etapa 1: Compilación con Maven
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copiamos los archivos de Maven y las dependencias
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# Damos permisos de ejecución al mvnw y compilamos el proyecto empaquetando el .jar
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# Etapa 2: Imagen ligera para ejecución (Solo JRE)
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copiamos el .jar generado desde la etapa de compilación
COPY --from=build /app/target/*.jar app.jar

# Exponemos el puerto en el que corre tu Spring Boot
EXPOSE 8080

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]