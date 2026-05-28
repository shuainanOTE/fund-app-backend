package com.youlin;

public class FundDTO {
    public String name;
    public double nav;
    public double changePercent;
    public String date;

    public FundDTO(String name, double nav, double changePercent, String date) {
        this.name = name;
        this.nav = nav;
        this.changePercent = changePercent;
        this.date = date;
    }
}