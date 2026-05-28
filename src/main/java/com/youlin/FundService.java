package com.youlin;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FundService {
    private static final Map<String, String> PAGE_CONFIG = new HashMap<>();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    static {
        PAGE_CONFIG.put("pima3", "yp010001");
    }

    public List<FundDTO> getProcessedFunds() {
        List<Fund> allFunds = Database.fetchAll(); // 保持你的資料存取

        return allFunds.stream()
                .collect(Collectors.groupingBy(Fund::getName))
                .entrySet().stream()
                .map(entry -> calculateFundData(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
    }

    private FundDTO calculateFundData(String name, List<Fund> history) {
        // 1. 取得該基金的 priceList (這是正確的資料源)
        List<Map<String, Object>> prices = history.get(0).getPriceList();

        // 2. 依照日期排序 (Postman 測出的資料日期為字串，需比較字串)
        prices.sort((a, b) -> b.get("date").toString().compareTo(a.get("date").toString()));

        // 3. 取出最新與前一筆資料
        Map<String, Object> today = prices.get(0);
        double todayNav = Double.parseDouble(today.get("nav").toString());
        String todayDate = today.get("date").toString();

        double changePercent = 0.0;
        if (prices.size() >= 2) {
            Map<String, Object> yesterday = prices.get(1);
            double yesterdayNav = Double.parseDouble(yesterday.get("nav").toString());

            if (todayNav != yesterdayNav) {
                BigDecimal tNav = BigDecimal.valueOf(todayNav);
                BigDecimal yNav = BigDecimal.valueOf(yesterdayNav);
                changePercent = tNav.subtract(yNav)
                        .divide(yNav, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100))
                        .setScale(2, RoundingMode.HALF_UP)
                        .doubleValue();
            }
        }

        // 4. 回傳 DTO
        return new FundDTO(name, todayNav, changePercent, todayDate);
    }

    public void updateAllFunds() {
        updateFund("安聯台灣科技基金", "ACDD04");
        updateFund("野村台灣運籌基金", "ackh03");
        updateFund("路博邁台灣5G股票基金T累積型", "acnb01");
        updateFund("國泰台灣高股息基金-A不配息(台幣)", "accy149");
        updateFund("路博邁台日雙星股票基金T累積型(台幣)", "ACNB180");
        updateFund("PIMCO收益增長基金-BM級類別", "pima3");
        updateFund("主動統一台股增長(00981A)", "00981A.TW");
        updateFund("主動統一全球創新(00988A)", "00988A.TW");
        updateFund("主動統一升級50(00403A)", "00403A.TW");

    }

    public void updateFund(String fundName, String fundCode) {
        try {
            String pageCode = PAGE_CONFIG.getOrDefault(fundCode, "yp010000");

            // 1. 取得資料
            String[] rawData = Crawler.getFund(pageCode, fundCode);
            LocalDate date = LocalDate.parse(rawData[0], DATE_FORMATTER);
            double nav = Double.parseDouble(rawData[1]);

            // 2. 建立 Fund 物件，並使用我們剛剛補上的「橋樑」Setter
            Fund fund = new Fund();
            fund.setName(fundName);
            fund.setTempNav(nav);    // 使用新補的橋樑
            fund.setTempDate(date);  // 使用新補的橋樑

            // 3. 呼叫儲存
            Database.saveFundData(fund);

        } catch (Exception e) {
            System.err.println("❌ 更新失敗: " + fundName);
            e.printStackTrace();
        }
    }
}