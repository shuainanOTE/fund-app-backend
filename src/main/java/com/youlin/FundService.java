package com.youlin;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class FundService {
    private static final Map<String, String> PAGE_CONFIG = new HashMap<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    static {
        PAGE_CONFIG.put("pima3", "yp010001");
    }

    public void updateAllFunds() {
        updateFund("安聯台灣科技基金", "ACDD04");
        updateFund("野村台灣運籌基金", "ackh03");
        updateFund("路博邁台灣5G股票基金T累積型", "acnb01");
        updateFund("國泰台灣高股息基金-A不配息(台幣)", "accy149");
        updateFund("路博邁台日雙星股票基金T累積型(台幣)", "ACNB180");
        updateFund("PIMCO收益增長基金-BM級類別", "pima3");
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

        } catch (Exception e) {
            System.err.println("更新失敗: " + fundName);
            e.printStackTrace();
        }
    }
}