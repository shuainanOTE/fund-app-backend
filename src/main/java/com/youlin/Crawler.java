package com.youlin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Crawler {
    public static String[] getFund(String pageCode, String fundCode) throws IOException {
        // 1. 判斷是否為 ETF (如果 fundCode 包含 .TW 就走 ETF 邏輯)
        boolean isETF = fundCode.contains(".TW");
        String url = isETF
                ? "https://www.moneydj.com/ETF/X/Basic/Basic0003.xdjhtm?etfid=" + fundCode
                : "https://www.moneydj.com/funddj/ya/" + pageCode + ".djhtm?a=" + fundCode;

        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();

        // 2. 根據是否為 ETF 選擇不同的定位方式
        Elements tds;
        if (isETF) {
            // ETF 的表格結構：找到含有「淨值」文字的那一行 tr，再取裡面的 td
            // 這樣寫最穩，不用管它是第幾個 table 或 tr
            tds = doc.select("tr:contains(淨值)").select("td");
        } else {
            // 原本的基金邏輯
            tds = doc.select(".t01").first().select("tr").get(1).select("td");
        }

        String date;
        String nav;

        if (isETF) {
            // ETF 需要清理文字
            date = tds.get(0).text().replaceAll(".*\\((.*)\\).*", "$1");
            nav = tds.get(1).text().split("\\(")[0].replace(",", "");
        } else {
            // 原本的基金邏輯
            date = tds.get(0).text();
            nav = tds.get(1).text();
        }

        return new String[] { date, nav };
    }
}