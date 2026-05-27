package com.youlin;

public class FundDisplayDTO {
    public String name;
    public double nav;
    public double changePercent;
    public String updateDate;

    public FundDisplayDTO(String name, double nav, double changePercent, String updateDate) {
        this.name = name;
        this.nav = nav;
        this.changePercent = changePercent;
        this.updateDate = updateDate;
    }
}