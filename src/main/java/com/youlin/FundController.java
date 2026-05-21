package com.youlin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class FundController {

    // 直接呼叫 Database 類別的方法
    @GetMapping("/api/funds")
    public List<Fund> getFunds() {
        return Database.fetchAll();
    }
}