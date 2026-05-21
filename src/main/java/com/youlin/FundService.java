package com.youlin;

import org.springframework.stereotype.Service;
import java.time.LocalDate;

@Service
public class FundService {

    public void updateFund(String fundName, String fundCode) {
        try {
            String navStr = Crawler.getFundNav(fundCode);
            double nav = Double.parseDouble(navStr);

            Fund fund = new Fund(fundName, nav, LocalDate.now());
            Database.saveFundData(fund);

            System.out.println("成功更新: " + fundName);
        } catch (Exception e) {
            System.out.println("更新失敗: " + fundName);
            e.printStackTrace();
        }
    }
}