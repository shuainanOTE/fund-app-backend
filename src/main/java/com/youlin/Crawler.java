package com.youlin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import java.io.IOException;

public class Crawler {
    public static String[] getFund(String pageCode, String fundCode) throws IOException {
        String url = "https://www.moneydj.com/funddj/ya/" + pageCode + ".djhtm?a=" + fundCode;
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        Elements tds = doc.select(".t01").first().select("tr").get(1).select("td");

        String date = tds.get(0).text();
        String nav = tds.get(1).text();

        return new String[] { date, nav };
    }
}