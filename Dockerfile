# 1. 使用 Java 21 的映像檔作為基礎
FROM eclipse-temurin:21-jdk-jammy

# 2. 設定工作目錄
WORKDIR /app

# 3. 先複製 pom.xml 和 mvnw 等檔案，優化 Docker 的層級快取
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# 4. 下載相依套件 (這樣做可以加快後續的部署速度)
RUN ./mvnw dependency:go-offline

# 5. 複製你的所有程式碼
COPY src ./src

# 6. 進行打包 (產出 jar 檔)
RUN ./mvnw clean package -DskipTests

# 7. 啟動應用程式
# 這裡對應你 pom.xml 的名稱和版本
CMD ["java", "-jar", "target/fund-app-backend-1.0-SNAPSHOT.jar"]