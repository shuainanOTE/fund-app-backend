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
        String sql = "SELECT name, nav, update_date FROM funds ORDER BY update_date DESC";

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                funds.add(new Fund(
                        rs.getString("name"),
                        rs.getDouble("nav"),
                        rs.getObject("update_date", java.time.LocalDate.class)
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return funds;
    }

}
