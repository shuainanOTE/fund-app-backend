package com.youlin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Fund {
    private Long id;
    private String name;

    // 命名改為 nav，但在 JSON 輸出時維持 "nav" (或你想保持的名稱)
    private double nav;

    // 使用 LocalDate，這對後續日期運算非常方便
    @JsonProperty("update_date")
    private LocalDate updateDate;

    public Fund() {}

    public Fund(String name, double nav, LocalDate updateDate) {
        this.name = name;
        this.nav = nav;
        this.updateDate = updateDate;
    }

    // --- Standard Getters and Setters ---

    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getNav() { return nav; }
    public void setNav(double nav) { this.nav = nav; }

    public LocalDate getUpdateDate() { return updateDate; }
    public void setUpdateDate(LocalDate updateDate) { this.updateDate = updateDate; }

    public String getFormattedDate() {
        return updateDate != null ? updateDate.toString() : "";
    }
}