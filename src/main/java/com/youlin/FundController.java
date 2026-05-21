package com.youlin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.time.LocalDateTime;

@RestController
public class FundController {

    // 直接呼叫 Database 類別的方法
    @GetMapping("/api/funds")
    public List<Fund> getFunds() {
        return Database.fetchAll();
    }

    @GetMapping("/api/update")
    public String triggerUpdate() {
        FundService service = new FundService();
        service.updateFund("安聯台灣科技基金", "ACDD04");
        service.updateFund("野村台灣運籌基金", "ackh03");
        return "✅ 資料已於 " + LocalDateTime.now() + " 更新完成！";
    }


}