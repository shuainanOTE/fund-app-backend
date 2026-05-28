package com.youlin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDateTime;

@RestController
public class FundCTRL {

    @Autowired
    private FundService fundService;

    @GetMapping("/ping")
    public String ping() {
        return "ping";
    }

    @GetMapping("/api/funds")
    public List<FundDTO> getFunds() {
        System.out.println("重新整理頁面成功！");
        return fundService.getProcessedFunds();
    }

    @GetMapping("/api/update")
    public String triggerUpdate() {
        fundService.updateAllFunds();
        return "✅ 資料已於 " + LocalDateTime.now() + " 更新完成！";
    }

    @Scheduled(cron = "0 0 18 * * ?", zone = "Asia/Taipei")
    public void scheduledScraper() {
        System.out.println("--- 開始執行每日爬蟲任務 ---");
        fundService.updateAllFunds();
        System.out.println("--- 更新任務完成 ---");
    }
}