# 使用 JDK 21
FROM eclipse-temurin:21-jdk-jammy

WORKDIR /app

# 複製所有檔案到容器內
COPY . .

# 修改權限確保 mvnw 可以執行
RUN chmod +x ./mvnw

# 改用 ./mvnw 來執行打包指令 (它會自動下載對應版本的 Maven)
RUN ./mvnw clean package -DskipTests

# 啟動應用程式
CMD ["java", "-jar", "target/fund-app-backend-1.0-SNAPSHOT.jar"]