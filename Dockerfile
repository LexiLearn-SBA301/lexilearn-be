# ===== Stage 1: Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy pom trước để tận dụng cache layer khi chỉ đổi source
COPY pom.xml .
COPY src ./src

# Build jar, bỏ test cho nhanh. Cache mount .m2 để build lần sau nhanh hơn.
RUN mvn clean package -DskipTests -B

# ===== Stage 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Copy jar đã build từ stage trên
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
