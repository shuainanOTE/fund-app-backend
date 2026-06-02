package com.youlin;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FundService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static final Map<String, FundInfo> FUND_REGISTRY = new LinkedHashMap<>();
    private static class FundInfo {
        String fundCode;
        boolean isETF;
        boolean isSpecial;

        FundInfo(String fundCode, boolean isETF, boolean isSpecial) {
            this.fundCode = fundCode;
            this.isETF = isETF;
            this.isSpecial = isSpecial;
        }
    }
    // URL
    static {
        // Fund
        FUND_REGISTRY.put("安聯台灣科技基金", new FundInfo("ACDD04", false, false));
        FUND_REGISTRY.put("野村台灣運籌基金", new FundInfo("ackh03", false, false));
        FUND_REGISTRY.put("路博邁台灣5G股票基金T累積型", new FundInfo("acnb01", false, false));
        FUND_REGISTRY.put("國泰台灣高股息基金-A不配息(台幣)", new FundInfo("accy149", false, false));
        FUND_REGISTRY.put("路博邁台日雙星股票基金T累積型(台幣)", new FundInfo("ACNB180", false, false));
        // 特殊
        FUND_REGISTRY.put("PIMCO收益增長基金-BM級類別", new FundInfo("pima3", false, true));
        // ETF
        FUND_REGISTRY.put("主動統一台股增長(00981A)", new FundInfo("00981A.TW", true, false));
        FUND_REGISTRY.put("主動統一全球創新(00988A)", new FundInfo("00988A.TW", true, false));
        FUND_REGISTRY.put("主動統一升級50(00403A)", new FundInfo("00403A.TW", true, false));
    }
    // 排序
    public List<FundDTO> getProcessedFunds() {
        return Database.fetchAll().stream()
                .collect(Collectors.groupingBy(Fund::getName))
                .entrySet().stream()
                .map(entry -> calculateFundData(entry.getKey(), entry.getValue()))
                .peek(fundDto -> {
                    Long fundId = Database.fetchFundIdByName(fundDto.getName());
                    if (fundId != null) {
                        fundDto.setPerformance(Database.fetchPerformanceByFundId(fundId));
                    }
                })
                .collect(Collectors.toList());
    }
    // 計算
    private FundDTO calculateFundData(String name, List<Fund> history) {
        List<Map<String, Object>> prices = history.get(0).getPriceList();
        prices.sort((a, b) -> b.get("date").toString().compareTo(a.get("date").toString()));

        Map<String, Object> today = prices.get(0);
        double todayNav = Double.parseDouble(today.get("nav").toString());
        String todayDate = today.get("date").toString();

        double changePercent = 0.0;
        if (prices.size() >= 2) {
            double yNav = Double.parseDouble(prices.get(1).get("nav").toString());
            if (todayNav != yNav) {
                changePercent = BigDecimal.valueOf(todayNav).subtract(BigDecimal.valueOf(yNav))
                        .divide(BigDecimal.valueOf(yNav), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
        }

        FundDTO dto = new FundDTO(name, todayNav, changePercent, todayDate);
        dto.setHoldings(Database.fetchHoldingsByFundId(Database.fetchFundIdByName(name)));
        return dto;
    }
    // 提取 淨值、日期
    public void updateAllFundsNav() {
        FUND_REGISTRY.forEach((name, info) -> updateFund(name, info));
    }
    // 提取 十大持股
    public void updateAllFundsHoldings() {
        System.out.println("DEBUG: 觸發更新，目前實例 HashCode: " + this.hashCode());
        FUND_REGISTRY.forEach((name, info) -> {
            Long fundId = Database.fetchFundIdByName(name);
            if (fundId != null) {
                syncHoldingsTask(fundId, info);
            }
        });
    }
    // 更新 淨值、日期
    private void updateFund(String fundName, FundInfo info) {
        try {
            String[] rawData = Crawler.getFundNav(info.fundCode, info.isSpecial);

            Fund fund = new Fund();
            fund.setName(fundName);
            fund.setTempNav(Double.parseDouble(rawData[1]));
            fund.setTempDate(LocalDate.parse(rawData[0], DATE_FORMATTER));
            Database.saveFundData(fund);

//            System.out.println("✅ 成功同步淨值: " + fundName);
        } catch (Exception e) {
            System.err.println("❌ 淨值更新失敗: " + fundName);
            e.printStackTrace();
        }
    }
    // 更新 十大持股
    public void syncHoldingsTask(Long fundId, FundInfo info) {
        try {
            List<String[]> holdings;

            if (info.isETF) {
                holdings = Crawler.getETFHoldings(info.fundCode);
            } else {
                holdings = Crawler.getHoldings(info.fundCode, info.isSpecial);
            }

            if (holdings != null && !holdings.isEmpty()) {
                Database.deleteHoldings(fundId);
                Database.saveHoldings(fundId, holdings);
                System.out.println("✅ " + info.fundCode + " 同步完成，更新 " + holdings.size() + " 筆持股。");
            } else {
                System.err.println("⚠️ " + info.fundCode + " 未抓取到任何資料，略過更新。");
            }
        } catch (Exception e) {
            System.err.println("❌ " + info.fundCode + " 同步失敗: " + e.getMessage());
        }
    }
    // 更新 績效
    public void updateAllPerformance() {
        for (Map.Entry<String, FundInfo> entry : FUND_REGISTRY.entrySet()) {
            String fundName = entry.getKey();
            FundInfo info = entry.getValue();

            try {
                List<String[]> data;
                if (info.isETF) {
                    data = Crawler.getETFPerformance(info.fundCode);
                } else {
                    data = Crawler.getPerformance(info.fundCode, info.isSpecial);
                }

                Long fundId = Database.fetchFundIdByName(fundName);

                if (fundId != null && !data.isEmpty()) {
                    Database.savePerformanceData(fundId.intValue(), data);
                } else {
                    System.out.println("⚠️ 警告: 未取得 " + fundName + " 的 ID 或爬蟲資料為空。");
                }
            } catch (Exception e) {
                System.err.println("❌ 更新 " + fundName + " 過程發生錯誤: " + e.getMessage());
            }
        }
    }
}