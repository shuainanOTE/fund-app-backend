package com.youlin;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class FundScheduler {


//    @Scheduled(fixedRate = 30000) // 測試用每30秒觸發一次
    @Scheduled(cron = "0 0 19 * * ?")
    public void runDailyUpdate() {
        System.out.println("⏰ 自動任務啟動：開始爬取最新基金淨值...");

        FundService service = new FundService();

        service.updateFund("安聯台灣科技基金", "ACDD04");
        service.updateFund("野村台灣運籌基金", "ackh03");

        System.out.println("✅ 今日資料更新完成！");
    }
}