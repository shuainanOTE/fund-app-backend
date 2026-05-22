package com.youlin;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

public class Database {
    // 你的 Supabase API 基礎路徑
    private static final String BASE_URL = "https://vzhlovwzlwghjgarmdcs.supabase.co/rest/v1/funds";

    // 請將這裡換成新的 Service Role Key (Generate 後的)
    private static final String API_KEY = System.getenv("SUPABASE_API_KEY");

    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", API_KEY);
        headers.set("Authorization", "Bearer " + API_KEY);
        return headers;
    }


    public static void saveFundData(Fund fund) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();
        // 關鍵：告訴 Supabase 如果衝突了，請執行更新 (on_conflict=name,update_date)
        headers.set("Prefer", "resolution=merge-duplicates");

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", fund.getName());
            body.put("nav_today", fund.getNav_today());
            body.put("update_date", fund.getDate());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 使用 POST，因為有了複合唯一鍵，Supabase 會自動處理更新
            restTemplate.postForEntity(BASE_URL + "?on_conflict=name,update_date", entity, String.class);

            System.out.println("✅ 成功同步: " + fund.getName() + " (日期: " + fund.getDate() + ")");
        } catch (Exception e) {
            System.err.println("❌ 同步失敗: " + e.getMessage());
        }
    }

    public static List<Fund> fetchAll() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();

        try {
            // 使用 select=* 撈取所有資料
            ResponseEntity<Fund[]> response = restTemplate.exchange(
                    BASE_URL + "?select=*", HttpMethod.GET, new HttpEntity<>(headers), Fund[].class);

            return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ API 撈取失敗: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}