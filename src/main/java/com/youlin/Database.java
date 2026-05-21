package com.youlin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.sql.ResultSet;

public class Database {
    private static final String URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASSWORD = System.getenv("DB_PASSWORD");

    public static void saveFundData(Fund fund) {
        String sql = "INSERT INTO funds (name, nav, update_date) VALUES (?, ?, ?) " +
                "ON CONFLICT (name) DO UPDATE SET " +
                "nav = EXCLUDED.nav, " +
                "update_date = EXCLUDED.update_date";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            System.out.println("成功連線至資料庫: " + conn.getMetaData().getURL());
            System.out.println("當前連線的資料庫名稱: " + conn.getCatalog());

            pstmt.setString(1, fund.getName());
            pstmt.setDouble(2, fund.getNav());
            pstmt.setObject(3, fund.getUpdateDate()); // 這裡現在就正確了！

            pstmt.executeUpdate();
            System.out.println("成功寫入資料庫: " + fund.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Fund> fetchAll() {
        List<Fund> funds = new ArrayList<>();
        // 強制在連線 URL 加入 SSL 要求 (如果環境變數沒寫，這裡會幫你補上)
        String connectionUrl = URL;
        if (!connectionUrl.contains("ssl=true")) {
            connectionUrl += (connectionUrl.contains("?") ? "&" : "?") + "ssl=true&sslmode=require";
        }

        System.out.println("DEBUG: 嘗試連線至: " + connectionUrl);

        try {
            // 1. 明確載入驅動
            Class.forName("org.postgresql.Driver");

            // 2. 建立連線 (將參數改為 connectionUrl)
            try (Connection conn = DriverManager.getConnection(connectionUrl, USER, PASSWORD);
                 PreparedStatement pstmt = conn.prepareStatement("SELECT name, nav, update_date FROM funds ORDER BY update_date DESC");
                 ResultSet rs = pstmt.executeQuery()) {

                System.out.println("DEBUG: 資料庫連線成功！開始撈取資料...");

                while (rs.next()) {
                    funds.add(new Fund(
                            rs.getString("name"),
                            rs.getDouble("nav"),
                            rs.getObject("update_date", java.time.LocalDate.class)
                    ));
                }
                System.out.println("DEBUG: 成功撈取 " + funds.size() + " 筆資料。");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("錯誤：找不到 PostgreSQL JDBC 驅動程式。");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("錯誤：連線發生異常。");
            e.printStackTrace(); // 這會告訴我們具體是 SSL 還是驗證失敗
        }
        return funds;
    }

}
