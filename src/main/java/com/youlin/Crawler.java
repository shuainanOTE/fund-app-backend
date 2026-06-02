package com.youlin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Crawler {
    // 判斷 Fund
    public static String[] getFundNav(String fundCode, boolean isSpecial) throws IOException {
        String pageCode = isSpecial ? "yp010001" : "yp010000";
        return getFund(pageCode, fundCode);
    }
    // 判斷 ETF
    public static List<String[]> getHoldings(String fundCode, boolean isSpecial) throws IOException {
        if (isSpecial) {
            return getPima3Holdings(fundCode);
        }
        String holdingCode = "yp013000";
        return getHoldings(holdingCode, fundCode);
    }
    // Fund and ETF(淨值、日期)
    public static String[] getFund(String pageCode, String fundCode) throws IOException {
        boolean isETF = fundCode.contains(".TW");
        String url = isETF
                ? "https://www.moneydj.com/ETF/X/Basic/Basic0003.xdjhtm?etfid=" + fundCode
                : "https://www.moneydj.com/funddj/ya/" + pageCode + ".djhtm?a=" + fundCode;
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        Elements tds;

        if (isETF) {
            tds = doc.select("tr:contains(淨值)").select("td");
        } else {
            tds = doc.select(".t01").first().select("tr").get(1).select("td");
        }

        String date;
        String nav;

        if (isETF) {
            date = tds.get(0).text().replaceAll(".*\\((.*)\\).*", "$1");
            nav = tds.get(1).text().split("\\(")[0].replace(",", "");
        } else {
            date = tds.get(0).text();
            nav = tds.get(1).text();
        }

        return new String[] { date, nav };
    }
    // Fund(十大持股)
    public static List<String[]> getHoldings(String holdingCode, String fundCode) throws IOException {
        if ("pima3".equals(fundCode)) {
            return getPima3Holdings(fundCode);
        }
        String url = "https://www.moneydj.com/funddj/ya/" + holdingCode + ".djhtm?a=" + fundCode;
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();
        List<String[]> holdings = new ArrayList<>();
        Elements rows = doc.select("table.t01 tr");

        for (int i = 1; i < rows.size(); i++) {
            Elements tds = rows.get(i).select("td");
            if (tds.size() >= 8) {
                int[][] indices = {{0, 2}, {4, 6}};
                for (int[] pair : indices) {
                    String name = tds.get(pair[0]).text().trim();
                    String valStr = tds.get(pair[1]).text().replaceAll("[^0-9.]", "");
                    if (!name.isEmpty() && !valStr.isEmpty()) {
                        try {
                            double percent = Double.parseDouble(valStr);
                            if (percent > 0.1 && percent < 25.0) {
                                holdings.add(new String[] { name, valStr + "%" });
                            }
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
        }
        return holdings;
    }
    // ETF (十大持股)
    public static List<String[]> getETFHoldings(String etfCode) throws IOException {
        String url = "https://www.moneydj.com/ETF/X/Basic/Basic0007.xdjhtm?etfid=" + etfCode;
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").timeout(10000).get();
        List<String[]> holdings = new ArrayList<>();
        Elements rows = doc.select("table.datalist tbody tr");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() >= 2) {
                String name = tds.get(0).text().trim();
                String percent = tds.get(1).text().trim();
                if (!name.isEmpty() && !percent.isEmpty()) {
                    holdings.add(new String[] { name, percent + "%" });
                }
            }
        }
        return holdings;
    }
    // 特殊 PIMCO(十大持股)
    private static List<String[]> getPima3Holdings(String fundCode) throws IOException {
        String url = "https://www.moneydj.com/funddj/yp/yp013001.djhtm?a=" + fundCode;
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        List<String[]> holdings = new ArrayList<>();
        Elements rows = doc.select("table.t01 tr");

        for (Element row : rows) {
            Elements tds = row.select("td");
            if (tds.size() == 2) {
                String name = tds.get(0).text().trim();
                String percent = tds.get(1).text().trim();
                if (!name.equals("投資名稱") && !name.isEmpty()) {
                    holdings.add(new String[] { name, percent });
                }
            }
        }
        return holdings;
    }
    // Fund(績效)
    public static List<String[]> getPerformance(String fundCode, boolean isSpecial) throws IOException {
        String baseUrl = isSpecial ? "https://www.moneydj.com/funddj/yp/yp012001.djhtm?a="
                : "https://www.moneydj.com/funddj/yp/yp012000.djhtm?a=";

        Document doc = Jsoup.connect(baseUrl + fundCode)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .get();

        List<String[]> performanceList = new ArrayList<>();

        Element targetTable = doc.select("table:contains(單筆申購(原幣))").first();
        if (targetTable == null) return performanceList;

        Elements rows = targetTable.select("tr");
        Elements headers = rows.get(1).select("td");

        for (Element row : rows) {
            if (row.text().contains("單筆申購(原幣)")) {
                Elements tds = row.select("td");
                int minSize = Math.min(headers.size(), tds.size());
                for (int i = 1; i < minSize; i++) {
                    String period = headers.get(i).text().trim();
                    String rate = tds.get(i).text().trim();
                    performanceList.add(new String[] { period, rate });
                }
                break;
            }
        }
        return performanceList;
    }
    // ETF(績效)
    public static List<String[]> getETFPerformance(String fundCode) throws IOException {
        String url = "https://www.moneydj.com/ETF/X/Basic/Basic0008.xdjhtm?etfid=" + fundCode;

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        List<String[]> performanceList = new ArrayList<>();

        Element table = doc.getElementById("ctl00_ctl00_MainContent_MainContent_stable");
        if (table == null) return performanceList;

        Elements headers = table.select("tr").first().select("th");
        Element targetRow = table.select("tr:contains(市價)").first();

        if (targetRow != null) {
            Elements tds = targetRow.select("td"); // 該行中的數據欄位
            for (int i = 0; i < tds.size(); i++) {
                String period = headers.get(i + 1).text().trim();
                String rate = tds.get(i).text().trim();
                performanceList.add(new String[] { period, rate });
            }
        }
        return performanceList;
    }
}