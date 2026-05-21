package com.youlin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Fund {
    private String name;
    private double nav;

    @JsonProperty("update_date") // 確保與資料庫欄位名稱對應
    private LocalDate updateDate;

    // 1. 必須要有一個空建構子，讓 JSON 轉換器能運作
    public Fund() {}

    // 2. 這是你原本的建構子
    public Fund(String name, double nav, LocalDate updateDate) {
        this.name = name;
        this.nav = nav;
        this.updateDate = updateDate;
    }

    // 3. Setter 方法也是必要的，否則轉換器沒辦法把值填進去
    public void setName(String name) { this.name = name; }
    public void setNav(double nav) { this.nav = nav; }
    public void setUpdateDate(LocalDate updateDate) { this.updateDate = updateDate; }

    // Getter
    public String getName() { return name; }
    public double getNav() { return nav; }
    public LocalDate getUpdateDate() { return updateDate; }
}