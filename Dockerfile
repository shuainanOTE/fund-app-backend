# --- 第一階段：編譯階段 ---
# 使用官方 Maven + Java 21 映像檔來編譯你的程式碼
FROM maven:3.9-eclipse-temurin-21 AS build

# 設定工作目錄
WORKDIR /app

# 將本地的檔案複製到容器中
COPY . .

# 執行 Maven 清理並打包，跳過測試以節省時間
RUN mvn clean package -DskipTests

# --- 第二階段：執行階段 ---
# 使用輕量級的 JRE 21 來執行，這樣 Docker 映像檔會比較小，跑起來比較快
FROM eclipse-temurin:21-jre-jammy

# 設定工作目錄
WORKDIR /app

# 從第一階段編譯好的結果中，把產出的 jar 檔複製過來
# 假設你的 jar 檔是在 target/ 資料夾下
COPY --from=build /app/target/*.jar app.jar

# 指定 Docker 對外開放的 Port (預設為 8080)
EXPOSE 8080

# 啟動應用程式
ENTRYPOINT ["java", "-jar", "app.jar"]