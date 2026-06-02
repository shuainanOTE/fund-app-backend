package com.youlin;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import java.util.*;
import java.util.stream.Collectors;

public class Database {
    private static final String BASE_URL =
            "https://vzhlovwzlwghjgarmdcs.supabase.co/rest/v1";
    private static final String API_KEY = System.getenv("SUPABASE_API_KEY");
    private static final RestTemplate restTemplate = new RestTemplate();
    private static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", API_KEY);
        headers.set("Authorization", "Bearer " + API_KEY);
        return headers;
    }
    // 查詢(淨值、日期)
    public static List<Fund> fetchAll() {
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
    // 查詢(ID)
    public static Long fetchFundIdByName(String name) {
        HttpHeaders headers = createHeaders();
        String url = BASE_URL + "/fund?name=eq." + name + "&select=id";

        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {}
            );

            List<Map<String, Object>> body = response.getBody();
            if (body != null && !body.isEmpty()) {
                return Long.valueOf(String.valueOf(body.get(0).get("id")));
            }
        } catch (Exception e) {
            System.err.println("❌ 找不到基金 ID: " + name);
            e.printStackTrace();
        }
        return null;
    }
    // 新增(淨值、日期)
    public static void saveFundData(Fund fund) {
        HttpHeaders headers = createHeaders();
        headers.set("Prefer", "resolution=merge-duplicates");

        try {
            Long fundId = fetchFundIdByName(fund.getName());
            Map<String, Object> body = new HashMap<>();
            body.put("fund_id", fundId);
            body.put("nav", fund.getNav());
            body.put("date", fund.getDate().toString());

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            String priceUrl = "https://vzhlovwzlwghjgarmdcs.supabase.co/rest/v1/price?on_conflict=fund_id,date";
            restTemplate.postForEntity(priceUrl, entity, String.class);

            System.out.println("✅ 成功同步淨值: " + fund.getName());
        } catch (Exception e) {
            System.err.println("❌ 同步失敗: " + e.getMessage());
        }
    }
    // 刪除(十大持股)
    public static void deleteHoldings(Long fundId) {
        HttpHeaders headers = createHeaders();
        String url = BASE_URL + "/holding?fund_id=eq." + fundId;

        try {
            restTemplate.exchange(url, HttpMethod.DELETE, new HttpEntity<>(headers), Void.class);
        } catch (Exception e) {
            System.err.println("❌ 刪除持股失敗: " + e.getMessage());
        }
    }
    // 新增(十大持股)
    public static void saveHoldings(Long fundId, List<String[]> holdings) {
        HttpHeaders headers = createHeaders();
        headers.set("Prefer", "return=minimal");
        List<Map<String, Object>> batch = new ArrayList<>();

        for (String[] h : holdings) {
            Map<String, Object> row = new HashMap<>();
            row.put("fund_id", fundId);
            row.put("stock", h[0]);
            try {
                String cleanPercent = h[1].replaceAll("[^0-9.]", "");
                row.put("percent", Double.parseDouble(cleanPercent));
                batch.add(row);
            } catch (NumberFormatException e) {
                System.err.println("⚠️ 略過無法解析的比例資料: " + h[0] + " -> " + h[1]);
            }
        }

        try {
            String url = BASE_URL + "/holding";
            restTemplate.postForEntity(url, new HttpEntity<>(batch, headers), String.class);
        } catch (Exception e) {
            System.err.println("❌ 儲存持股失敗: " + e.getMessage());
        }
    }
    // 查詢(十大持股)
    public static List<FundDTO.HoldingDTO> fetchHoldingsByFundId(Long fundId) {
        RestTemplate restTemplate = new RestTemplate();
        String url = BASE_URL + "/holding?fund_id=eq." + fundId + "&select=stock,percent";
        ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                url, HttpMethod.GET, new HttpEntity<>(createHeaders()),
                new ParameterizedTypeReference<List<Map<String, Object>>>() {}
        );

        return response.getBody().stream()
                .map(m -> new FundDTO.HoldingDTO(
                        m.get("stock").toString(),
                        Double.parseDouble(m.get("percent").toString())
                )).collect(Collectors.toList());
    }
    // 刪除、新增(績效)
    public static void savePerformanceData(int fundId, List<String[]> dataList) {
        try {
            String deleteUrl = BASE_URL + "/performance?fund_id=eq." + fundId;
            HttpEntity<String> deleteEntity = new HttpEntity<>(createHeaders());
            restTemplate.exchange(deleteUrl, HttpMethod.DELETE, deleteEntity, String.class);

            List<Map<String, Object>> payload = new ArrayList<>();
            for (String[] data : dataList) {
                Map<String, Object> row = new HashMap<>();
                row.put("fund_id", fundId);
                row.put("period", data[0]);
                double rate = data[1].equalsIgnoreCase("N/A") ? 0.0 : Double.parseDouble(data[1].replace(",", ""));
                row.put("rate", rate);
                payload.add(row);
            }

            String insertUrl = BASE_URL + "/performance";
            HttpEntity<List<Map<String, Object>>> insertEntity = new HttpEntity<>(payload, createHeaders());
            restTemplate.postForEntity(insertUrl, insertEntity, String.class);

            System.out.println("✅ 成功同步績效至 Supabase: fund_id=" + fundId);

        } catch (Exception e) {
            System.err.println("❌ 資料庫寫入失敗: " + e.getMessage());
        }
    }
    // 查詢(績效)
    public static List<FundDTO.PerformanceDTO> fetchPerformanceByFundId(Long fundId) {
        String url = BASE_URL + "/performance?fund_id=eq." + fundId + "&select=period,rate";
        HttpHeaders headers = createHeaders();
        try {
            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers),
                    new ParameterizedTypeReference<List<Map<String, Object>>>() {});

            List<FundDTO.PerformanceDTO> list = new ArrayList<>();
            if (response.getBody() != null) {
                for (Map<String, Object> map : response.getBody()) {
                    list.add(new FundDTO.PerformanceDTO(
                            (String) map.get("period"),
                            Double.parseDouble(String.valueOf(map.get("rate")))
                    ));
                }
            }
            return list;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}