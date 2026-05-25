package com.youlin;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.util.*;

public class Database {
    private static final String BASE_URL = "https://vzhlovwzlwghjgarmdcs.supabase.co/rest/v1/funds";
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
        // 確保衝突時執行更新
        headers.set("Prefer", "resolution=merge-duplicates");

        try {
            Map<String, Object> body = new HashMap<>();
            body.put("name", fund.getName());
            // 這裡改成新的欄位名稱 nav
            body.put("nav", fund.getNav());
            // updateDate 已經是 LocalDate，這裡直接用它
            body.put("update_date", fund.getUpdateDate().toString());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // POST 到 Supabase，on_conflict 指定為 name 與 update_date
            restTemplate.postForEntity(BASE_URL + "?on_conflict=name,update_date", entity, String.class);

            System.out.println("✅ 成功同步: " + fund.getName() + " (日期: " + fund.getUpdateDate() + ")");
        } catch (Exception e) {
            System.err.println("❌ 同步失敗: " + e.getMessage());
        }
    }

    public static List<Fund> fetchAll() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();

        try {
            ResponseEntity<Fund[]> response = restTemplate.exchange(
                    BASE_URL + "?select=*", HttpMethod.GET, new HttpEntity<>(headers), Fund[].class);

            return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ API 撈取失敗: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}