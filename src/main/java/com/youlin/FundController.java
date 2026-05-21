package com.youlin;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
public class FundController {

    @GetMapping("/api/funds")
    public List<Fund> getFunds() {
        try {
            return Database.fetchAll();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}