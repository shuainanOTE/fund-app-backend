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

        // 按名稱分組
        Map<String, List<Fund>> grouped = allFunds.stream()
                .collect(Collectors.groupingBy(Fund::getName));

        for (String name : grouped.keySet()) {
            List<Fund> history = grouped.get(name);
            // 確保日期由新到舊
            history.sort((a, b) -> b.getUpdateDate().compareTo(a.getUpdateDate()));

            Fund today = history.get(0); // 最新的一筆
            double changePercent = 0.0;

            // 如果有兩筆以上，且第二筆數值不同，才計算漲跌
            if (history.size() >= 2) {
                Fund yesterday = history.get(1);
                if (today.getNav() != yesterday.getNav()) {
                    BigDecimal todayNav = BigDecimal.valueOf(today.getNav());
                    BigDecimal yesterdayNav = BigDecimal.valueOf(yesterday.getNav());

                    changePercent = todayNav.subtract(yesterdayNav)
                            .divide(yesterdayNav, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .setScale(2, RoundingMode.HALF_UP)
                            .doubleValue();
                }
            }

            // 使用正規化後的名稱
            result.add(new FundDisplayDTO(name, today.getNav(), changePercent, today.getUpdateDate().toString()));
        }
        System.out.println("重新整理頁面成功！");
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
    public String name;
    public double nav;           // 改名 nav
    public double changePercent;
    public String updateDate;    // 改名 updateDate

    public FundDisplayDTO(String name, double nav, double changePercent, String updateDate) {
        this.name = name;
        this.nav = nav;
        this.changePercent = changePercent;
        this.updateDate = updateDate;
    }
}