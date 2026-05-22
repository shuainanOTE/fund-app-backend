package com.youlin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

public class Fund {
    private Long id;
    private String name;

    @JsonProperty("nav_today") // 這裡！強制 JSON 輸出變成 "nav_today"
    private double nav_today;  // 變數名稱統一改為 nav_today

    @JsonProperty("update_date")
    private LocalDate updateDate;

    public Fund() {}

    public Fund(String name, double nav_today, LocalDate updateDate) {
        this.name = name;
        this.nav_today = nav_today;
        this.updateDate = updateDate;
    }

    // Setter
    public void setName(String name) { this.name = name; }
    public void setNav_today(double nav_today) { this.nav_today = nav_today; }
    public void setUpdateDate(LocalDate updateDate) { this.updateDate = updateDate; }

    // Getter
    public String getName() { return name; }
    public double getNav_today() { return nav_today; } // 方法名也統一
    public LocalDate getUpdateDate() { return updateDate; }

    public String getDate() {
        return updateDate != null ? updateDate.toString() : "";
    }

    public Long getId() { return id; }
}