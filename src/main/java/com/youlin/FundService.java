package com.youlin;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class FundService {
    // 建立對應表：key 是 fundCode，value 是 pageCode
    private static final Map<String, String> PAGE_CONFIG = new HashMap<>();

    static {
        PAGE_CONFIG.put("pima3", "yp010001");
        // 如果未來有其他的特殊基金，只要在這邊加一行即可：
        // PAGE_CONFIG.put("otherCode", "yp010002");
    }

    public void updateFund(String fundName, String fundCode) {
        try {
            // 決定要用哪一個頁面代碼，預設為 yp010000
            String pageCode = PAGE_CONFIG.getOrDefault(fundCode, "yp010000");

            // 呼叫 Crawler
            String navStr = Crawler.getFundNav(pageCode, fundCode);
            double nav = Double.parseDouble(navStr);

            Fund fund = new Fund(fundName, nav, LocalDate.now());
            Database.saveFundData(fund);

            System.out.println("成功更新: " + fundName);
        } catch (Exception e) {
            System.out.println("更新失敗: " + fundName);
            e.printStackTrace();
        }
    }
}