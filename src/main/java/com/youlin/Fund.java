package com.youlin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class Fund {
    private Long id;
    private String name;
    private String type;
    private double tempNav;
    private LocalDate tempDate;
    private List<Map<String, Object>> priceList;

    public Fund() {}

    @JsonProperty("price")
    public void setPriceList(List<Map<String, Object>> priceList) {
        this.priceList = priceList;
    }

    public List<Map<String, Object>> getPriceList() {
        return priceList;
    }

    // Getter/Setter
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public void setTempNav(double nav) { this.tempNav = nav; }
    public double getNav() { return this.tempNav; }

    public void setTempDate(LocalDate date) { this.tempDate = date; }
    public LocalDate getDate() { return this.tempDate; }
}