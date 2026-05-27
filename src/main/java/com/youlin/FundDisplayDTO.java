package com.youlin;

public class FundDisplayDTO {
    public String name;
    public double nav;           // 改名 nav
    public double changePercent;
    public String updateDate;    // 改名 updateDate

    public FundDisplayDTO(String name, double nav, double changePercent, String updateDate) {
        this.name = name;
        this.nav = nav;
        this.changePercent = changePercent;
        this.updateDate = updateDate;
    }
}