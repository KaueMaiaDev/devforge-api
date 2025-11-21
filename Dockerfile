# --- ETAPA 1: BUILD (Construção) ---
# Usamos uma imagem que já tem o MAVEN instalado
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

# Cria a pasta de trabalho
WORKDIR /app

# Copia todo o seu projeto para dentro do servidor
COPY . .

# Manda o Maven compilar o projeto (gera o arquivo .jar)
# O -DskipTests agiliza o processo pulando testes unitários no deploy
# AQUI ESTÁ A CORREÇÃO: Adicionamos -Dfile.encoding=UTF-8 para evitar o erro MalformedInputException
RUN mvn clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8 -Dfile.encoding=UTF-8

# --- ETAPA 2: RUN (Execução) ---
# Agora usamos uma imagem leve apenas com o JAVA para rodar
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copia o .jar gerado na etapa anterior (build) para cá
COPY --from=build /app/target/*.jar app.jar

# Liga o servidor
ENTRYPOINT ["java", "-jar", "app.jar"]