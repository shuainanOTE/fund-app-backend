# 使用 JDK 21
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# 複製所有檔案到容器內
COPY . .

# 直接使用 Maven 指令進行打包 (跳過測試以節省時間)
RUN mvn clean package -DskipTests

# 啟動應用程式
CMD ["java", "-jar", "target/fund-app-backend-1.0-SNAPSHOT.jar"]