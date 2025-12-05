#Docker file multi-stage para Hostings como Railway o Render que no permiten subir el .jar desde local por #lo que toca compilar el código y sacarlo desde dockerfile


# ===== ETAPA 1: COMPILACIÓN =====
# Railway ejecuta esta parte para GENERAR el JAR por medio de dockerfile
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copia el código fuente desde GitHub
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

# AQUÍ Railway compila y genera el JAR
RUN ./mvnw clean package -DskipTests

# ===== ETAPA 2: EJECUCIÓN =====
# Railway crea un contenedor limpio solo para ejecutar
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copia el JAR que se generó en la ETAPA 1
COPY --from=build /app/target/*.jar apibazar.jar

# Railway ejecuta esto
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "apibazar.jar"]
