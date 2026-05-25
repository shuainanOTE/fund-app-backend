package com.youlin;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class FundService {
    private static final Map<String, String> PAGE_CONFIG = new HashMap<>();
    // 定義日期格式
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    static {
        PAGE_CONFIG.put("pima3", "yp010001");
    }

    public void updateFund(String fundName, String fundCode) {
        try {
            String pageCode = PAGE_CONFIG.getOrDefault(fundCode, "yp010000");

            // 1. 取得包含 [日期, NAV] 的陣列
            String[] rawData = Crawler.getFund(pageCode, fundCode);

            // 2. 解析資料
            LocalDate updateDate = LocalDate.parse(rawData[0], DATE_FORMATTER);
            double nav = Double.parseDouble(rawData[1]);

            // 3. 建立正規化後的 Fund 物件 (不需要 nav_today/yesterday 了)
            Fund fund = new Fund(fundName, nav, updateDate);

            // 4. 存入資料庫 (這裡記得把 Database.saveFundData 裡的 SQL 改成 INSERT...ON CONFLICT)
            Database.saveFundData(fund);

//            System.out.println("成功更新: " + fundName + " 日期: " + updateDate);
        } catch (Exception e) {
            System.err.println("更新失敗: " + fundName);
            e.printStackTrace();
        }
    }
}