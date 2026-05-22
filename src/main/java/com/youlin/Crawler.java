package com.youlin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class Crawler {
    public static String getFundNav(String pageCode, String fundCode) throws IOException {
        String url = "https://www.moneydj.com/funddj/ya/" + pageCode + ".djhtm?a=" + fundCode;
        Document doc = Jsoup.connect(url).userAgent("Mozilla/5.0").get();
        return doc.select(".t01").first().select("tr").get(1).select("td").get(1).text();
    }
}