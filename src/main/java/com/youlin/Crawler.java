package com.youlin;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;

public class Crawler {
    public static String getFundNav(String fundCode) throws IOException {
        String url = "https://www.moneydj.com/funddj/ya/yp010000.djhtm?a=" + fundCode;
        Document doc = Jsoup.connect(url).get();
        return doc.select(".t01").first().select("tr").get(1).select("td").get(1).text();
    }
}