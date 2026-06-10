# 📈 Fund & ETF Mobile Dashboard

一個用 React + Spring Boot 寫的基金與 ETF 看盤 PWA。
純手機畫面優化，前後端與資料庫已全部部署到雲端，點開就能用，不需要在本地端安裝任何環境。

<img width="370" height="445" alt="S__43327493" src="https://github.com/user-attachments/assets/f5bab112-63cc-4578-979d-b8ed8db8819c" />

---

## 🔗 傳送門 (Live Demo)

*   **🎨 前端網頁 (PWA)**：https://fund-app-frontend.vercel.app
    *   *📌 備註：本專案完全針對**手機畫面**開發。如果用電腦瀏覽器點開，請按 `F12` 切換成**手機檢視模式**，不然畫面會變形。*
    *   *💡 提示：用手機打開時，選擇「新增至主畫面」，用起來就跟一般 App 差不多。*
    
*   **☕ 後端 Swagger API**：https://fund-app-backend-9wbm.onrender.com/swagger-ui/index.html
    *   *點進去可以直接點 `Try it out` 測試 API，會直接去撈雲端資料庫的即時數據。*
 
*   前端 GitHub 連結：``https://github.com/shuainanOTE/fund-app-frontend``
*   後端 GitHub 連結：``https://github.com/shuainanOTE/fund-app-backend``

---

## 💡 為什麼想做這個？

平常買股票很容易找到一堆免費的公開 API，但想要追蹤**共同基金**時，發現市場上幾乎找不到免費又好用的 API。每次要看自己的投資組合，都得開好幾個不同的銀行或基金 App 一直切換。

既然市場不給，那就自己寫。我寫了一個爬蟲，把分散在財經網站的基金與 ETF 淨值、績效表現、投資持股明細抓下來結構化，再用 React 做成一個自己專用的手機看盤看板，解決自己的問題。

---

## 🛠️ 技術棧與雲端配置

因為不想只做一個「只能在 localhost 跑的玩具」，所以我把所有服務都雲端化了：

*   **前端**(**Vercel**)： React 18 / Tailwind CSS
*   **後端**(**Render**)： Java 21 / Spring Boot 3.3.6
*   **資料庫**(**Supabase**)： PostgreSQL
*   **排程/監控**： **Cron-Job**

---

## 🗺️ 系統架構 (System Architecture)

專案採用前後端分離架構，並將所有服務、資料庫完整託管於雲端平台。整體資料流與架構對稱設計如下：

```text
                     ┌───────────────────────┐
                     │   📱 User (Mobile)    │
                     └───────────────────────┘
                                 │
                    (Service Worker Offline Cache)
                                 │
                                 ▼
                     ┌───────────────────────┐
                     │  🎨 Frontend: Vercel  │
                     └───────────────────────┘
                                 │
                       (HTTPS / RESTful API)
                                 │
                                 ▼
                     ┌───────────────────────┐
                     │  ☕ Backend: Render   │
                     └───────────────────────┘
                        │                 │
                (標準 JDBC 連線)       (Jsoup 網路爬蟲)
                        ▼                 ▼
             ┌────────────────┐     ┌────────────────┐
             │  💾 Supabase   │     │  🌐 外部財經   │
             │  (PostgreSQL)  │     │   (數據來源)    │
             └────────────────┘     └────────────────┘
```
## 👤 作者資訊
YOU LIN HOU（侯佑霖）
製作時間：3 星期
