package com.youlin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FundController {

    @GetMapping("/api/funds")
    public List<FundDisplayDTO> getFunds() {
        List<Fund> allFunds = Database.fetchAll();
        List<FundDisplayDTO> result = new ArrayList<>();

        // 1. 將資料按基金名稱分組
        Map<String, List<Fund>> grouped = allFunds.stream()
                .collect(Collectors.groupingBy(Fund::getName));

        // 2. 對每個基金，取出最新兩筆並計算
        for (String name : grouped.keySet()) {
            List<Fund> history = grouped.get(name);

            // 依日期排序（最新的在最前面）
            history.sort((a, b) -> b.getUpdateDate().compareTo(a.getUpdateDate()));

            if (history.size() >= 2) {
                Fund today = history.get(0);
                Fund yesterday = history.get(1);

                // 1. 轉為 BigDecimal 進行運算
                BigDecimal todayNav = BigDecimal.valueOf(today.getNav_today());
                BigDecimal yesterdayNav = BigDecimal.valueOf(yesterday.getNav_today());

                // 2. 公式: ((今天 - 昨天) / 昨天) * 100
                // 注意：BigDecimal 的除法需要指定精度與捨入模式
                BigDecimal diff = todayNav.subtract(yesterdayNav);
                BigDecimal change = diff.divide(yesterdayNav, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                // 3. 取到小數點後兩位 (四捨五入)
                double finalChange = change.setScale(2, RoundingMode.HALF_UP).doubleValue();

                result.add(new FundDisplayDTO(name, today.getNav_today(), finalChange, today.getDate()));
            } else if (history.size() == 1) {
                // 如果只有一筆資料，漲跌幅設為 0
                result.add(new FundDisplayDTO(name, history.get(0).getNav_today(), 0.0, history.get(0).getDate()));
            }
        }
        return result;
    }

    @GetMapping("/api/update")
    public String triggerUpdate() {
        FundService service = new FundService();
        service.updateFund("安聯台灣科技基金", "ACDD04");
        service.updateFund("野村台灣運籌基金", "ackh03");
        service.updateFund("路博邁台灣5G股票基金T累積型", "acnb01");
        service.updateFund("國泰台灣高股息基金-A不配息(台幣)", "accy149");
        service.updateFund("路博邁台日雙星股票基金T累積型(台幣)", "ACNB180");
        service.updateFund("PIMCO收益增長基金-BM級類別", "pima3");
        return "✅ 資料已於 " + LocalDateTime.now() + " 更新完成！";
    }
}

class FundDisplayDTO {
    public String name;          // 前端直接讀 stock.name
    public double nav_today;           // 前端直接讀 stock.price
    public double changePercent; // 前端直接讀 stock.profitPercent
    public String update_date;   // 前端直接讀 stock.update_date

    public FundDisplayDTO(String name, double nav_today, double changePercent, String update_date) {
        this.name = name;
        this.nav_today = nav_today;
        this.changePercent = changePercent;
        this.update_date = update_date;
    }
}