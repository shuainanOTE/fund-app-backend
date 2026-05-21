package com.youlin;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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

        // 1. 設定 Header 告訴 Supabase 遇到重複請合併
        headers.set("Prefer", "resolution=merge-duplicates");

        try {
            // 2. 關鍵：在 URL 後面加上 on_conflict=name
            // 這樣 Supabase 才知道要用 name 這個欄位來判斷重複
            String url = BASE_URL + "?on_conflict=name";

            HttpEntity<Fund> entity = new HttpEntity<>(fund, headers);
            restTemplate.postForEntity(url, entity, String.class);

            System.out.println("✅ 成功寫入/更新資料: " + fund.getName());
        } catch (Exception e) {
            System.err.println("❌ 寫入失敗: " + e.getMessage());
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