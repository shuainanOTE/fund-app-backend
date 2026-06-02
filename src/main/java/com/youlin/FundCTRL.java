package com.youlin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api")
public class FundCTRL {

    @Autowired
    private FundService fundService;
    // Nothing
    @GetMapping("/ping")
    public String ping() {
        return "ping";
    }
    // 查詢 全部資料
    @GetMapping("/fetch/all")
    public List<FundDTO> getFunds() {
        System.out.println("重新整理頁面成功！");
        return fundService.getProcessedFunds();
    }
    // 更新 淨值、日期
    @GetMapping("/update/fund")
    public String triggerUpdate() {
        fundService.updateAllFundsNav();
        return "✅ 資料已於 " + LocalDateTime.now() + " 更新完成！";
    }
    // 更新 十大持股
    @GetMapping("/update/holding")
    public String holdingUpdate() {
        fundService.updateAllFundsHoldings();
        return "✅ 資料已於 " + LocalDateTime.now() + " 更新完成！";
    }
    // 查詢 特定十大持股
    @GetMapping("/fetch/holding/{fundId}")
    public List<FundDTO.HoldingDTO> getHoldings(@PathVariable Long fundId) {
        return Database.fetchHoldingsByFundId(fundId);
    }
    // 更新 績效
    @GetMapping("/update/performance")
    public String performanceUpdate() {
        fundService.updateAllPerformance();
        return "✅ 績效資料已於 " + LocalDateTime.now() + " 更新完成！";
    }
    // 查詢 績效
    @GetMapping("/fetch/performance/{fundId}")
    public List<FundDTO.PerformanceDTO> getPerformance(@PathVariable Long fundId) {
        return Database.fetchPerformanceByFundId(fundId);
    }
}