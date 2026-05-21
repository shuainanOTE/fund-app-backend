package com.youlin;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class FundController {

    @GetMapping("/api/funds")
    public List<Fund> getFunds() {
        System.out.println("Debug: DB_URL = " + System.getenv("DB_URL"));

        try {
            List<Fund> funds = Database.fetchAll();
            System.out.println("Debug: 撈取到的資料筆數 = " + (funds != null ? funds.size() : "null"));
            return funds;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}