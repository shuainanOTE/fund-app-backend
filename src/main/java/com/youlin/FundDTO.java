package com.youlin;

import java.util.List;

public class FundDTO {
    public String name;
    public double nav;
    public double changePercent;
    public String date;
    private List<HoldingDTO> holdings;
    private List<PerformanceDTO> performance;
    // 淨值、日期
    public FundDTO(String name, double nav, double changePercent, String date) {
        this.name = name;
        this.nav = nav;
        this.changePercent = changePercent;
        this.date = date;
    }
    // 十大持股
    public static class HoldingDTO {
        private String stock;
        private double percent;

        public HoldingDTO(String stock, double percent) {
            this.stock = stock;
            this.percent = percent;
        }

        public String getStock() { return stock; }
        public double getPercent() { return percent; }
    }
    // 績效
    public static class PerformanceDTO {
        private String period;
        private double rate;

        public PerformanceDTO(String period, double rate) {
            this.period = period;
            this.rate = rate;
        }

        public String getPeriod() { return period; }
        public double getRate() { return rate; }
    }

    // --- Getters & Setters ---
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getNav() { return nav; }
    public void setNav(double nav) { this.nav = nav; }

    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<HoldingDTO> getHoldings() { return holdings; }
    public void setHoldings(List<HoldingDTO> holdings) { this.holdings = holdings; }

    public List<PerformanceDTO> getPerformance() { return performance; }
    public void setPerformance(List<PerformanceDTO> performance) { this.performance = performance; }
}