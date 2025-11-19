# 1. Usa uma imagem base do Java 21
FROM eclipse-temurin:21-jdk-alpine

# 2. Cria uma pasta de trabalho
WORKDIR /app

# 3. Copia o arquivo que o Maven gera para dentro do servidor
COPY target/*.jar app.jar

# 4. O comando que liga o servidor (igual ao botão Play do IntelliJ)
ENTRYPOINT ["java", "-jar", "app.jar"]