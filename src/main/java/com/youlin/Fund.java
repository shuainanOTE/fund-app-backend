package com.youlin;

import java.time.LocalDate;

public class Fund {
    private String name;
    private double nav;
    private LocalDate updateDate;

    public Fund(String name, double nav, LocalDate updateDate) {
        this.name = name;
        this.nav = nav;
        this.updateDate = updateDate;
    }

    // Getter
    public String getName() { return name; }
    public double getNav() { return nav; }
    public LocalDate getUpdateDate() { return updateDate; }
}