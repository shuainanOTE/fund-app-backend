package com.youlin;

import java.io.IOException;
import java.util.List;

import static com.youlin.Crawler.getPerformance;

public class Test {
    public static void main(String[] args) {
        try {
            // 測試標的列表
            String[][] testTargets = {
                    {"ACKH03", "false"},
                    {"PIMA3", "true"}
            };

            for (String[] target : testTargets) {
                String code = target[0];
                boolean isSpecial = Boolean.parseBoolean(target[1]);

                System.out.println("--- 測試標的: " + code + " (特殊: " + isSpecial + ") ---");

                // 正確呼叫 Crawler
                List<String[]> results = Crawler.getPerformance(code, isSpecial);

                if (results.isEmpty()) {
                    System.out.println("⚠️ 警告: 未抓取到任何資料。");
                } else {
                    for (String[] p : results) {
                        System.out.println("區間: " + p[0] + " | 報酬率: " + p[1]);
                    }
                }

                System.out.println("\n--- 測試 ETF: 00981A.TW ---");
                List<String[]> etfPerf = Crawler.getETFPerformance("00981A.TW");
                for (String[] p : etfPerf) {
                    System.out.println("區間: " + p[0] + " | 報酬率: " + p[1]);
                }

                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}