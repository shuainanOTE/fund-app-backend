package com.youlin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Autowired
    private FundService fundService;

    @GetMapping("/api/funds")
    public List<FundDisplayDTO> getFunds() {
        List<Fund> allFunds = Database.fetchAll();
        List<FundDisplayDTO> result = new ArrayList<>();
        Map<String, List<Fund>> grouped = allFunds.stream()
                .collect(Collectors.groupingBy(Fund::getName));

        for (String name : grouped.keySet()) {
            List<Fund> history = grouped.get(name);
            history.sort((a, b) -> b.getUpdateDate().compareTo(a.getUpdateDate()));
            Fund today = history.get(0);
            double changePercent = 0.0;

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
            result.add(new FundDisplayDTO(name, today.getNav(), changePercent, today.getUpdateDate().toString()));
        }
        System.out.println("重新整理頁面成功！");
        return result;
    }

    @GetMapping("/api/update")
    public String triggerUpdate() {
        fundService.updateAllFunds();
        return "✅ 資料已於 " + LocalDateTime.now() + " 更新完成！";
    }

    @GetMapping("/ping")
    public String ping() {
        return "ping";
    }

    @Scheduled(cron = "0 0 18 * * ?")
    public void scheduledScraper() {
        System.out.println("--- 開始執行每日爬蟲任務 ---");
        fundService.updateAllFunds();
        System.out.println("--- 更新任務完成 ---");
    }
}