package com.youlin;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;

public class Database {
    private static final String BASE_URL =
            "https://vzhlovwzlwghjgarmdcs.supabase.co/rest/v1";
    private static final String API_KEY = System.getenv("SUPABASE_API_KEY");

    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", API_KEY);
        headers.set("Authorization", "Bearer " + API_KEY);
        return headers;
    }

    public static List<Fund> fetchAll() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();

        try {
            ResponseEntity<Fund[]> response = restTemplate.exchange(
                    BASE_URL + "/fund?select=name,price(nav,date)&price.order=date.desc", HttpMethod.GET, new HttpEntity<>(headers), Fund[].class);

            return response.getBody() != null ? Arrays.asList(response.getBody()) : new ArrayList<>();
        } catch (Exception e) {
            System.err.println("❌ API 撈取失敗: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public static Long fetchFundIdByName(String name) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();
        String url = BASE_URL + "/fund?name=eq." + name + "&select=id";

        try {
            // 使用 ParameterizedTypeReference 來明確告知型別
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                // 轉換時使用 String.valueOf 再轉 Long，避免型別轉換異常
                return Long.valueOf(String.valueOf(body.get(0).get("id")));
            }
        } catch (Exception e) {
            System.err.println("❌ 找不到基金 ID: " + name);
            e.printStackTrace();
        }
        return null;
    }

    public static void saveFundData(Fund fund) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = createHeaders();
        headers.set("Prefer", "resolution=merge-duplicates");

        try {
            // 1. 先確認 fund 存在，取得 ID (或者你已經有 ID)
            // 這裡假設你已經知道基金名稱對應的 ID，或者透過 API 先查詢
            Long fundId = fetchFundIdByName(fund.getName());

            // 2. 準備存入 price 表的資料
            Map<String, Object> body = new HashMap<>();
            body.put("fund_id", fundId); // 這是最重要的連結！
            body.put("nav", fund.getNav());
            body.put("date", fund.getDate().toString());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // 注意：這裡 URL 要指向 price 表，而不是 fund 表
            String priceUrl = "https://vzhlovwzlwghjgarmdcs.supabase.co/rest/v1/price?on_conflict=fund_id,date";
            restTemplate.postForEntity(priceUrl, entity, String.class);

            System.out.println("✅ 成功同步淨值: " + fund.getName());
        } catch (Exception e) {
            System.err.println("❌ 同步失敗: " + e.getMessage());
        }
    }



}